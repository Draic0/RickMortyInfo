package example.rickmortyinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class FragmentList extends Fragment {

    private static final String TAG = "FragmentList";

    MyAdapter adapter;
    ProgressBar pb;
    TextView label;
    LinearLayout waitPanel;

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
                    NextContentRequest rqst = new NextContentRequest(FragmentList.this);
                    rqst.execute(link);
                }else{
                    waitPanel.setVisibility(View.GONE);
                }
            }
        });
    }

    private static class NextContentRequest extends Request{
        private FragmentList fragment;
        NextContentRequest(FragmentList fragment){
            this.fragment = fragment;
        }
        @Override
        protected void onPostExecute(Object response) {
            super.onPostExecute(response);
            JSONArray arr = CommonOps.addNextSources(fragment.getActivity(),(String)response);
            if(arr==null){
                fragment.label.setText("Could not load data...");
                fragment.pb.setVisibility(View.GONE);
            }else {
                fragment.waitPanel.setVisibility(View.GONE);
                fragment.adapter.addMoreSources(arr);
            }
            addingSources = false;
        }
    }
}
