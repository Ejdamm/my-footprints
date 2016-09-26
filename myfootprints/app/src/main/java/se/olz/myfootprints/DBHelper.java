package se.olz.myfootprints;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Coordinates.db";
    public static final String TABLE_NAME = "entry";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_SESSION = "session";
    public static final String COLUMN_NAME_TIMESTAMP = "accessedTimestamp";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE= "longitude";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INT_TYPE = " INT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_SESSION + LONG_TYPE + COMMA_SEP +
                    COLUMN_NAME_TIMESTAMP + LONG_TYPE + COMMA_SEP +
                    COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void clean() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    private static double roundDown5(double d) {
        return (long) (d * 1e5) / 1e5;
    }

    public boolean insertCoordinates(long session, long accessedTimestamp, double latitude, double longitude)
    {
        latitude = roundDown5(latitude);
        longitude = roundDown5(longitude);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_SESSION, session);
        contentValues.put(COLUMN_NAME_TIMESTAMP, accessedTimestamp);
        contentValues.put(COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(COLUMN_NAME_LONGITUDE, longitude);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public ArrayList<CoordinatesContainer> getAllEntries() {
        ArrayList<CoordinatesContainer> CoordinateEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null );
        res.moveToFirst();

        int id;
        long session, accessedTimestamp;
        double latitude, longitude;

        while(!res.isAfterLast()){
            id = res.getInt(res.getColumnIndex(COLUMN_NAME_ID));
            session = res.getLong(res.getColumnIndex(COLUMN_NAME_SESSION));
            accessedTimestamp = res.getLong(res.getColumnIndex(COLUMN_NAME_TIMESTAMP));
            latitude = res.getDouble(res.getColumnIndex(COLUMN_NAME_LATITUDE));
            longitude = res.getDouble(res.getColumnIndex(COLUMN_NAME_LONGITUDE));
            CoordinatesContainer row = new CoordinatesContainer(id, session, accessedTimestamp, latitude, longitude);
            CoordinateEntries.add(row);
            res.moveToNext();
        }
        res.close();
        return CoordinateEntries;
    }

    public void drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
    }
}
