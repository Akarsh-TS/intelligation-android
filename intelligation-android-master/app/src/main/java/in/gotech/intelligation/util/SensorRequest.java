package in.gotech.intelligation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashSet;

import in.gotech.intelligation.R;
import in.gotech.intelligation.VolleyApplication;

/**
 * Created by anirudh on 23/01/16.
 */
public abstract class SensorRequest {
    public void refreshSensorIds(Context context) {
        SharedPreferences credentialsSharedPref = context.getSharedPreferences(VolleyApplication.PREFS_NAME, Context.MODE_PRIVATE);
        String username = credentialsSharedPref.getString("username", null);
        final SharedPreferences.Editor credentialsEditor = credentialsSharedPref.edit();
        String url = context.getString(R.string.server_ip) + "/requestall?aadhaar_id=" + username;
        JsonArrayRequest credentialVerificationRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        HashSet<String> sensorIdSet = new HashSet<String>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                sensorIdSet.add(response.getJSONObject(i).getString("sensor_id"));
                            } catch (Exception e) {
                                Log.e("Login", "Error parsing sensor ids");
                            }
                        }
                        credentialsEditor.putStringSet("sensor_ids", sensorIdSet);
                        credentialsEditor.commit();
                        onRefreshSensorIds();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        VolleyApplication.getInstance().getRequestQueue().add(credentialVerificationRequest);
    }
    public abstract void onRefreshSensorIds();
}
