package se.olz.myfootprints;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 15;
    private static final String DATABASE_NAME= "my_footprints.db";
    private static String TABLE_NAME;
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_SESSION = "session";
    private static final String COLUMN_NAME_TIMESTAMP = "accessedTimestamp";
    private static final String COLUMN_NAME_LATITUDE = "latitude";
    private static final String COLUMN_NAME_LONGITUDE= "longitude";
    public static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context, String email) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        TABLE_NAME = "`" + email + "`";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_SESSION + " INTEGER, " +
                COLUMN_NAME_TIMESTAMP + " INTEGER, " +
                COLUMN_NAME_LATITUDE + " DOUBLE, " +
                COLUMN_NAME_LONGITUDE +  " DOUBLE)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(sql, null);
        int id;
        res.moveToFirst();
        if (res.getCount() > 0)
            id = res.getInt(res.getColumnIndex(COLUMN_NAME_ID));
        else
            id = 0;
        res.close();
        db.close();
        return id;
    }

    public boolean insertMultiple(ArrayList<RawPosition> pos)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(RawPosition rawPosition : pos){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME_ID, rawPosition.getId());
            contentValues.put(COLUMN_NAME_SESSION, rawPosition.getSession());
            contentValues.put(COLUMN_NAME_TIMESTAMP, rawPosition.getAccessedTimestamp());
            contentValues.put(COLUMN_NAME_LATITUDE, rawPosition.getLatitude());
            contentValues.put(COLUMN_NAME_LONGITUDE, rawPosition.getLongitude());
            db.insert(TABLE_NAME, null, contentValues);
        }
        db.close();
        return true;
    }

    public boolean insertOne(RawPosition pos)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_SESSION, pos.getSession());
        contentValues.put(COLUMN_NAME_TIMESTAMP, pos.getAccessedTimestamp());
        contentValues.put(COLUMN_NAME_LATITUDE, pos.getLatitude());
        contentValues.put(COLUMN_NAME_LONGITUDE, pos.getLongitude());
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public ArrayList<RawPosition> getEntries(String sql) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final ArrayList<RawPosition> CoordinateEntries = new ArrayList<>();
        Cursor res =  db.rawQuery(sql, null );
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
            RawPosition row = new RawPosition(id, session, accessedTimestamp, latitude, longitude);
            CoordinateEntries.add(row);
            res.moveToNext();
        }
        res.close();
        db.close();
        return CoordinateEntries;
    }

    public ArrayList<RawPosition> getAfter(int index) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_ID + " > " + index;
        return getEntries(sql);
    }

    public ArrayList<RawPosition> getAllEntries() {
        String sql =  "SELECT * FROM " + TABLE_NAME;
        return getEntries(sql);
    }

}
