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
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final JSONArray source = CommonOps.getSources(getActivity());
        rv.setAdapter(new MyAdapter(getActivity(),source));
    }
}
