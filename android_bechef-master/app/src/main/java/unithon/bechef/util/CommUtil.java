package unithon.bechef.util;

import android.content.Context;
import android.util.Log;
import okhttp3.ResponseBody;

import java.io.*;
import java.util.Date;

public class CommUtil {
    final static String TAG = "CoomUtil";

    public String writeResponseBodyToDisk(ResponseBody body, String ext, Context context) {
        String filePath = context.getExternalFilesDir(null) + File.separator + Long.valueOf(new Date().getTime()).toString() + "." + ext;
        try {
            // todo change the file location/name according to your needs

            File futureStudioIconFile = new File(filePath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return filePath;
            } catch (IOException e) {
                Log.e(TAG, "buffer", e);
                return "";
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "whole", e);
            return "";
        }
    }
}
