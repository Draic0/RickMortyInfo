package example.rickmortyinfo;

/**
 * Created by Draic0 on 01.08.2019.
 */

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request extends AsyncTask<String, Void, Object> {

    private static final String TAG = "Request";

    @Override
    protected Object doInBackground(String... params) {
        String link = params[0];
        if(link==null){
            return null;
        }
        Object serverResponse = null;
        ByteArrayOutputStream bas = null;
        HttpURLConnection connection = null;
        try {
            connection = prepareConnection(link);
            InputStream input = connection.getInputStream();
            if (link.endsWith(".jpeg")) {
                serverResponse = BitmapFactory.decodeStream(input);
            } else {
                BufferedInputStream bis = new BufferedInputStream(input);
                bas = new ByteArrayOutputStream();
                byte[] dat = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(dat)) != -1) {
                    bas.write(dat, 0, bytesRead);
                }
                serverResponse = new String(bas.toByteArray(), "UTF-8");
            }
        }catch(IOException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }finally{
            try {
                if(connection!=null) {
                    connection.disconnect();
                }
                if(bas!=null) {
                    bas.close();
                }
            }catch(Exception exc){
                Log.e(TAG,Log.getStackTraceString(exc));
            }
        }
        return serverResponse;
    }

    private HttpURLConnection prepareConnection(String link) throws IOException{
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        return connection;
    }

}
