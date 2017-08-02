package unithon.bechef.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import unithon.bechef.R;
import unithon.bechef.util.CommUtil;
import unithon.bechef.util.trans.NaverServiceGenerator;
import unithon.bechef.util.trans.object.BechefDetailList;
import unithon.bechef.util.trans.object.Bechef_rec;
import unithon.bechef.util.trans.object.Ingre;
import unithon.bechef.util.trans.service.BechefApiClient;
import unithon.bechef.util.trans.service.NaverApiClient;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private String url;
    private String img;
    private String title;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ArrayList<String> texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = (LinearLayout) findViewById(R.id.food_ln);
        linearLayout1 = (LinearLayout) findViewById(R.id.food_lngre);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        img = intent.getStringExtra("img");
        title = intent.getStringExtra("title");


        collapsingToolbarLayout.setTitle(title);
        Picasso.with(this).load(img).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                appBarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));

            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });

        sendAsyncTask sendAsyncTasks = new sendAsyncTask();
        sendAsyncTasks.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_added, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tts:
                testAsyncTask test = new testAsyncTask();
                test.execute(texts);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class testAsyncTask extends AsyncTask<ArrayList<String>, Void, Void> {


        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {
            ArrayList<String> goPlay = arrayLists[0];
            NaverApiClient naverApiClient = NaverServiceGenerator.createService(NaverApiClient.class);
            CommUtil commUtil = new CommUtil();
            for (int i = 0; i < goPlay.size(); i++) {
                try {
                    String result = commUtil.writeResponseBodyToDisk(naverApiClient.getVoiceData("mijin", 0, goPlay.get(i)).execute().body(), "mp3", getApplicationContext());
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
                } catch (IOException e) {
                    Log.e("menu", "error", e);
                }
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    Log.e("menu", "error", e);
                }
            }

            return null;
        }

    }

    private class sendAsyncTask extends AsyncTask<Void, Void, BechefDetailList> {

        @Override
        protected BechefDetailList doInBackground(Void... voids) {
            BechefApiClient bechefApiClient = BechefApiClient.retrofit.create(BechefApiClient.class);
            try {
                BechefDetailList bechefDetailList = bechefApiClient.getDetailChef(url).execute().body();
                return bechefDetailList;
            } catch (Exception e) {
                Log.e("test", "abc", e);
            }

            return null;
        }

        protected void onPostExecute(BechefDetailList bechefDetailList) {
            Bechef_rec detail = bechefDetailList.getResult();

            texts = detail.getTexts();
            ArrayList<String> imgs = detail.getImgs();
            ArrayList<Ingre> ingres = detail.getIngre();

            for (int i = 0; i < texts.size(); i++) {
                View layout2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.content_detail_img_item, linearLayout, false);

                TextView desc = (TextView) layout2.findViewById(R.id.detail_desc);
                ImageView img = (ImageView) layout2.findViewById(R.id.detail_img);
                TextView no = (TextView) layout2.findViewById(R.id.detail_no);

                try {
                    if (texts.get(i) != null)
                        desc.setText(texts.get(i));
                } catch (Exception e) {
                    Log.e("test", "out", e);
                }

                try {
                    if (imgs.get(i) != null)
                        Picasso.with(getApplicationContext()).load(imgs.get(i)).into(img);
                } catch (Exception e) {
                    Log.e("test", "out", e);
                }


                no.setText(String.valueOf(i + 1));

                linearLayout.addView(layout2);
            }

            for (int i = 0; i < ingres.size(); i++) {
                View layout3 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.content_detail_img_ingre_item, linearLayout1, false);

                TextView unit = (TextView) layout3.findViewById(R.id.ingre_unit);
                TextView name = (TextView) layout3.findViewById(R.id.ingre_name);

                try {
                    if (ingres.get(i).getCnt() != null)
                        unit.setText(ingres.get(i).getCnt());
                } catch (Exception e) {
                    Log.e("test", "out", e);
                }

                try {
                    if (ingres.get(i).getIngre_name() != null)
                        name.setText(ingres.get(i).getIngre_name());
                } catch (Exception e) {
                    Log.e("test", "out", e);
                }


                linearLayout1.addView(layout3);
            }

        }
    }
}
