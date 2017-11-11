package in.gotech.intelligation.weather;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import in.gotech.intelligation.R;
import in.gotech.intelligation.VolleyApplication;

public class WeatherFragment extends Fragment {

    private Typeface weatherFont;
    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View weatherRootView = inflater.inflate(R.layout.weather, container, false);
        cityField = (TextView) weatherRootView.findViewById(R.id.city_field);
        updatedField = (TextView) weatherRootView.findViewById(R.id.updated_field);
        detailsField = (TextView) weatherRootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) weatherRootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) weatherRootView.findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weather.ttf");
        weatherIcon.setTypeface(weatherFont);
        String OpenWeatherURL = "http://api.openweathermap.org/data/2.5/weather?units=metric&q=";
        String City = "Bangalore";
        String OW_API_KEY = "5e4d05029d8b5eaf515777b3110276c3";
        URL url1 = null;
        try {
            url1 = new URL(OpenWeatherURL + City + "&appid=" + OW_API_KEY);
        } catch (MalformedURLException e) {
            Log.e("Req:", "URL Error");
        }
        String url = OpenWeatherURL + City + "&appid=" + OW_API_KEY;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response == null){
                            Toast.makeText(getContext(),"Not Found",Toast.LENGTH_LONG).show();
                        } else {
                            renderWeather(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Req:", "Opps Volley error");

                    }
                });
        VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);
        return weatherRootView;
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " +json.getJSONObject("sys").getString("country"));
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("WeatherError", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
}
