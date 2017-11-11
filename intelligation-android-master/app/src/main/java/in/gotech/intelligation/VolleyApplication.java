package in.gotech.intelligation;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by anirudh on 31/07/15.
 */
public class VolleyApplication extends Application {

    public static final String PREFS_NAME = "Credentials";

    private static VolleyApplication sInstance;

    private RequestQueue mRequestQueue;

    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);

        sInstance = this;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
