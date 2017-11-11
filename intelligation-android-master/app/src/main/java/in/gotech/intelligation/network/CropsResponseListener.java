package in.gotech.intelligation.network;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anirudh on 17/01/16.
 */
public abstract class CropsResponseListener implements Response.Listener<JSONArray> {
    @Override
    public void onResponse(JSONArray response) {
        ArrayList<CharSequence> crops = new ArrayList<>(response.length());
        try {
            for (int i = 0; i < response.length(); i++) {
                crops.add(response.getJSONObject(i).getString("crop_name"));
            }
        } catch (JSONException e) {
            Log.e("CropResponseListener", "Crop name JSON's bad");
        }
        onCropsResponse(crops);
    }

    public abstract void onCropsResponse(ArrayList<CharSequence> crops);

}
