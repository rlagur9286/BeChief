from bs4 import BeautifulSoup
import urllib.request
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
import logging
import urllib.parse
import http.client
import urllib.parse
import urllib.error
import urllib.request
import json
#from database import LikeManager
from django import forms
import os


logging.basicConfig(
    format="[%(name)s][%(asctime)s] %(message)s",
    handlers=[logging.StreamHandler()],
    level=logging.INFO
)
logger = logging.getLogger(__name__)
logger.setLevel(level=logging.DEBUG)

RECIPE_URL = 'http://www.10000recipe.com'
UPLOAD_FOLDER = 'upload/'
ALLOWED_FORMAT = ['png', 'jpg', 'jpeg', 'gif', 'JPG', 'PNG']


@csrf_exempt
def manage_recipt(request, option=None):
    logger.debug(request)
    if request.method == 'POST':
        if option is None:
            try:
                # USER INFO from APP
                data = json.loads(request.body.decode('utf-8'))
                logger.debug("INPUT %s", data)

                return JsonResponse({'success': True, 'data': data})

            except Exception as exp:
                logger.exception(exp)
                return JsonResponse({'success': False, 'reason': 'INTERNAL SERVER ERROR'})

        elif option == 'list':  # get Recipt list
            try:
                result_set = dict()
                file = request.FILES.get('img')
                if file is None:
                    return JsonResponse({'success': False, 'result': '이미지 넣어주세요'})
                if allowed_file(str(file)):
                    save_file(file)
                    img_path = './unithon_server/upload/%s' %str(file)
                    keyword = classify(picture=img_path)
                    url_list = get_url_list(keyword=keyword)
                    result_set['result_set'] = url_list
                    print('keyword : ', keyword)
                    if not url_list:
                        print('\'' + keyword + "\'", "에 대한 검색결과가 없습니다.")
                        return JsonResponse({'success': False, 'result': '그런 음식 없습니다'})
                    else:
                        return JsonResponse({'success': True, 'result': result_set, 'keyword': keyword})

                else:
                    return JsonResponse({'success': False, 'result': '[%s] 파일 형식 확인해 주세요' % file.filename})

            except Exception as exp:
                logger.exception(exp)
                return JsonResponse({'success': False})

    if request.method == 'GET':
        if option == 'detail':
            try:
                result_set = dict()
                data = json.loads(json.dumps(request.GET))
                logger.debug("INPUT : %s", data)
                url = data.get('url')
                if url is None:
                    return JsonResponse({'success': False, 'result': 'URL이 없습니다.'})

                step_text_list, step_img_src_list, ingredient_list = get_recipe(url)
                result_set['texts'] = step_text_list
                result_set['imgs'] = step_img_src_list
                result_set['ingre'] = ingredient_list
                return JsonResponse({'success': True, 'result': result_set})

            except Exception as exp:
                logger.exception(exp)
                return JsonResponse({'success': False})

        elif option == 'like':
            try:
                like_db = LikeManager()
                data = json.loads(json.dumps(request.GET))
                logger.debug("INPUT : %s", data)
                keyword = data.get('keyword')
                img = data.get('img')
                user_id = data.get('user_id')
                result = like_db.insert_keyword2tb(img=img, keyword=keyword, user_id=user_id)
                if not result:
                    return JsonResponse({'success': False, 'result': result})
                return JsonResponse({'success': True, 'result': '성공!'})

            except Exception as exp:
                logger.exception(exp)
                return JsonResponse({'success': False})

        elif option == 'find_kitchen':
            try:
                data = json.loads(json.dumps(request.GET))
                logger.debug("INPUT : %s", data)
                lat = data.get('lat')
                lng = data.get('lng')
                keyword = data.get('keyword')

                if not lat or not lng or not keyword:
                    return JsonResponse({'success': False, 'result': '문언가 없습니다.'})

                result = get_hot_place(lat=lat, lng=lng, keyword=keyword)
                print('result : ', result)
                if result is None:
                    return JsonResponse({'success': False, 'result': '검색 실패.'})

                return JsonResponse({'success': True, 'result': result})

            except Exception as exp:
                logger.exception(exp)
                return JsonResponse({'success': False})


def get_url_list(keyword):
    url_list = []
    params = urllib.parse.urlencode({
        'q': keyword,
    })
    source_code_from_url = urllib.request.urlopen(RECIPE_URL + "/recipe/list.html?" + params)
    soup = BeautifulSoup(source_code_from_url, 'lxml', from_encoding='utf-8')
    if soup.find_all('div', {'class': 'result_none'}):
        return None
    for page in soup.find_all('div', {"class": "col-xs-4"})[:10]:
        tmp = dict()
        tmp['url'] = page.find('a')['href']
        tmp['title'] = page.find('a').find('div', {'class': 'caption'}).find('h4').text
        tmp['img'] = page.find('a').find_all('img')[1].get('src')
        url_list.append(tmp)
#    print(url_list)
    return url_list


