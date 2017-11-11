package in.gotech.intelligation.stats;

/**
 * Created by anirudh on 21/07/15.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import in.gotech.intelligation.R;
import in.gotech.intelligation.VolleyApplication;

public class StatsFragment extends Fragment {
    private LineChart mChart;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private StatsRecyclerViewAdapter mStatsRecyclerViewAdapter;
    private ArrayList<SensorStatsData> mSensorStatsDataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.stats, container, false);
        LinearLayout statsLinearLayout = (LinearLayout) v.findViewById(R.id.linear_layout_stats);

        mSensorStatsDataList = new ArrayList<>();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_stats);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mStatsRecyclerViewAdapter = new StatsRecyclerViewAdapter(getContext(), mSensorStatsDataList);
        mRecyclerView.setAdapter(mStatsRecyclerViewAdapter);

        populateLineData();

        return v;
    }


    private void populateLineData() {
        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(VolleyApplication.PREFS_NAME, Activity.MODE_PRIVATE);

        Set<String> sensorIdSet = credentialsSharedPref.getStringSet("sensor_ids", null);

        for (String s : sensorIdSet) {
            JsonObjectRequest statRequest = getStatJsonObjectRequest(s);
            RequestQueue requestQueue = VolleyApplication.getInstance().getRequestQueue();
            requestQueue.add(statRequest);
        }
    }

    public JsonObjectRequest getStatJsonObjectRequest(String sensorId) {
        String url = getString(R.string.server_ip) + "/get_stats?sensor_id=" + sensorId;
        return new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SensorStatsData sensorStatsData = new SensorStatsData();
                        try {
                            String cropName = response.getString("crop");
                            sensorStatsData.cropName = cropName;
                            JSONArray valueArray = response.getJSONArray("values");
                            for (int i = 0; i < valueArray.length(); i++) {

                                JSONObject sensorValue = valueArray.getJSONObject(i);
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date d = formatter.parse(sensorValue.getString("time_recorded"));
                                double value = sensorValue.getDouble("value");
                                sensorStatsData.sensorTimeSeries.add(d, value);
                                Log.d("StatFragment", "Added Date: " + d + " Value: " + value);

                            }
                            mSensorStatsDataList.add(sensorStatsData);
                            mStatsRecyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("StatsFragment", "Oh noes, can't parse JSON");
                        } catch (ParseException e) {
                            Log.e("StatsFragment", "Oh noes, can't parse date");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("StatsFragment", "Oops! Volley's Network's bad!\n" + error);
                    }
                });
    }
    private LineData setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);x
            yVals.add(new Entry(val, i));
        }

        LineDataSet set1;
                    // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(Color.BLACK);
//            set1.setCircleColor(Color.BLACK);
//            set1.setLineWidth(1f);
//            set1.setCircleRadius(3f);
//            set1.setDrawCircleHole(false);
//            set1.setValueTextSize(9f);
//            set1.setDrawFilled(true);

//            if (Utils.getSDKInt() >= 18) {
//                // fill drawable only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.stats_gradient);
//                set1.setFillDrawable(drawable);
//            }
//            else {
//                set1.setFillColor(Color.BLACK);
//            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            return data;

    }
}
