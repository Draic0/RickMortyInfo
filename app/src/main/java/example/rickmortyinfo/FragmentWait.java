package example.rickmortyinfo;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Draic0 on 03.08.2019.
 */

public class FragmentWait extends Fragment {

    private static final String TAG = "FragmentWait";

    public FragmentWait() {}

    private ProgressBar pb;
    private TextView label;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wait_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pb = getView().findViewById(R.id.progress_bar);
        label = getView().findViewById(R.id.label);
        button = getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread trd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadSources();
                    }
                });
                trd.start();
            }
        });
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                loadSources();
            }
        });
        trd.start();
    }

    private void loadSources(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                label.setText("Loading resources. Please wait.");
            }
        });
        String link = "https://rickandmortyapi.com/api/character/";
        JSONArray results = new JSONArray();
        do {
            String response = getServerResponse(link);
            if(response==null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBadConnection();
                    }
                });
                return;
            }
            link = "";
            try {
                JSONObject obj = new JSONObject(response);
                JSONArray arr = obj.getJSONArray("results");
                for (int i = 0; i < arr.length(); i++) {
                    results.put(arr.get(i));
                }
                JSONObject o = obj.getJSONObject("info");
                link = o.getString("next");
            } catch (JSONException exc) {
                Log.e(TAG, Log.getStackTraceString(exc));
            }
        }while(link.length()>0);
        CommonOps.cacheSources(getActivity(), results.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getActivity()).showFragment(MainActivity.FRAGMENT_LIST,null);
            }
        });
    }

    private void showBadConnection(){
        pb.setVisibility(View.GONE);
        label.setText("Could not load resources. Please check your internet connection.");
        button.setVisibility(View.VISIBLE);
    }

    private String getServerResponse(String request){
        MessageToServer msg = new MessageToServer();
        msg.execute(request);
        try{
            while (msg.getStatus() != AsyncTask.Status.FINISHED) {
                Thread.sleep(300);
            }
        }catch(InterruptedException exc){
            Log.e(TAG,Log.getStackTraceString(exc));
        }
        return msg.getServerTextResponse();

    }
}
