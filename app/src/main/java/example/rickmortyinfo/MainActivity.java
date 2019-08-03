package example.rickmortyinfo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private LinearLayout contentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contentHolder = (LinearLayout)findViewById(R.id.content_holder);
        if(CommonOps.getSources(this)==null){
            showFragment(FRAGMENT_WAIT, null);
        }else {
            showFragment(FRAGMENT_LIST, null);
        }
    }

    public static final String FRAGMENT_WAIT = "wait";
    public static final String FRAGMENT_LIST = "list";
    public static final String FRAGMENT_INFO = "info";
    public void showFragment(String tag, Bundle args){
        contentHolder.removeAllViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        switch(tag) {
            case FRAGMENT_LIST:
                fragment = new FragmentList();
                break;
            case FRAGMENT_INFO:
                fragment = new FragmentInfo();
                fragmentTransaction.addToBackStack(tag);
                break;
            case FRAGMENT_WAIT:
                fragment = new FragmentWait();
                break;
            default:
                return;
        }
        if(args!=null){
            fragment.setArguments(args);
        }
        fragmentTransaction.replace(R.id.content_holder,fragment,tag);
        fragmentTransaction.commit();
    }

}
