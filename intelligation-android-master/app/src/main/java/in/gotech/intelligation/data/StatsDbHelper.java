package in.gotech.intelligation.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.gotech.intelligation.data.StatsContract.StatsEntry;

/**
 * Created by anirudh on 13/02/16.
 */
public class StatsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "stats.db";

    public StatsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_STATS_TABLE = "CREATE TABLE " + StatsEntry.TABLE_NAME + "(" +
                StatsEntry.COLUMN_SENSOR_ID + " TEXT PRIMARY KEY," +
                StatsEntry.COLUMN_SENSED_VALUE + " REAL," +
                StatsEntry.COLUMN_SENSED_TIME + " INTEGER)";
        db.execSQL(SQL_CREATE_STATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
