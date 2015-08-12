package com.anstar.fieldwork;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;


public class IntroductionFragment extends Fragment {

    private OnIntroductionFragmentInteractionListener mOnIntroductionFragmentInteractionListener;

    // Container Activity must implement this interface
    public interface OnIntroductionFragmentInteractionListener {
        void onButtonLoginClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_introduction, container, false);

        final ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        final ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);

        CirclePageIndicator circleIndicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

        Button login = (Button) v.findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mOnIntroductionFragmentInteractionListener.onButtonLoginClick();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnIntroductionFragmentInteractionListener = (OnIntroductionFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnIntroductionFragmentInteractionListener");
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private final int[] mImages = new int[] {
                R.layout.calendar,
                R.layout.customers,
                R.layout.payment
        };

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            ((ViewPager) container).removeView((View)object);
        }

        @Override
        public int getCount() {
            return this.mImages.length;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            //final Context context = IntroductionActivity.this;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            try {
                View page = inflater.inflate(mImages[position], null);
                ((ViewPager) container).addView(page, 0);
                return page;
            }
            catch (Exception e) {
                return null;
            }
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == ((View) object);
        }
    }

}
