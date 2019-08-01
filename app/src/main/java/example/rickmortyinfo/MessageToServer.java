package example.rickmortyinfo;

/**
 * Created by Draic0 on 01.08.2019.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageToServer extends AsyncTask<String, Void, Object> {

    private static final String TAG = "MessageToServer";

    private Object serverResponse = null;

    @Override
    protected Object doInBackground(String... params) {
        String link = params[0];
        if(link.endsWith(".jpeg")){
            serverResponse = getBitmapFromURL(link);
        }else{
            serverResponse = getTextFromURL(link);
        }
        return serverResponse;
    }

    private Bitmap getBitmapFromURL(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);
            return mBitmap;
        } catch (IOException exc) {
            Log.e(TAG,Log.getStackTraceString(exc));
            return null;
        }
    }
    private String getTextFromURL(String link){
        String response = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            try {
                connection.connect();
            }catch(ConnectException exc){
                Log.e(TAG,Log.getStackTraceString(exc));
                return response;
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            InputStream in = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            byte[] dat = new byte[1024];
            int bytesRead;
            while((bytesRead = bis.read(dat))!=-1){
                bas.write(dat,0,bytesRead);
            }
            response = new String(bas.toByteArray(),"UTF-8");
            bas.close();
            bis.close();
        }catch(IOException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    public String getServerTextResponse(){
        return (String) serverResponse;
    }
    public Bitmap getServerBitmapResponse(){
        return (Bitmap) serverResponse;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        //Log.d(TAG, result);
    }
}
