package com.anstar.fieldwork;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {


    private ImageView mViewLogo;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_splash, container, false);
        mViewLogo = (ImageView) v.findViewById(R.id.logo);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate);
        anim.reset();
        mViewLogo.clearAnimation();
        mViewLogo.startAnimation(anim);
    }
}
