package in.gotech.intelligation.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.sql.SQLException;

/**
 * Created by anirudh on 13/02/16.
 */
public class StatsProvider extends ContentProvider{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    private StatsDbHelper mOpenHelper;
    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables("sensor_stats");
    }
    static final int STATS = 100;
    static final int STATS_WITH_SENSOR_ID = 101;
    private static final String sSensorSelection = StatsContract.StatsEntry.TABLE_NAME +
            "." + StatsContract.StatsEntry.COLUMN_SENSOR_ID + "= ?";
    @Override
    public boolean onCreate() {
        mOpenHelper = new StatsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case STATS_WITH_SENSOR_ID :
                retCursor = getWeatherByLocationSettingAndDate(uri, projection);
                break;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;


    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection) {
        String sensorId = StatsContract.StatsEntry.getSensorIdFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSensorSelection,
                new String[]{sensorId},
                null,
                null,
                null
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case STATS:
                return StatsContract.StatsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STATS:
                long _id = db.insert(StatsContract.StatsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = StatsContract.StatsEntry.buildStatsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        db.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted = 0;
        switch (match) {
            case STATS:
                numDeleted = db.delete(StatsContract.StatsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StatsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, StatsContract.PATH_STATS, STATS);
        matcher.addURI(authority, StatsContract.PATH_STATS + "/*", STATS_WITH_SENSOR_ID);

        return matcher;

    }
}
