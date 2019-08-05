package example.rickmortyinfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class Character implements Serializable {
    private static final String TAG = "Character";
    private String data;
    public Character(JSONObject data){
        this.data = data.toString();
    }
    public String getStringValue(String key){
        String value = null;
        try{
            value = new JSONObject(data).getString(key);
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        return value;
    }
    public Integer getIntValue(String key){
        Integer value = null;
        try{
            value = new JSONObject(data).getInt(key);
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        return value;
    }
    public String getName(){
        return getStringValue("name");
    }
    public String getInfoLink(){
        return getStringValue("url");
    }
    public String getImgLink(){
        return getStringValue("image");
    }
    public String getImgName(){
        String link = getImgLink();
        return link.substring(link.lastIndexOf('/')+1);
    }
    public Integer getId(){
        return getIntValue("id");
    }
    public String getStatus(){
        return getStringValue("status");
    }
    public String getSpecies(){
        return getStringValue("species");
    }
    public String getType(){
        return getStringValue("type");
    }
    public String getGender(){
        return getStringValue("gender");
    }
    public String getOrigin(){
        try {
            JSONObject o = new JSONObject(data).getJSONObject("origin");
            return o.getString("name");
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        return null;
    }
    public String getLocation(){
        try {
            JSONObject o = new JSONObject(data).getJSONObject("location");
            return o.getString("name");
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        return null;
    }

    public Drawable getImage(Context context){
        return CommonOps.getCachedImage(context, getImgName());
    }

}
