package example.rickmortyinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class CommonOps {

    private static final String TAG = "CommonOps";

    public static String getCachedImagesDir(Context context){
        File f = new File(context.getApplicationInfo().dataDir+File.separator+"cached_photos"+File.separator);
        if(!f.isDirectory()){
            f.mkdirs();
        }
        return f.toString();
    }

    public static void cacheJpeg(Context context, Bitmap bmp, String name) {
        if(bmp==null){
            return;
        }
        FileOutputStream out = null;
        File f = new File(getCachedImagesDir(context)+name);
        try {
            out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try { if (out != null ) out.close(); }
            catch(Exception ex) {}
        }
    }
    public static JSONArray addNextSources(Context context,String response){
        JSONArray results = new JSONArray();
        JSONArray sources = CommonOps.getSources(context);
        if(sources==null){
            sources = new JSONArray();
        }
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject o = obj.getJSONObject("info");
            String link = o.getString("next");
            SharedPreferences sp = CommonOps.getSharedPreferences(context);
            sp.edit().putString("next", link).apply();
            JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                results.put(arr.get(i));
                sources.put(arr.get(i));
            }
        } catch (JSONException exc) {
            Log.e(TAG, Log.getStackTraceString(exc));
        }
        CommonOps.cacheSources(context,sources.toString());
        return results;
    }
    private static void cacheSources(Context context, String data){
        String path = context.getApplicationInfo().dataDir+File.separator+"SOURCES.txt";
        writeToFile(path,data);
    }
    public static Drawable getCachedImage(Context context, String name){
        return Drawable.createFromPath(getCachedImagesDir(context)+name);
    }
    public static JSONArray getSources(Context context){
        String src = getRawSources(context);
        if(src!=null){
            try {
                return new JSONArray(src);
            }catch(JSONException exc){
                Log.e(TAG,Log.getStackTraceString(exc));
            }
        }
        return null;
    }
    public static String getRawSources(Context context){
        File f = new File(context.getApplicationInfo().dataDir+File.separator+"SOURCES.txt");
        if(f.isFile()){
            String src = readFromFile(f.getPath());
            return src;
        }
        return null;
    }
    private static void writeToFile(String path, String data) {
        try {
            FileOutputStream fos = new FileOutputStream (new File(path));
            byte[] bts = data.getBytes();
            fos.write(bts);
            fos.close();
        }catch(IOException exc) {
            Log.e(TAG, Log.getStackTraceString(exc));
        }
    }
    private static String readFromFile(String path) {
        String str = null;
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] bts = new byte[fis.available()];
            fis.read(bts);
            str = new String(bts);
        }catch(FileNotFoundException exc) {
            Log.e(TAG, Log.getStackTraceString(exc));
        }catch(IOException exc) {
            Log.e(TAG, Log.getStackTraceString(exc));
        }
        return str;
    }
    private static final String spTag = "RickMorty";
    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(spTag,0);
    }
}