def get_recipe(url):
    step_text_list = []
    step_img_src_list = []
    ingredient_list = []
    try:
        source_code_from_url = urllib.request.urlopen(RECIPE_URL + url)
        soup = BeautifulSoup(source_code_from_url, 'lxml', from_encoding='utf-8')
        lists = soup.find('div', {"id": "divConfirmedMaterialArea"}).find_all('li')
        for ingredient in lists:
            try:
                tmp = dict()
                tmp['ingre_name'] = ingredient.text.split()[0]
                tmp['cnt'] = ingredient.find('span', {'class': 'ingre_unit'}).text
            except:
                continue
            ingredient_list.append(tmp)
        for step in range(0, 20):
            for page in soup.find_all('div', {"id": "stepDiv"+str(step)}):
                step_text_list.append(page.text)
                if page.find_all('div', {"id": "stepimg"+str(step)}):
                    step_img_src_list.append(page.find_all('div', {"id": "stepimg" + str(step)})[0].find_all("img")[0].get('src'))
        return step_text_list, step_img_src_list, ingredient_list
    except Exception as e:
        print(e)
        return e


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_FORMAT


def classify(picture):
    headers = {
        'Prediction-Key': '73fe73f82f5e4db1ac5657c02204e171',
        'Content-Type': 'application/json',
    }
    params = urllib.parse.urlencode({
        # Request parameters
        'iterationId': '05d76db2-bc53-42f6-a3bb-b8d535f0a6f8',
    })
    data = open(picture, "rb").read(10000000)

    try:
        conn = http.client.HTTPSConnection('southcentralus.api.cognitive.microsoft.com')
        conn.request("POST", "/customvision/v1.0/Prediction/04ab6738-919b-44ca-bea9-84c865571687/image?%s" % params, data, headers)
        response = conn.getresponse()
        json_string = response.read().decode('utf-8')
        json_obj = json.loads(json_string)
        item = json_obj.get('Predictions')
        conn.close()
        if item is None:
            return False
        # if item[0].get('Probability') > 0.5:
        return item[0].get('Tag')
        # return False
    except Exception as e:
        print('error : ', e)
        return None


def save_file(file):
    filename = file._get_name()
    print(os.getcwd())
    fd = open('./unithon_server/upload/%s' % str(filename), 'wb')
    for chunk in file.chunks():
        fd.write(chunk)
    fd.close()


def get_product(query=None):
    client_id = "JxBeGGLiXDh9_BK94zp7"
    client_secret = "ogU4D772CJ"
    encText = urllib.parse.quote(query)
    url = "https://openapi.naver.com/v1/search/shop.json?query=" + encText  # json 결과
    # url = "https://openapi.naver.com/v1/search/blog.xml?query=" + encText # xml 결과
    request = urllib.request.Request(url)
    request.add_header("X-Naver-Client-Id", client_id)
    request.add_header("X-Naver-Client-Secret", client_secret)
    response = urllib.request.urlopen(request)
    rescode = response.getcode()
    if (rescode == 200):
        response_body = response.read()
        print(response_body.decode('utf-8'))
    else:
        print("Error Code:" + rescode)


def get_hot_place(lat, lng, keyword):
    result = {}
    client_id = "JxBeGGLiXDh9_BK94zp7"
    client_secret = "ogU4D772CJ"
    latlng = 'latlng&query=%s,%s' % (lat, lng)
    url2position = "https://openapi.naver.com/v1/map/reversegeocode?encoding=utf-8&coordType=" + latlng  # json 결과
    request = urllib.request.Request(url2position)
    request.add_header("X-Naver-Client-Id", client_id)
    request.add_header("X-Naver-Client-Secret", client_secret)
    response = urllib.request.urlopen(request)
    rescode = response.getcode()
    if (rescode == 200):
        response_body = response.read().decode('utf-8')
        json_obj = json.loads(response_body)
        region = json_obj.get('result').get('items')[0].get('addrdetail').get('dongmyun')
        encText = urllib.parse.quote(region + '/' + keyword)
        url2query = "https://openapi.naver.com/v1/search/local.json?query=" + encText  # json 결과
        request = urllib.request.Request(url2query)
        request.add_header("X-Naver-Client-Id", client_id)
        request.add_header("X-Naver-Client-Secret", client_secret)
        response = urllib.request.urlopen(request)
        rescode = response.getcode()
        if (rescode == 200):
            response_body = response.read().decode('utf-8')
            json_obj = json.loads(response_body)
#	        result_list = list()

            result_list = []

            for item in json_obj.get('items'):
                tmp = dict()
                tmp['title'] = item.get('title')
                tmp['mapx'] = item.get('mapx')
                tmp['mapy'] = item.get('mapy')
                result_list.append(tmp)
            return result_list
    else:
        print("Error Code:" + rescode)


if __name__ == '__main__':
    # get_url_list(keyword='돼지불고기')
    # get_product(query='당근')
    result = get_hot_place(lat=127.1052133, lng=37.3595316, keyword='피자')
    print(result)
