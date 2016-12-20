package com.baijiahulian.bjhl_liveplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

// import com.genshuixue.liveplayer.ui.BJLiveCourseActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.join).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toJoin();
            }
        });
        return view;
    }

    public void toJoin() {
        Intent intent = new Intent(getContext(), JoinCodeActivity.class);
        startActivity(intent);
    }
}