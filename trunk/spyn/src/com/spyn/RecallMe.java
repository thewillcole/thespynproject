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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecallMe extends Activity {
	
	private int[] MAX_ROW;
	private int SCREEN_WIDTH = 50;
	private int SCREEN_HEIGHT = 300;
	
	private NotesDbAdapter mDbHelper;
	private LinearLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//SETUP
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
		
		//EMULATION CODE
		MAX_ROW = ScanMe.processImg(bitmap);
		getRowIDsAndRowCounts();
		
		//TEARDOWN
		mDbHelper.close();
	}
	
    public void getRowIDsAndRowCounts() {
    	Cursor notes = mDbHelper.fetchAllNotes();
    	notes.moveToNext();
    	//notes.isBeforeFirst()
    	while (!notes.isAfterLast()) {
    		try {
    			
    			int yPos;
    			int xPos = (int) (SCREEN_WIDTH*Math.random()) + 10;
    			int rowID = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
    			int rowCount = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT)));
    			String title = notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
    			int photoID = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_PHOTO)));
    			int knitID = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_KNIT)));
    			int audioID = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_AUDIO)));
    			int videoID = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_VIDEO)));
    			
    			Toast.makeText(this, "ROWS SCANNED:" + MAX_ROW[0]+"Row: "+rowCount, Toast.LENGTH_LONG).show();
        		
    			if ((rowCount <= MAX_ROW[0])&&(rowCount!=0)) {
    				float fract = (float)rowCount/MAX_ROW[0];
    				yPos = SCREEN_HEIGHT-(int)Math.round((fract)*SCREEN_HEIGHT);
    				Toast.makeText(this, "Y Pos:" + yPos+" rowCount/MAX_ROW: "+fract, Toast.LENGTH_LONG).show();
    				addButton(xPos, yPos, rowID, rowCount, title, photoID, knitID, audioID, videoID);
    			}
    			
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
    
    public void callNoteEdit(int rowID) {
    	Intent i = new Intent(RecallMe.this, NoteEdit.class); // NoteView
        i.putExtra(NotesDbAdapter.KEY_ROWID, (long)rowID);
        i.setAction(NotesDbAdapter.ACTION_VIEW);
        startActivity(i);
        
    }
    
    public void addButton(int xPos, int yPos, final int rowID, final int rowCount, final String title,
    		final int photoID, final int knitID, final int audioID, final int videoID) {
        final ImageView imgb = new ImageView(this);
        imgb.setImageResource(R.drawable.pin_v1);
        imgb.setId(rowID);
        imgb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	ImageView myImageView = imgb;
            	int myRowID = rowID; //int id = imgb.getId();
            	String myTitle = title; int myRowCount = rowCount;
            	int myPhotoID = photoID; int myKnitID = knitID;
            	int myAudioID = audioID; int myVideoID = videoID;
            	
            	boolean hasBeenClicked = false;
            	
            	callNoteEdit(myRowID);
            	Toast.makeText(RecallMe.this, myTitle + "\n" + rowCount, Toast.LENGTH_SHORT).show();
            	
//            	if (hasBeenClicked) {
//            	callNoteEdit(myRowID);
//            	Toast.makeText(RecallMe.this, myTitle + "\n" + myRowID, Toast.LENGTH_SHORT).show();
//            	} else {
//            		hasBeenClicked = true;
//            		myImageView.setImageResource(R.drawable.pin_v2);
//            		//myImageView.setImageBitmap("");
//            	}
            }
        });    
        
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.width = 75; params1.height = 75;
        params1.leftMargin = 0; params1.topMargin = yPos;
        layout.addView(imgb, params1);
    }
	
}
