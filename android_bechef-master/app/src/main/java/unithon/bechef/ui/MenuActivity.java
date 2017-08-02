package unithon.bechef.ui;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Response;
import unithon.bechef.R;
import unithon.bechef.util.CommUtil;
import unithon.bechef.util.trans.NaverServiceGenerator;
import unithon.bechef.util.trans.object.BookList;
import unithon.bechef.util.trans.service.GoogleApiClient;
import unithon.bechef.util.trans.service.NaverApiClient;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    private boolean mKillFlag = false;
    private Handler mKillHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        testAsyncTask testasyncTask = new testAsyncTask();
        testasyncTask.execute("최나나 누나 배고프고 춥고 힘들어요");

        mKillHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mKillFlag = false;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (!mKillFlag) {
            Toast.makeText(this, "앱을 종료하시겠습니까?",
                    Toast.LENGTH_SHORT).show();
            mKillFlag = true;
            mKillHandler.sendEmptyMessageDelayed(0, 2000);

        } else {
            finishApp();
        }
    }

    protected void finishApp() {
        finish();
    }

    private class testAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            NaverApiClient naverApiClient = NaverServiceGenerator.createService(NaverApiClient.class);
            CommUtil commUtil = new CommUtil();
            try {
                return commUtil.writeResponseBodyToDisk(naverApiClient.getVoiceData("mijin", 0, strings[0]).execute().body(), "mp3", getApplicationContext());
            } catch (IOException e) {
                Log.e("menu", "error", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("menu", result);
            if (result != null) {

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(result);
                    FileDescriptor fd = fis.getFD();
                    mediaPlayer.setDataSource(fd);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("menu", "error", e);

                }


            }
        }
    }

}
