package in.gotech.intelligation.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import in.gotech.intelligation.R;
import in.gotech.intelligation.VolleyApplication;

/**
 * Created by anirudh on 20/07/15.
 */
public class SignUp extends AppCompatActivity {
    private final int MAP_ACTIVITY_REQUEST_CODE = 1;
    EditText nameEditText;
    EditText mobileNumberEditText;
    EditText aadhaarIdEditText;
    EditText passwordEditText;
    EditText latitudeEditText;
    EditText longitudeEditText;
    EditText deviceIdEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        mobileNumberEditText = (EditText) findViewById(R.id.mobile_number_edit_text);
        aadhaarIdEditText = (EditText) findViewById(R.id.aadhaar_id_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        latitudeEditText = (EditText) findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) findViewById(R.id.longitude_edit_text);
        deviceIdEditText = (EditText) findViewById(R.id.device_id_edit_text);
    }

    public void submitSignUp(View v) {
        VolleyApplication.getInstance().getRequestQueue().add(getSignUpJsonObjectRequest());
    }

    public void getLocation(View v) {
        Intent mapActivity = new Intent(this, MapsActivity.class);
        startActivityForResult(mapActivity, MAP_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                longitudeEditText.setText(latitude + "");
                latitudeEditText.setText(longitude + "");
            }
        }
    }

    private StringRequest getSignUpJsonObjectRequest() {
        String name = nameEditText.getText().toString();
        String mobileNumber = mobileNumberEditText.getText().toString();
        String aadhaarId = aadhaarIdEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();
        String deviceId = deviceIdEditText.getText().toString();
        String url = getString(R.string.server_ip)
                + "/sign_up?name=" + name
                + "&mobile_no=" + mobileNumber
                + "&aadhaar_id=" + aadhaarId
                + "&password=" + password
                + "&lat=" + latitude
                + "&lon=" + longitude
                + "&device_id=" + deviceId;
        StringRequest signUpRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SignUp.this, R.string.signup_successfull_toast, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 406) {
                            Toast.makeText(SignUp.this, R.string.signup_form_incomplete_toast, Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 401) {
                            Toast.makeText(SignUp.this, R.string.sign_up_user_exists_toast, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        signUpRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return signUpRequest;

    }

}

