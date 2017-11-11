package in.gotech.intelligation.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import in.gotech.intelligation.VolleyApplication;
import in.gotech.intelligation.network.CropsResponseListener;
import in.gotech.intelligation.network.JsonArrayCropRequest;
import in.gotech.intelligation.network.JsonObjectSensorRequest;
import in.gotech.intelligation.R;
import in.gotech.intelligation.Sensor;
import in.gotech.intelligation.network.SensorResponseListener;
import in.gotech.intelligation.util.SensorRequest;

/**
 * Created by anirudh on 16/01/16.
 */
public class SettingsFragment extends Fragment{
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mSettingsListAdapter;
    ArrayList<Sensor> mSensorArrayList;
    ArrayList<CharSequence> mCrops;
    FrameLayout mSettingsView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSettingsView = (FrameLayout) inflater.inflate(R.layout.settings, container, false);
        setRetainInstance(true);

        mSensorArrayList = new ArrayList<Sensor>();
        mCrops = new ArrayList<>();

        getCropsJsonArrayRequest().fetchCrops();

        return mSettingsView;
    }

    public JsonObjectSensorRequest getSensorJsonObjectRequest(int sensorId) {
        String url = getString(R.string.server_ip) + "/request?sensor_id=" + sensorId;
        return new JsonObjectSensorRequest(url,
                new SensorResponseListener() {
                    @Override
                    public void onNewSensorReading(Sensor newSensorReading) {
                        mSensorArrayList.add(newSensorReading);
                        mSettingsListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SummaryFragment", "Oops! Volley's Network's bad!\n" + error);
                    }
                });
    }

    void getSensors() {
        SensorRequest sr = new SensorRequest() {
            @Override
            public void onRefreshSensorIds() {
                SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(VolleyApplication.PREFS_NAME, Activity.MODE_PRIVATE);

                Set<String> sensorIdSet = credentialsSharedPref.getStringSet("sensor_ids", null);

                for (String s : sensorIdSet) {
                    getSensorJsonObjectRequest(Integer.parseInt(s)).fetchSensor();
                }
            }
        };
        sr.refreshSensorIds(getContext());
    }

    public JsonArrayCropRequest getCropsJsonArrayRequest() {
        String url = getString(R.string.server_ip) + "/retrieve_all_crops";
        return new JsonArrayCropRequest(url,
                new CropsResponseListener() {
                    @Override
                    public void onCropsResponse(ArrayList<CharSequence> crops) {
                        mCrops.addAll(crops);

                        View settingsRecyclerView = LayoutInflater.from(SettingsFragment.this.getContext()).inflate(R.layout.settings_recycler_view, mSettingsView, false);

                        mSettingsView.addView(settingsRecyclerView);

                        mRecyclerView = (RecyclerView) mSettingsView.findViewById(R.id.settings_recycler_view);
                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mSettingsListAdapter = new SettingsRecyclerViewAdapter(mSensorArrayList, getContext(), crops);
                        mRecyclerView.setAdapter(mSettingsListAdapter);

                        getSensors();

                        FloatingActionButton fab = (FloatingActionButton) mSettingsView.findViewById(R.id.setting_fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Sensor s = new Sensor();
                                s.newSensor = true;
                                s.editing = true;
                                TreeSet<Integer> pinSet = new TreeSet<>();
                                for (int i = 2; i <= 5; i++) {
                                    pinSet.add(i);
                                }
                                for (Sensor sensor : mSensorArrayList) {
                                    pinSet.remove(sensor.pinNumber);
                                }
                                if (pinSet.isEmpty()) {
                                    Toast.makeText(getContext(), R.string.cannot_add_sensors_toast, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                s.pinNumber = pinSet.first();
                                mSensorArrayList.add(0, s);
                                mSettingsListAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SummaryFragment", "Oops! Volley's Network's bad!\n" + error);
                    }
                });
    }
}
