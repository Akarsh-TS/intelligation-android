package in.gotech.intelligation.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import in.gotech.intelligation.R;
import in.gotech.intelligation.Sensor;
import in.gotech.intelligation.VolleyApplication;

/**
 * Created by anirudh on 16/01/16.
 */
public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewAdapter.SettingsCardViewHolder> {
    private ArrayList<Sensor> mSensors;
    private ArrayList<CharSequence> mCrops;
    private Context mContext;
    private static Drawable editDrawable;
    private static Drawable saveDrawable;

    public SettingsRecyclerViewAdapter(ArrayList<Sensor> sensors, Context context, ArrayList<CharSequence> crops) {
        mContext = context;
        mSensors = sensors;
        mCrops = crops;
        if (Build.VERSION.SDK_INT >= 21) {
            saveDrawable = mContext.getDrawable(R.drawable.ic_save_black_48dp);
        } else {
            saveDrawable = mContext.getResources().getDrawable(R.drawable.ic_save_black_48dp);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            editDrawable = mContext.getDrawable(R.drawable.ic_edit_black_48dp);
        } else {
            editDrawable = mContext.getResources().getDrawable(R.drawable.ic_edit_black_48dp);
        }
    }

    @Override
    public int getItemCount() {
        return mSensors.size();
    }

    @Override
    public void onBindViewHolder(SettingsCardViewHolder holder, int position) {
        Sensor currentSensor = mSensors.get(position);
        holder.pinNumberTextView.setText(mContext.getString(R.string.pin_number) + currentSensor.pinNumber);
        holder.cropSpinner.setSelection(mCrops.indexOf(currentSensor.cropName));
        if (currentSensor.editing) {
            holder.editImageButton.setImageDrawable(saveDrawable);
            holder.cropSpinner.setEnabled(true);
            holder.deleteImageButton.setClickable(true);
            holder.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.editImageButton.setImageDrawable(editDrawable);
            holder.cropSpinner.setEnabled(false);
            holder.deleteImageButton.setClickable(false);
            holder.deleteImageButton.setVisibility(View.INVISIBLE);
        }
        holder.editImageButton.setTag(currentSensor);
        holder.deleteImageButton.setTag(currentSensor);
    }

    @Override
    public SettingsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_card, parent, false);
        SettingsCardViewHolder vh = new SettingsCardViewHolder(v);
        return vh;
    }

    public class SettingsCardViewHolder extends RecyclerView.ViewHolder {
        public Spinner cropSpinner;
        public TextView pinNumberTextView;
        public ImageButton editImageButton;
        public ImageButton deleteImageButton;

        public SettingsCardViewHolder(final View settingsCardView) {
            super(settingsCardView);
            cropSpinner = (Spinner) settingsCardView.findViewById(R.id.crop_name_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mCrops);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            cropSpinner.setAdapter(adapter);
            pinNumberTextView = (TextView) settingsCardView.findViewById(R.id.pin_number_text_view);
            editImageButton = (ImageButton) settingsCardView.findViewById(R.id.edit_image_button);

            deleteImageButton = (ImageButton) settingsCardView.findViewById(R.id.delete_image_button);
            deleteImageButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int position = getAdapterPosition();
                            final Sensor currentSensor = mSensors.get(position);
                            final SharedPreferences credentialsSharedPref = mContext.getSharedPreferences(VolleyApplication.PREFS_NAME, Context.MODE_PRIVATE);
                            String aadhaar_id = credentialsSharedPref.getString("username", "");
                            String url = mContext.getString(R.string.server_ip) + "/delete_sensor?aadhaar_id=" + aadhaar_id + "&sensor_id=" + currentSensor.sensorId;
                            JsonArrayRequest deleteSensorRequest = new JsonArrayRequest(url,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            mSensors.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, mSensors.size());

                                            SharedPreferences.Editor credentialsEditor = credentialsSharedPref.edit();

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

                                            Toast.makeText(mContext, R.string.sensor_deleted_toast, Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("SettingsRecyclerView", "Oops! Volley's Network's bad!");
                                        }
                                    }
                            );
                            VolleyApplication.getInstance().getRequestQueue().add(deleteSensorRequest);
                        }
                    }
            );

            editImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Sensor currentSensor = mSensors.get(position);
                    if (currentSensor.newSensor) {
                        int cropId = cropSpinner.getSelectedItemPosition() + 1;
                        final SharedPreferences credentialsSharedPref = mContext.getSharedPreferences(VolleyApplication.PREFS_NAME, Context.MODE_PRIVATE);
                        String aadhaar_id = credentialsSharedPref.getString("username", "");
                        String url = mContext.getString(R.string.server_ip) + "/add_sensor?aadhaar_id=" + aadhaar_id + "&crop_id=" + cropId + "&pin_no=" + currentSensor.pinNumber;
                        JsonObjectRequest addSensorRequest = new JsonObjectRequest(url,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SharedPreferences.Editor credentialsEditor = credentialsSharedPref.edit();

                                        HashSet<String> sensorIdSet = new HashSet<>();
                                        JSONArray sensors;
                                        try {
                                            sensors = response.getJSONArray("sensors");
                                        } catch (JSONException e) {
                                            Log.e("SettingsFragment", "Invalid JSON");
                                            return;
                                        }

                                        for (int i = 0; i < sensors.length(); i++) {
                                            try {
                                                sensorIdSet.add(sensors.getJSONObject(i).getString("sensor_id"));
                                            } catch (Exception e) {
                                                Log.e("SettingsFragment", "Error parsing sensor ids");
                                            }
                                        }
                                        credentialsEditor.putStringSet("sensor_ids", sensorIdSet);
                                        credentialsEditor.commit();

                                        currentSensor.newSensor = false;
                                        currentSensor.editing = false;
                                        try {
                                            currentSensor.sensorId = Integer.parseInt(response.getString("insertId"));
                                        } catch (JSONException e) {
                                            Log.e("SettingsFragment", "Invalid JSON");
                                            return;
                                        }

                                        Toast.makeText(mContext, R.string.new_sensor_added_toast, Toast.LENGTH_SHORT).show();

                                        cropSpinner.setEnabled(false);
                                        editImageButton.setImageDrawable(editDrawable);
                                        deleteImageButton.setClickable(false);
                                        deleteImageButton.setVisibility(View.INVISIBLE);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("SettingsRecyclerView", "Oops! Volley's Network's bad!");
                                    }
                                }
                        );
                        VolleyApplication.getInstance().getRequestQueue().add(addSensorRequest);
                        return;
                    }
                    if (editImageButton.getDrawable() == saveDrawable) {
                        int cropId = cropSpinner.getSelectedItemPosition() + 1;
                        String url = mContext.getString(R.string.server_ip) + "/edit_sensor_settings?sensor_id=" + currentSensor.sensorId + "&crop_id=" + cropId;
                        StringRequest editSensorRequest = new StringRequest(url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        currentSensor.editing = false;
                                        Toast.makeText(mContext, R.string.settings_changed_toast, Toast.LENGTH_SHORT).show();
                                        editImageButton.setImageDrawable(editDrawable);
                                        cropSpinner.setEnabled(false);
                                        deleteImageButton.setClickable(false);
                                        deleteImageButton.setVisibility(View.INVISIBLE);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("SettingsRecyclerView", "Oops! Volley's Network's bad!");
                                    }
                                }
                        );
                        VolleyApplication.getInstance().getRequestQueue().add(editSensorRequest);
                    } else {
                        currentSensor.editing = true;
                        deleteImageButton.setClickable(true);
                        deleteImageButton.setVisibility(View.VISIBLE);
                        editImageButton.setImageDrawable(saveDrawable);
                        cropSpinner.setEnabled(true);
                    }
                }
            });
        }
    }
}
