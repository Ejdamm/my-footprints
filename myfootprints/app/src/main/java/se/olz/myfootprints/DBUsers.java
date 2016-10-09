package se.olz.myfootprints;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class DBUsers extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "myfootprints_users.db";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_EMAIL = "email";
    private static final String COLUMN_NAME_TOKEN = "token";
    private static final String COLUMN_NAME_LOGGEDIN = "loggedin";
    private static final String STRING_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + INT_TYPE + " PRIMARY KEY, " +
                    COLUMN_NAME_EMAIL + STRING_TYPE + COMMA_SEP +
                    COLUMN_NAME_TOKEN + STRING_TYPE + COMMA_SEP +
                    COLUMN_NAME_LOGGEDIN + BOOL_TYPE + " )";
    public static final String TAG = DBHelper.class.getSimpleName();

    public DBUsers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void clean() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }
}
