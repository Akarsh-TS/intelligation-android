package in.gotech.intelligation.summary;

/**
 * Created by anirudh on 21/07/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Set;

import in.gotech.intelligation.R;
import in.gotech.intelligation.Sensor;
import in.gotech.intelligation.VolleyApplication;
import in.gotech.intelligation.login.Login;
import in.gotech.intelligation.network.JsonObjectSensorRequest;
import in.gotech.intelligation.network.SensorResponseListener;
import in.gotech.intelligation.util.SensorRequest;

public class SummaryFragment extends Fragment {
    SummaryListAdapter mSummaryListAdapter;
    ListView summaryListView;
    ArrayList<Sensor> mSensorArrayList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Set<String> mSensorIdSet;
    int mPendingRequests;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.summary, container, false);

        summaryListView = (ListView) mSwipeRefreshLayout.findViewById(R.id.summary_list_view);

        mSensorArrayList = new ArrayList<>();

        mSummaryListAdapter = new SummaryListAdapter(getActivity(), R.layout.summary_card, mSensorArrayList);

        summaryListView.setAdapter(mSummaryListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();

        return mSwipeRefreshLayout;

    }


    public JsonObjectSensorRequest getSensorJsonObjectRequest(int sensorId) {
        String url = getString(R.string.server_ip) + "/request?sensor_id=" + sensorId;
        return new JsonObjectSensorRequest(url,
                new SensorResponseListener() {
                    @Override
                    public void onNewSensorReading(Sensor newSensorReading) {
                        mSensorArrayList.add(newSensorReading);
                        mSummaryListAdapter.notifyDataSetChanged();
                        mPendingRequests--;
                        if (mPendingRequests == 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SummaryFragment", "Oops! Volley's Network's bad!\n" + error);
                    }
                });
    }

    private class SummaryListAdapter extends ArrayAdapter<Sensor> {

        ArrayList<Sensor> sensorArrayList;
        Context mContext;
        LayoutInflater inflater;

        public SummaryListAdapter(Context context, int layoutResourceId, ArrayList<Sensor> data) {
            super(context, layoutResourceId, data);
            sensorArrayList = data;
            mContext = context;
            inflater = getActivity().getLayoutInflater();
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            SummaryView summaryView = (SummaryView) convertView;
            if (null == summaryView)
                summaryView = SummaryView.inflate(parent);
            summaryView.setItem(getItem(position));
            return summaryView;
        }
    }

    void refresh() {
        SensorRequest sr = new SensorRequest() {
            @Override
            public void onRefreshSensorIds() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSensorArrayList.clear();
                SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(VolleyApplication.PREFS_NAME, Activity.MODE_PRIVATE);
                mSensorIdSet = credentialsSharedPref.getStringSet("sensor_ids", null);
                mPendingRequests = mSensorIdSet.size();
                for (String s : mSensorIdSet) {
                    getSensorJsonObjectRequest(Integer.parseInt(s)).fetchSensor();
                }
            }
        };
        sr.refreshSensorIds(getContext());
    }
}

