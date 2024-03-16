package com.example.projetofinalcm2022.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projetofinalcm2022.models.History;
import com.example.projetofinalcm2022.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "battleshipDB";
    private static final String PROFILE_TABLE = "profiles";
    private static final String HISTORY_TABLE = "history";

    private static final String KEY_ID = "ID";

    // Column names for PROFILES
    private static final String KEY_NAME = "name";
    private static final String KEY_PASS = "password";
    private static final String KEY_PHOTO = "photo";

    // Column names for HISTORY
    private static final String KEY_MODE = "game_mode";
    private static final String KEY_DATE = "date";
    private static final String KEY_WINNER = "winner";
    private static final String KEY_NUMBER_SHOTS = "shots";
    private static final String KEY_BOAT_HITS = "hits";
    private static final String KEY_SUNKEN_SHIPS = "sunken";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryProfile = "CREATE TABLE " + PROFILE_TABLE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT," +
                KEY_PASS + " TEXT," +
                KEY_PHOTO + " BLOB)";
        String queryHistory = "CREATE TABLE " + HISTORY_TABLE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MODE + " TEXT," +
                KEY_DATE + " TEXT," +
                KEY_WINNER + " TEXT," +
                KEY_NUMBER_SHOTS + " INTEGER," +
                KEY_BOAT_HITS + " INTEGER," +
                KEY_SUNKEN_SHIPS + " INTEGER);";

        db.execSQL(queryProfile);
        db.execSQL(queryHistory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public Profile addProfile(Profile profile) {

        if (!profile.isValid())
            return null;
        if (this.getProfile(profile.getName()) != null)
            return null;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, profile.getName());
        contentValues.put(KEY_PASS, profile.getPassword());
        contentValues.put(KEY_PHOTO, profile.getBitmap());

        long id = db.insert(PROFILE_TABLE, null, contentValues);
        return getProfile(id);
    }

    public boolean deleteProfile(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROFILE_TABLE, KEY_ID + " = " + id, null) > 0;
    }

    public boolean updateProfile(long id, String name, String password, byte[] photo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PROFILE_TABLE + " WHERE " + KEY_ID + " = " + id + ";";
        Cursor cursor = db.rawQuery(query, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_PASS, password);
        contentValues.put(KEY_PHOTO, photo);

        return db.update(PROFILE_TABLE, contentValues, KEY_ID + " = " + id, null) > 0;
    }
    public Profile getProfile(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PROFILE_TABLE + " WHERE " + KEY_ID + " = " + id + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();
        return new Profile(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getBlob(3));
    }

    public Profile getProfile(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PROFILE_TABLE + " WHERE " + KEY_NAME + " = '" + name + "';";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();

        return new Profile(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getBlob(3));
    }

    public List<Profile> getProfiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Profile> allProfiles = new ArrayList<>();

        String query = "SELECT * FROM " + PROFILE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Profile profile = new Profile(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getBlob(3));
                allProfiles.add(profile);
            } while (cursor.moveToNext());
        }
        return allProfiles;
    }



    public long addHistory(History history) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MODE, history.getMode());
        contentValues.put(KEY_DATE, history.getDate());
        contentValues.put(KEY_NUMBER_SHOTS, history.getNumberOfShots());
        contentValues.put(KEY_WINNER, history.getWinner());
        contentValues.put(KEY_BOAT_HITS, history.getBoatHits());
        contentValues.put(KEY_SUNKEN_SHIPS, history.getSunkenShips());

        return db.insert(HISTORY_TABLE, null, contentValues);
    }

    public boolean deleteHistory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(HISTORY_TABLE, KEY_ID + " = " + id, null) > 0;
    }

    public History getHistory(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + HISTORY_TABLE + " WHERE " + KEY_ID + " = " + id + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() <= 0)
            return null;

        cursor.moveToFirst();
        return new History(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6));
    }

    public List<History> getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<History> histories = new ArrayList<>();

        String query = "SELECT * FROM " + HISTORY_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                History history = new History(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getInt(6));
                histories.add(history);
            } while (cursor.moveToNext());
        }
        return histories;
    }
}
