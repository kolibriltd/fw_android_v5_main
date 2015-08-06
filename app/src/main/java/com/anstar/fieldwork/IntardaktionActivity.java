package com.anstar.fieldwork;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;


public class IntardaktionActivity extends AppCompatActivity {
    Button login;

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
            final Context context = IntardaktionActivity.this;
            LayoutInflater inflater = LayoutInflater.from(context);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intardaktion);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);

        CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

        login = (Button) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(IntardaktionActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

}
