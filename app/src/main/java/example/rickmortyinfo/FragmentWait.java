package example.rickmortyinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
                loadSources();
            }
        });
        loadSources();
    }

    private void loadSources(){
        pb.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        label.setText("Loading resources. Please wait.");
        String link = "https://rickandmortyapi.com/api/character/";
        GetContentRequest rqst = new GetContentRequest(FragmentWait.this);
        rqst.execute(link);

    }

    private static class GetContentRequest extends Request{
        private FragmentWait fragment;
        GetContentRequest(FragmentWait fragment){
            this.fragment = fragment;
        }
        @Override
        protected void onPostExecute(Object response) {
            super.onPostExecute(response);
            if(response==null) {
                fragment.showBadConnection();
                return;
            }
            CommonOps.addNextSources(fragment.getActivity(),(String)response);
            ((MainActivity)fragment.getActivity()).showFragment(MainActivity.FRAGMENT_LIST,null);
        }
    }

    private void showBadConnection(){
        pb.setVisibility(View.GONE);
        label.setText("Could not load resources. Please check your internet connection.");
        button.setVisibility(View.VISIBLE);
    }
}
