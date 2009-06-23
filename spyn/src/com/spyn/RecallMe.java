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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RecallMe extends Activity {
	
	private int MAX_ROW;
	private NotesDbAdapter mDbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
		Intent intent = getIntent();
		Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(bitmap);
		setContentView(imageView);
		
		Toast.makeText(this, "RECALL is working", Toast.LENGTH_LONG).show();
		
		
		//EMULATION CODE
		MAX_ROW = 23;
		getRowIDsAndRowCounts();
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
    
    public void addButton() {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams( new
                        ViewGroup.LayoutParams( LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT ) ); 
        
       //setContentView(new SampleView(this));
        ImageButton imgb1 = new ImageButton(this);
        imgb1.setImageResource(R.drawable.pin_v3);
        imgb1.setAdjustViewBounds(true);
        imgb1.setId(5);
        imgb1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Call Note_Edit
            }
        });    
        
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams
        (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    layout.addView(imgb1, params1); 
        this.setContentView(layout); 
    }
	
}
