package in.gotech.intelligation.network;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import in.gotech.intelligation.Sensor;

/**
 * Created by anirudh on 17/01/16.
 */
public abstract class SensorResponseListener implements Response.Listener<JSONObject> {
    @Override
    public void onResponse(JSONObject response) {
        int sensorId = 0;
        int sensorValue = 0;
        String cropName = null;
        boolean autoStatus = false;
        boolean motorStatus = false;
        int pinNumber = 0;
        try {
            sensorId = response.getInt("sensor_id");
            sensorValue = response.getInt("current_value");
            cropName = response.getString("crop_name");
            autoStatus = response.getInt("auto") == 1;
            motorStatus = response.getInt("motor_status") == 1;
            pinNumber = response.getInt("pin_no");

        } catch (Exception e) {
            Log.e("SummaryFragment", "Oops! JSON's bad!");
        }
        Sensor newSensorReading = new Sensor(sensorId, sensorValue, cropName, autoStatus, motorStatus, pinNumber);
        onNewSensorReading(newSensorReading);
    }

    public abstract void onNewSensorReading(Sensor newSensorReading);

}
