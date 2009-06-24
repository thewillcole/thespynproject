/*
 * Daniela Rosner & Will Cole
 * 
 * Spyn!
 * 
 * RECALL
 * 
 */

package com.spyn;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecallMe extends Activity {
	
	private int MAX_ROW;
	private NotesDbAdapter mDbHelper;
	private LinearLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
		Intent intent = getIntent();
		Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		
        layout = new LinearLayout(this);
        layout.setLayoutParams( new
                        ViewGroup.LayoutParams( LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT ) ); 
        layout.setBackgroundDrawable(new BitmapDrawable(bitmap));
        this.setContentView(layout); 
		
		Toast.makeText(this, "RECALL is working", Toast.LENGTH_LONG).show();
		
		
		//EMULATION CODE
		MAX_ROW = 23;
		getRowIDsAndRowCounts();
		addButton(50, 30, 0);
		addButton(34, 89, 0);
	}
	
    public void getRowIDsAndRowCounts() {
    	Cursor notes = mDbHelper.fetchAllNotes();
    	int[] rowIDs = new int[notes.getCount()];
    	int[] rowCounts = new int[notes.getCount()];
    	notes.moveToNext();
    	for (int i = 0; i < notes.getCount(); i++) {
    		try {	
    			rowIDs[i] = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
    			
    			rowCounts[i] = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT)));
    		} catch (NumberFormatException nfe) {
    			Toast.makeText(this, "RECALL:\nERROR:\nNFE\n" + nfe, Toast.LENGTH_LONG).show();
    		} catch (IllegalArgumentException iae) {
    			Toast.makeText(this, "RECALL:\nERROR:\nIAE\n" + iae, Toast.LENGTH_LONG).show();
    		} catch (CursorIndexOutOfBoundsException cioobe) {
    			Toast.makeText(this, "RECALL:\nERROR:\nCIOOB\n" + cioobe, Toast.LENGTH_LONG).show();
    		} catch (Exception e) {
    			Toast.makeText(this, "RECALL:\nERROR:\nE\n" + e, Toast.LENGTH_LONG).show();
    		}
    		notes.moveToNext();
    	}
    }
    
    public void callNoteEdit() {
    	Toast.makeText(RecallMe.this, "BUTTON!", Toast.LENGTH_LONG).show();
    }
    
    public void addButton(int xPos, int yPos, int rowID) {
        ImageButton imgb = new ImageButton(this);
        imgb.setImageResource(R.drawable.pin_v3);
        imgb.setAdjustViewBounds(true);
        imgb.setId(rowID);// was 5
        imgb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// HOW DO I GET THE ID?
            	callNoteEdit();
            }
        });    
        
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.width = 30; params1.height = 30;
        params1.leftMargin = xPos; params1.topMargin = 400-yPos;
        layout.addView(imgb, params1);
    }
	
}
