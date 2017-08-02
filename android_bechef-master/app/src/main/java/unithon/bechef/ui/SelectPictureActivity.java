package unithon.bechef.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.felipecsl.gifimageview.library.GifImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unithon.bechef.R;
import unithon.bechef.util.trans.GifDataDownloader;
import unithon.bechef.util.trans.object.BechefLIst;
import unithon.bechef.util.trans.service.BechefApiClient;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPictureActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_FROM_ALBUM = 2;

    private ImageView mImageView;
    private ImageView take_a_photo_btn;
    private ImageView pick_a_photo_btn;
    private Toolbar mToolbar;
    private String mCurrentPhotoPath;
    private View mProgressView;
    private View mPicView;
    private GifImageView gifImageView;

    private static String TAG = "test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        gifImageView = (GifImageView) findViewById(R.id.activity_select_progress);
        mPicView = findViewById(R.id.select_form);
        mProgressView = findViewById(R.id.activity_select_progress);
        mImageView = (ImageView) findViewById(R.id.activity_select_main01);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent1);
            }
        });



        take_a_photo_btn = (ImageView) findViewById(R.id.activity_select_main03);
        take_a_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        pick_a_photo_btn = (ImageView) findViewById(R.id.activity_select_main04);
        pick_a_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToAlbum();
                Toast.makeText(getApplicationContext(), "미구현 기능입니다.", 1000).show();
            }
        });

        new GifDataDownloader() {
            @Override
            protected void onPostExecute(final byte[] bytes) {
                gifImageView.setBytes(bytes);
                gifImageView.startAnimation();
                Log.d(TAG, "GIF width is " + gifImageView.getGifWidth());
                Log.d(TAG, "GIF height is " + gifImageView.getGifHeight());
            }
        }.execute("http://teamnexters.com/loading.gif");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        gifImageView.startAnimation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPicView.setVisibility(View.VISIBLE);
            mPicView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mPicView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPicView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "unithon.bechef.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //ACTION_PICK 즉 사진을 고르겠다!
        //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SendImg sendImg = new SendImg();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            sendImg.execute();
        } else if (requestCode == PICK_FROM_ALBUM) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                Log.d("test", uri.toString());

                mCurrentPhotoPath = uri.getPath();
                sendImg.execute();

            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private class SendImg extends AsyncTask<Void, Void, BechefLIst> {
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected BechefLIst doInBackground(Void... voids) {

            Log.d("test", mCurrentPhotoPath);

            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap = resizeBitmap(file.getAbsolutePath(), 1344, 1008);
            try {

                FileOutputStream out = new FileOutputStream(file.getAbsolutePath());

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

            } catch (FileNotFoundException exception) {
                Log.e("FileNotFoundException", exception.getMessage());
            } catch (IOException exception) {
                Log.e("IOException", exception.getMessage());
            }


            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            BechefApiClient bechefApiClient = BechefApiClient.retrofit.create(BechefApiClient.class);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("img", file.getName(), requestFile);
            Call<BechefLIst> resultCall = bechefApiClient.saveImg(fileToUpload);

            try {
                resultCall.enqueue(new Callback<BechefLIst>() {
                    @Override
                    public void onResponse(Call<BechefLIst> call, Response<BechefLIst> response) {

                        //progressDialog.dismiss();

                        // Response Success or Fail
                        if (response.isSuccessful()) {
                            Log.d("test", response.body().toString());
                            Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                            BechefLIst bechefLIst = response.body();
                            intent.putExtra("datas", bechefLIst);
                            intent.putExtra("keyword", bechefLIst.getKeyword());
                            startActivity(intent);
                        } else {
                            Log.d("test", String.valueOf(response.code()));
                            Toast.makeText(getApplicationContext(), "서버가 동작하지 않습니다.", 2000);
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(Call<BechefLIst> call, Throwable t) {
                        Log.e("test", "failed", t);
                        showProgress(false);
                        //progressDialog.dismiss();
                    }
                });

            } catch (Exception e) {
                Log.e("test", "uploading", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BechefLIst bechefLIst) {
            //Log.d("test", bechefLIst.toString());
        }
    }

    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

}
