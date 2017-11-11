package in.gotech.intelligation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import in.gotech.intelligation.login.Login;
import in.gotech.intelligation.navdrawer.NavDrawerActivity;

/**
 * Created by anirudh on 20/07/15.
 */
public class Splash extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);
    /* New Handler to start the Menu-Activity
     * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences credentialsSharedPref = getSharedPreferences(VolleyApplication.PREFS_NAME, MODE_PRIVATE);
                Intent mainIntent;
                if (credentialsSharedPref.contains("username")) {
                    mainIntent = new Intent(Splash.this, NavDrawerActivity.class);
                } else {
                    mainIntent = new Intent(Splash.this, Login.class);
                }
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
