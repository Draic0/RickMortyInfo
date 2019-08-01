package example.rickmortyinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

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
        showFragment(FRAGMENT_LIST,null);
    }

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
