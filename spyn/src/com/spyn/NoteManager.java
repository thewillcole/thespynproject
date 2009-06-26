/*
 * Definitely stolen from Google's demo API
 * 
 * Will Cole for Spyn, May 1st 2009
 * 
 * NoteManager is the parent class to NoteEdit and NoteView because they share
 * so many similar functions. However, they have to be differentiated because
 * they deal with different kinds of objects in their respective layout files.
 */

package com.spyn;

import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RemoteViews.ActionException;

public abstract class NoteManager extends Activity {

	private Long mRowId;
    private NotesDbAdapter mDbHelper;
    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
     	// actions: ACTIVITY_CREATE=0; ACTIVITY_EDIT=1;
        mAction = getIntent().getAction();
    
        //Toast.makeText(NoteManager.this, "NoteManager: Action: " + mAction, Toast.LENGTH_SHORT).show();
        
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID) 
                							: null;
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras(); 
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) 
									: null;
		}
        
		populateFields();
    }
    
    protected abstract void populateFields();
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	//if (!mAction.equals(NotesDbAdapter.ACTION_CREATE)) {
    	saveState();
    	//}
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    protected abstract void saveState();
}
