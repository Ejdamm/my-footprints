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
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "myfootprints_users.db";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_EMAIL = "email";
    private static final String COLUMN_NAME_TOKEN = "token";
    private static final String COLUMN_NAME_LOGGEDIN = "loggedin";
    public static final String TAG = DBUsers.class.getSimpleName();

    public DBUsers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_EMAIL + " TEXT, " +
                COLUMN_NAME_TOKEN + " TEXT, " +
                COLUMN_NAME_LOGGEDIN + " BOOLEAN)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean isLoggedIn() {
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_LOGGEDIN + "=1 LIMIT 1";
        Cursor res =  db.rawQuery(sql, null);
        res.moveToFirst();
        if (res.getCount() > 0) {
            String email = res.getString(res.getColumnIndex(COLUMN_NAME_EMAIL));
            String token = res.getString(res.getColumnIndex(COLUMN_NAME_TOKEN));
            new User(email, token, -1);
            flag = true;
        }
        res.close();
        db.close();
        return flag;
    }

    public void insert(String email, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_EMAIL, email);
        contentValues.put(COLUMN_NAME_TOKEN, token);
        contentValues.put(COLUMN_NAME_LOGGEDIN, false);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_LOGGEDIN, 0);
        db.update(TABLE_NAME, contentValues, null, null);
        db.close();
    }

    public void login(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_LOGGEDIN, 1);
        db.update(TABLE_NAME, contentValues, COLUMN_NAME_EMAIL + " = ?", new String[] {email});
        isLoggedIn();
        db.close();
    }

    public void updateToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_TOKEN, User.getToken());
        db.update(TABLE_NAME, contentValues, COLUMN_NAME_EMAIL + " = ?", new String[] {User.getEmail()});
        db.close();
    }

    public boolean exist(String email) {
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_NAME_EMAIL + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_EMAIL + "='" + email + "' LIMIT 1";
        Cursor res =  db.rawQuery(sql, null);
        res.moveToFirst();
        if (res.getCount() > 0) {
            flag = true;
        }
        res.close();
        db.close();
        return flag;
    }
}
