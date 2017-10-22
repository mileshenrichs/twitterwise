package wazizhen.twitterwise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Tweet;

/**
 * Created by Henrichs on 10/21/2017.
 * Helper class enables use of global static database reference.
 * All database calls and manipulates go through DatabaseHelper.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "Tweets";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_FAVORITES = "favorites";

    // favorites table columns
    private static final String KEY_ID = "id";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_USER_DISPLAY_NAME = "userDisplayName";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PROFILE_PIC_URL = "profilePicUrl";
    private static final String KEY_DATE = "date";

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES +
                " (" +
                KEY_ID + " BIGINT, " +
                KEY_CONTENT + " VARCHAR(255), " +
                KEY_USER_DISPLAY_NAME + " VARCHAR(255), " +
                KEY_USER_NAME + " VARCHAR(255), " +
                KEY_PROFILE_PIC_URL + " VARCHAR(255), " +
                KEY_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
            onCreate(db);
        }
    }

    private static DatabaseHelper sInstance;

    // ...

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Insert a Tweet into the database
    public void addFavorite(Tweet tweet) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, tweet.getId());
            values.put(KEY_CONTENT, tweet.getContent());
            values.put(KEY_USER_DISPLAY_NAME, tweet.getUserDisplayName());
            values.put(KEY_USER_NAME, tweet.getUserName());
            values.put(KEY_PROFILE_PIC_URL, tweet.getProfilePicUrl());
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            values.put(KEY_DATE, format.format(tweet.getDate()));

            db.insertOrThrow(TABLE_FAVORITES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(e.toString(), "Error while trying to add tweet to database");
        } finally {
            db.endTransaction();
            Log.i("we good", "should be in database now");
        }
    }

    public ArrayList<Tweet> getAllFavorites() {
        ArrayList<Tweet> tweets = new ArrayList<>();

        String TWEETS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_FAVORITES);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TWEETS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));
                    String userDisplayName = cursor.getString(cursor.getColumnIndex(KEY_USER_DISPLAY_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    String profilePicUrl = cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL));
                    String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                    Date date = format.parse(dateStr);
                    tweets.add(new Tweet(id, content, userDisplayName, userName, profilePicUrl, date));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(e.toString(), "Error while trying to get tweets from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tweets;
    }

    public boolean hasTweet(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM favorites WHERE id = " + String.valueOf(id), null);
        if(c.getCount() <= 0){
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    public void removeFavorite(Tweet tweet) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FAVORITES + " WHERE " + KEY_ID + " = " + tweet.getId());
    }
}
