/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.spyn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {
	
    public static final String KEY_TITLE = "title";
    public static final String KEY_TIME = "time";
    public static final String KEY_BODY = "body";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_KNIT = "knit";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LON = "longitude";
    public static final String KEY_ROWCOUNT = "rowcount";
    public static final String KEY_ROWID = "_id";
    
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_VIEW = "view";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, time text not null, body text not null, "
                    + "video int not null, audio int not null, photo int not null, knit int not null, "
                    + "location text not null, latitude double not null, longitude double not null, rowcount int not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String time, String body, int video, int audio, 
    		int photo, int knit, String location, double latitude, double longitude, int rowcount) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_VIDEO, video);
        initialValues.put(KEY_AUDIO, audio);
        initialValues.put(KEY_PHOTO, photo);
        initialValues.put(KEY_KNIT, knit);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_LOCATION_LAT, latitude);
        initialValues.put(KEY_LOCATION_LON, longitude);
        initialValues.put(KEY_ROWCOUNT, rowcount);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_TIME, KEY_BODY, KEY_VIDEO, KEY_AUDIO, KEY_PHOTO, KEY_KNIT,
                KEY_LOCATION, KEY_LOCATION_LAT, KEY_LOCATION_LON, KEY_ROWCOUNT}, 
                null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_TIME, KEY_BODY, KEY_VIDEO, KEY_AUDIO, 
                        KEY_PHOTO, KEY_KNIT, KEY_LOCATION, KEY_LOCATION_LAT,
                        KEY_LOCATION_LON, KEY_ROWCOUNT}, KEY_ROWID + "=" + rowId, 
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String time, String body, 
    		int video, int audio, int photo, int knit, 
    		String location, double latitude, double longitude) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_TIME, time);
        args.put(KEY_BODY, body);
        args.put(KEY_VIDEO, video);
        args.put(KEY_AUDIO, audio);
        args.put(KEY_PHOTO, photo);
        args.put(KEY_KNIT, knit);
        args.put(KEY_LOCATION, location);
        args.put(KEY_LOCATION_LAT, latitude);
        args.put(KEY_LOCATION_LON, longitude);
        Cursor cursor = fetchNote(rowId);
        String rowCount = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT));
        args.put(KEY_ROWCOUNT, rowCount);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
