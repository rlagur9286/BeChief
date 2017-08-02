package unithon.bechef.util.trans.service;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import unithon.bechef.util.trans.object.BechefDetailList;
import unithon.bechef.util.trans.object.BechefLIst;
import unithon.bechef.util.trans.object.Locations;
import unithon.bechef.util.trans.object.location;

public interface BechefApiClient {

    @Multipart
    @POST("/api/recipt/list")
    Call<BechefLIst> saveImg(
            @Part MultipartBody.Part file);

    @GET("/api/recipt/detail")
    Call<BechefDetailList> getDetailChef(
            @Query("url") String url
    );

    @GET("/api/recipt/find_kitchen")
    Call<Locations> getKit(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("keyword") String keyword
    );

    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.124.45.64:8000/").addConverterFactory(GsonConverterFactory.create()).build();

}

