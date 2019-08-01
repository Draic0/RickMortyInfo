package example.rickmortyinfo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Draic0 on 01.08.2019.
 */

public class FragmentInfo extends Fragment {

    private static final String TAG = "FragmentInfo";

    private Character character;
    private int count;

    public FragmentInfo() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        character = (Character)getArguments().getSerializable("info");
        count = getArguments().getInt("count");
        ImageView iv = getView().findViewById(R.id.image);
        Drawable d = character.getImage(getActivity());
        if(d!=null) {
            iv.setImageDrawable(d);
        }
        initText();
    }
    private void initText(){
        LinearLayout ll = getView().findViewById(R.id.name);
        TextView tv = ll.findViewById(R.id.key);
        tv.setText("Name:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getName());
        ll = getView().findViewById(R.id.species);
        tv = ll.findViewById(R.id.key);
        tv.setText("Species:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getSpecies());
        ll = getView().findViewById(R.id.status);
        tv = ll.findViewById(R.id.key);
        tv.setText("Status:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getStatus());
        ll = getView().findViewById(R.id.type);
        tv = ll.findViewById(R.id.key);
        tv.setText("Type:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getType());
        ll = getView().findViewById(R.id.gender);
        tv = ll.findViewById(R.id.key);
        tv.setText("Gender:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getGender());
        ll = getView().findViewById(R.id.origin);
        tv = ll.findViewById(R.id.key);
        tv.setText("Origin:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getOrigin());
        ll = getView().findViewById(R.id.location);
        tv = ll.findViewById(R.id.key);
        tv.setText("Location:");
        tv = ll.findViewById(R.id.value);
        tv.setText(character.getLocation());
        ll = getView().findViewById(R.id.visits);
        tv = ll.findViewById(R.id.key);
        tv.setText("Page visited:");
        tv = ll.findViewById(R.id.value);
        if(count==1) {
            tv.setText("" + count + " time");
        }else{
            tv.setText("" + count + " times");
        }
    }
}
