package example.rickmortyinfo;

import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class FragmentList extends Fragment {

    private static final String TAG = "FragmentList";

    private MyAdapter adapter;
    private ProgressBar pb;
    private TextView label;
    private LinearLayout waitPanel;

    public FragmentList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment_layout, container, false);
    }

    private static boolean addingSources = false;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.list);
        pb = getView().findViewById(R.id.progress_bar);
        label = getView().findViewById(R.id.label);
        waitPanel = getView().findViewById(R.id.wait_panel);
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final JSONArray source = CommonOps.getSources(getActivity());
        adapter = new MyAdapter(getActivity(),source);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(addingSources){
                        return;
                    }
                    addingSources = true;
                    String link = CommonOps.getSharedPreferences(getActivity()).getString("next", "");
                    if(link.equals("")){
                        addingSources = false;
                        return;
                    }
                    waitPanel.setVisibility(View.VISIBLE);
                    label.setText("Loading...");
                    pb.setVisibility(View.VISIBLE);
                    Thread trd = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final JSONArray arr = getNextSources();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(arr==null){
                                        label.setText("Could not load data...");
                                        pb.setVisibility(View.GONE);
                                    }else {
                                        waitPanel.setVisibility(View.GONE);
                                        adapter.addMoreSources(arr);
                                    }
                                    addingSources = false;
                                }
                            });
                        }
                    });
                    trd.start();
                }else{
                    waitPanel.setVisibility(View.GONE);
                }
            }
        });
    }

    private JSONArray getNextSources() {
        String link = CommonOps.getSharedPreferences(getActivity()).getString("next", "");
        if (link.equals("")) {
            return null;
        }
        String response = CommonOps.getServerResponse(link);
        if (response == null) {
            return null;
        }
        JSONArray results = new JSONArray();
        JSONArray sources = CommonOps.getSources(getActivity());
        if(sources==null){
            sources = new JSONArray();
        }
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject o = obj.getJSONObject("info");
            link = o.getString("next");
            SharedPreferences sp = CommonOps.getSharedPreferences(getActivity());
            sp.edit().putString("next", link).apply();
            JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                results.put(arr.get(i));
                sources.put(arr.get(i));
            }
        } catch (JSONException exc) {
            Log.e(TAG, Log.getStackTraceString(exc));
        }
        CommonOps.cacheSources(getActivity(),sources.toString());
        return results;
    }
}
