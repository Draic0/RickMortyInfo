package example.rickmortyinfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class FragmentList extends Fragment {

    private static final String TAG = "FragmentList";

    public FragmentList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONArray source = getListSources();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv.setAdapter(new MyAdapter(getActivity(),source));
                    }
                });
            }
        });
        trd.start();
    }

    private JSONArray getListSources(){
        String src = CommonOps.getSources(getActivity());
        if(src!=null){
            try {
                return new JSONArray(src);
            }catch(JSONException exc){
                Log.e(TAG,Log.getStackTraceString(exc));
            }
        }
        String link = "https://rickandmortyapi.com/api/character/";
        JSONArray results = new JSONArray();
        do {
            String response = getServerResponse(link);
            if(response==null){
                return results;
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
        return results;
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
