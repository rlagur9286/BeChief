package unithon.bechef.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unithon.bechef.util.conf.Conf;
import unithon.bechef.util.mapviewer.NMapPOIflagType;
import unithon.bechef.util.mapviewer.NMapViewerResourceProvider;
import unithon.bechef.util.trans.object.BechefLIst;
import unithon.bechef.util.trans.object.Locations;
import unithon.bechef.util.trans.object.location;
import unithon.bechef.util.trans.service.BechefApiClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends NMapActivity {
    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = Conf.NaverMapAPIClient;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapOverlayManager mOverlayManager;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");

        mMapView = new NMapView(this);
        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        SendImg sendImg = new SendImg();
        sendImg.execute();

    }

    private class SendImg extends AsyncTask<Void, Void, Locations> {

        @Override
        protected Locations doInBackground(Void... voids) {
            Locations lc;
            BechefApiClient bechefApiClient = BechefApiClient.retrofit.create(BechefApiClient.class);
            try {
                lc = bechefApiClient.getKit("126.9496123", "37.5466706", keyword).execute().body();
                return lc;
            } catch (IOException e) {
                Log.e("test", "test", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Locations lcs) {
            int markerId = NMapPOIflagType.PIN;
            Log.d("test", lcs.toString());
            ArrayList<location> lc = lcs.getResult();
            NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
            poiData.beginPOIdata(2);

            for (int i = 0; i < lc.size(); i++) {
                poiData.addPOIitem(Double.parseDouble(lc.get(i).getMapx()), Double.parseDouble(lc.get(i).getMapy()), lc.get(i).getTitle(), markerId, 0);

            }

            poiData.endPOIdata();
            Log.d("test", poiData.toString());
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
            poiDataOverlay.showAllPOIdata(0);

        }
    }

    private Map<String, Object> getLocation() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = lm.getProviders(true);

        Location bestLocation = null;

        Map<String, Object> map = new HashMap<>();

        for (String provider : providers) {
            Location location = lm.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }

            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = location;
                map.put("lng", bestLocation.getLongitude());
                map.put("lat", bestLocation.getLatitude());
                Log.d("login", bestLocation.getLongitude() + " " + bestLocation.getLatitude());
                return map;
            }
        }

        if (bestLocation == null) {
            Log.d("login", "is NULL!");
        }
        return null;
    }

}
