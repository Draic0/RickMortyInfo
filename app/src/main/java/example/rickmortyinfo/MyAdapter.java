package example.rickmortyinfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class MyAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MyAdapter";

    private ArrayList<Character> data;
    private Activity context;

    public MyAdapter(Activity context, JSONArray source){
        this.context = context;
        data = new ArrayList<>();
        try {
            for (int i = 0; i < source.length(); i++) {
                JSONObject obj = source.getJSONObject(i);
                Character item = new Character(obj);
                data.add(item);
            }
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
    }

    public void addMoreSources(JSONArray source){
        try {
            for (int i = 0; i < source.length(); i++) {
                JSONObject obj = source.getJSONObject(i);
                Character item = new Character(obj);
                data.add(item);
            }
        }catch(JSONException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_layout,parent,false);
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character item = (Character)v.getTag();
                MainActivity act = (MainActivity)context;
                SharedPreferences sp = CommonOps.getSharedPreferences(act);
                int count = sp.getInt(item.getId().toString(),0);
                count++;
                sp.edit().putInt(item.getId().toString(),count).apply();
                Bundle args = new Bundle();
                args.putSerializable("info",item);
                args.putInt("count",count);
                act.showFragment(MainActivity.FRAGMENT_INFO,args);
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Character item = getItem(position);
        final ViewHolder h = (ViewHolder)holder;
        h.counter++;
        h.text.setText(item.getName());
        h.view.setTag(item);
        Drawable img = item.getImage(context);
        if(img!=null){
            h.image.setImageDrawable(img);
        }else{
            h.image.setImageDrawable(context.getResources().getDrawable(R.drawable.image_placeholder_24dp));
            final MessageToServer msg = new MessageToServer();
            msg.execute(item.getImgLink());
            Thread trd = new Thread(new Runnable() {
                @Override
                public void run() {
                    int test = h.counter;
                    try{
                        while(msg.getStatus()!= AsyncTask.Status.FINISHED) {
                            Thread.sleep(300);
                        }
                    }catch(InterruptedException exc){
                        Log.e(TAG,Log.getStackTraceString(exc));
                    }
                    Bitmap b = msg.getServerBitmapResponse();
                    CommonOps.cacheJpeg(context, b, item.getImgName());
                    if(test!=h.counter){
                        return;
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable d = item.getImage(context);
                            if(d!=null) {
                                h.image.setImageDrawable(d);
                            }
                        }
                    });
                }
            });
            trd.start();
        }
    }

    @Override
    public int getItemCount() {
        if(data==null) {
            return -1;
        }
        return data.size();
    }
    public Character getItem(int pos){
        return data.get(pos);
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        View view;
        int counter;
        ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            view = itemView;
            counter = 0;
        }
    }

}
