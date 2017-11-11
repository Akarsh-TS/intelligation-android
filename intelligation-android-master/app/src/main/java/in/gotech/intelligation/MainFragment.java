package in.gotech.intelligation;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import in.gotech.intelligation.stats.StatsFragment;
import in.gotech.intelligation.summary.SummaryFragment;
import in.gotech.intelligation.weather.WeatherFragment;

/**
 * Created by anirudh on 15/08/15.
 */
public class MainFragment extends Fragment {

    private static final int NUM_ITEMS = 3;

    MyAdapter mAdapter;

    ViewPager mPager;

    SlidingTabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(VolleyApplication.PREFS_NAME, AppCompatActivity.MODE_PRIVATE);

        Toast.makeText(getActivity(), "Welcome : " + credentialsSharedPref.getString("username", ""), Toast.LENGTH_SHORT).show();

        mAdapter = new MyAdapter(getChildFragmentManager());

        mPager = (ViewPager) rootView.findViewById(R.id.pager);

        mPager.setAdapter(mAdapter);

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        tabs.setDistributeEvenly(true);

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mPager);

        return rootView;

    }


    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SummaryFragment();
                case 1:
                    return new StatsFragment();
                default:
                    return new WeatherFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.summary_title);
                case 1:
                    return getString(R.string.statistics_title);
                case 2:
                    return getString(R.string.weather_title);
            }
            return "";
        }
    }
}
