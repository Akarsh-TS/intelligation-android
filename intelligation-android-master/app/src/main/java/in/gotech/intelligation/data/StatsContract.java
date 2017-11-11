package in.gotech.intelligation.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by anirudh on 13/02/16.
 */
public class StatsContract {
    public static final String CONTENT_AUTHORITY = "in.gotech.intelligation";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STATS = "stats";

    public static final class StatsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATS;

        public static Uri buildStatsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSensorStats(String sensorId) {
            return CONTENT_URI.buildUpon().appendPath(sensorId).build();
        }

        public static String getSensorIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME = "stats";
        public static final String COLUMN_SENSOR_ID = "sensor_id";
        public static final String COLUMN_SENSED_TIME = "sensed_time";
        public static final String COLUMN_SENSED_VALUE = "sensed_value";
    }
}
