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
    public static final String KEY_STITCHCOUNT = "stitchcount";
    public static final String KEY_ROWID = "_id";
    
    public static final String PROJ_TITLE = "project_title";
    public static final String PROJ_DESCRIPTION = "project_description";
    public static final String PROJ_BACKGROUND= "background_image";
    public static final String PROJ_CREATE = "create_time";
    public static final String PROJ_MODIFY = "modify_time";
    public static final String PROJ_STITCH = "stitch";
    public static final String PROJ_ROWID = "_id";
    
    
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_VIEW = "view";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    //public static long PROJ_ID=-1;
    
    /**
     * Database creation sql statement
     */
    private static final String NOTES_TABLE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, time text not null, body text not null, "
                    + "video int not null, audio int not null, "
                    + "photo int not null, knit int not null, location text not null, "
                    + "latitude double not null, longitude double not null, "
                    + "rowcount int not null, stitchcount int not null);";

    private static final String DATABASE_PROJECT_CREATE =
        "create table project (_id integer primary key autoincrement, "
    			+ "project_description text not null,"
                + "project_title text not null, create_time text not null, "
                + "modify_time text not null, "
                + "stitch integer not null, "
                + "background_image text not null);";

    
    private static final String DATABASE_NAME = "spyn.db";
    private static final String DATABASE_TABLE_NOTES = "notes";
    private static final String DATABASE_TABLE_PROJECT = "project";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(NOTES_TABLE_CREATE);
            db.execSQL(DATABASE_PROJECT_CREATE);
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
    		int photo, long knit, String location, double latitude, double longitude, int rowcount, int stitchcount) {
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
        initialValues.put(KEY_STITCHCOUNT, stitchcount);

        return mDb.insert(DATABASE_TABLE_NOTES, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE_NOTES, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database 
     * 
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotesforBackup() {
    	return mDb.rawQuery("SELECT DISTINCT "+KEY_ROWID+", "+
    			KEY_TITLE+", "+
    			KEY_TIME+", "+KEY_BODY+", "+
    			KEY_VIDEO+", "+KEY_AUDIO+", "+
    			KEY_PHOTO+", "+ KEY_KNIT+", "+
                KEY_LOCATION+", "+KEY_LOCATION_LAT+", "+
                KEY_LOCATION_LON+", "+KEY_ROWCOUNT+", "+
                KEY_STITCHCOUNT+
                " FROM "+DATABASE_TABLE_NOTES,null);
    }
    
    /**
     * Return a Cursor over the list of all notes in the database
     * where the knit_id is project_id
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {
    	String mSql = "SELECT * FROM "+ DATABASE_TABLE_NOTES+
    		" WHERE "+KEY_KNIT+"="+Spyn.PROJ_ID+";";
    	
    	return mDb.rawQuery(mSql, null);
    }
    
    /**
     * Return a Cursor over the last rowId 
     * where the knit_id is project_id
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchLastNoteRowID() {
    	String mSql = "SELECT MAX("+KEY_ROWID+") FROM "+ DATABASE_TABLE_NOTES +";"; //+
    		//" WHERE "+KEY_KNIT+"="+Spyn.PROJ_ID+";";
    	
    	return mDb.rawQuery(mSql, null);
    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId, long knitId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_NOTES, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_TIME, KEY_BODY, KEY_VIDEO, KEY_AUDIO, 
                        KEY_PHOTO, KEY_KNIT, KEY_LOCATION, KEY_LOCATION_LAT,
                        KEY_LOCATION_LON, KEY_ROWCOUNT,
                        KEY_STITCHCOUNT}, KEY_ROWID + "=" + rowId
                        +" AND "+KEY_KNIT+"="+knitId, 
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
    		int video, int audio, int photo, long knit, 
    		String location, double latitude, double longitude) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_TIME, time);
        args.put(KEY_BODY, body);
        args.put(KEY_VIDEO, video);
        args.put(KEY_AUDIO, audio);
        args.put(KEY_PHOTO, photo);
        args.put(KEY_KNIT, Spyn.PROJ_ID);
        args.put(KEY_LOCATION, location);
        args.put(KEY_LOCATION_LAT, latitude);
        args.put(KEY_LOCATION_LON, longitude);
        Cursor cursor = fetchNote(rowId,Spyn.PROJ_ID);
        String rowCount = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT));
        args.put(KEY_ROWCOUNT, rowCount);
        String stitchcount = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_STITCHCOUNT));
        args.put(KEY_STITCHCOUNT, stitchcount);

        return mDb.update(DATABASE_TABLE_NOTES, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchProject(long projId) throws SQLException {
    	String mSql = "SELECT * FROM "+ DATABASE_TABLE_PROJECT+
		" WHERE "+PROJ_ROWID+"="+projId+";";
	
    	return mDb.rawQuery(mSql, null);
    	
    	/*
        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_NOTES, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_TIME, KEY_BODY, KEY_VIDEO, KEY_AUDIO, 
                        KEY_PHOTO, KEY_KNIT, KEY_LOCATION, KEY_LOCATION_LAT,
                        KEY_LOCATION_LON, KEY_ROWCOUNT}, KEY_ROWID + "=" + rowId
                        +" AND "+KEY_KNIT+"="+knitId, 
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }*/
        //return mCursor;

    }
    
    /**
     * Insert into the project table using the details provided. 
     * 
     * @param title value to set the project title to
     * @param description value to set project description to
     * @param time value of the date and time of creation
     * @return Cursor positioned to matching project, if successful
     */
    public Cursor insertProject(String title, String description, String back_img,String time,int stitch) {
        Cursor c = mDb.rawQuery("INSERT INTO " + 
        		DATABASE_TABLE_PROJECT+" ("+PROJ_TITLE+","
        		+PROJ_DESCRIPTION+","+PROJ_BACKGROUND+","
        		+PROJ_CREATE+", "+PROJ_MODIFY+", "+PROJ_STITCH+") VALUES (\""
        		+title+"\",\""+description+"\",\""+back_img+"\",\""
        		+time+"\", \""+time+"\", \""+stitch+"\");",null); 
        return c;
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
    public boolean updateProject(long projId, String title, String time, 
    		String body, String photo, int stitch) {
        ContentValues args = new ContentValues();
        args.put(PROJ_TITLE, title);
        args.put(PROJ_MODIFY, time);
        args.put(PROJ_DESCRIPTION, body);
        args.put(PROJ_BACKGROUND, photo);
        args.put(PROJ_STITCH, stitch);
        
        return mDb.update(DATABASE_TABLE_PROJECT, args, PROJ_ROWID + "=" + projId, null) > 0;
    }
    /**
     * Update the project table using the details provided. 
     * 
     * @param title value to set the project title to
     * @param description value to set project description to
     * @param time value of the date and time of creation
     * @return Cursor positioned to matching project, if successful
     */
    public Cursor updateProjectRaw (long rowid, String title, String description, String back_img,String time) {
        Cursor c = mDb.rawQuery("UPDATE " + 
        		DATABASE_TABLE_PROJECT
        		+" SET "
        		+PROJ_TITLE+"=\""+title
        		+"\",  "
        		+PROJ_DESCRIPTION+"=\""+description
        		+"\",  "
        		+PROJ_BACKGROUND+"=\""+back_img
        		+"\",  "
        		+PROJ_MODIFY+"=\""+time+"\""
        		+" WHERE "
        		+PROJ_ROWID+"="+rowid+");",null); 
        return c;
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
    public long createProject(String title, String time, String body, 
    		String photo, int stitch) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(PROJ_TITLE, title);
        initialValues.put(PROJ_CREATE, time);
        initialValues.put(PROJ_MODIFY, time);
        initialValues.put(PROJ_DESCRIPTION, body);
        initialValues.put(PROJ_BACKGROUND, photo);
        initialValues.put(PROJ_STITCH, stitch);
        return mDb.insert(DATABASE_TABLE_PROJECT, null, initialValues);
        
    }
    
    /**
     * Select the project details using the details provided. 
     * 
     * @param title value to set the project title to
     * @param description value to set project description to
     * @param time value of the date and time of creation
     * @return true if the project was successfully created, false otherwise
     */
    public Cursor fetchAllProjects() {
        Cursor c = mDb.rawQuery("SELECT "+PROJ_TITLE+", "+PROJ_ROWID+" FROM " + 
        		DATABASE_TABLE_PROJECT,null); 
        return c;
    }
    
    
}
