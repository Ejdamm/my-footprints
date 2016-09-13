package se.olz.myfootsteps;


import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Coordinates.db";
    public static final String TABLE_NAME = "entry";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_SESSIONID= "sessionId";
    public static final String COLUMN_NAME_TIMESTAMP= "accessedTimestamp";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE= "longitude";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_SESSIONID + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME_TIMESTAMP + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

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

    public boolean insertCoordinates(int sessionID, int accessedTimestamp, double latitude, double longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_SESSIONID, sessionID);
        contentValues.put(COLUMN_NAME_TIMESTAMP, accessedTimestamp);
        contentValues.put(COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(COLUMN_NAME_LONGITUDE, longitude);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

}
