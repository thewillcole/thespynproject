/*
 * Daniela Rosner & Will Cole
 * 
 * Spyn!
 * 
 * RECALL
 * 
 */

package com.spyn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecallMe extends Activity {
	
	private int MAX_ROW;
	//private int[] MAX_ROW;
	private int TOTAL_ROWS;
	private int SCREEN_WIDTH = 50;
	private int SCREEN_HEIGHT = 400;
	
	private NotesDbAdapter mDbHelper;
	private LinearLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SETUP
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
		Intent intent = getIntent();
		//Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		Bitmap bitmap_to_show = null;
		Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		int oldW = bitmap.getWidth();
		if (Spyn.NUM_SCANS == 2) {
			Bitmap bitmap2 = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP_2);
			bitmap_to_show = getStitchedImage(2,bitmap,bitmap2,null);
		} else if (Spyn.NUM_SCANS == 3){
			Bitmap bitmap2 = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP_2);
			Bitmap bitmap3 = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP_3);
			bitmap_to_show = getStitchedImage(3,bitmap,bitmap2,bitmap3);
		} 
		
		// For debugging
		if (bitmap_to_show == null) {
			bitmap_to_show = bitmap;
		} 
		/*
		Bitmap finalBit = Bitmap.createBitmap(bitmap_to_show, 0, 0, 
				(int)(Math.round(bitmap_to_show.getWidth()/Spyn.NUM_SCANS)), bitmap_to_show.getHeight());
		Toast.makeText(this, "WIDTH OF IMG: " + bitmap_to_show.getWidth()+", "+oldW, Toast.LENGTH_LONG).show();
		*/		
		layout = new LinearLayout(this);
        layout.setLayoutParams( new
                        ViewGroup.LayoutParams( LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT ) ); 
        
        
        //int mWid = (int)(Math.round((float)bitmap_to_show.getWidth()/(float)Spyn.NUM_SCANS));
        //int mSpace = layout.getWidth() - mWid;
        //int mWBuff = (int)Math.round(mSpace/2);
        BitmapDrawable drawable = new BitmapDrawable(bitmap_to_show);
        
        //drawable.setBounds(mWBuff, 0, layout.getWidth()-mWBuff, 0);
        //layout.setMinimumWidth((int)(Math.round(bitmap_to_show.getWidth()/Spyn.NUM_SCANS)));
        //BitmapDrawable drawable = new BitmapDrawable(finalBit);
        layout.setBackgroundDrawable(drawable);	
        
        this.setContentView(layout); 
       
		// SAVE MAX ROW
		MAX_ROW = Spyn.TOTAL_ROWCOUNT;
		
		// RESET ROW COUNT
		//Spyn.TOTAL_ROWCOUNT = 0;
		Spyn.resetRowCount();
		getRowIDsAndRowCounts();
		
		//TEARDOWN
		mDbHelper.close();
	}
	
	public Bitmap getStitchedImage(int num_imgs, Bitmap bit1, 
			Bitmap bit2, Bitmap bit3) {
		Bitmap resizedBitmap1 = null;
		Bitmap resizedBitmap2 = null;
		Bitmap resizedBitmap3 = null;
		resizedBitmap1 = resizeBitmap(num_imgs,bit1);
		if (num_imgs==3) {
			resizedBitmap3 = resizeBitmap(num_imgs,bit3);
			resizedBitmap2 = resizeBitmap(num_imgs,bit2);
		} else if (num_imgs==2) {
			resizedBitmap2 = resizeBitmap(num_imgs,bit2);
		}
		Bitmap bitmap_to_show = Bitmap.createBitmap(resizedBitmap1.getWidth(),
				(resizedBitmap1.getHeight()*num_imgs),Config.ARGB_8888);
        //copyImage(num_imgs,bitmap_to_show,resizedBitmap1,resizedBitmap2,resizedBitmap3);
        copyImage(num_imgs,bitmap_to_show,resizedBitmap3,resizedBitmap2,resizedBitmap1);
        return bitmap_to_show;
	}
	
	public void copyImage(int num_imgs,Bitmap bmDest,Bitmap bmSrc1,Bitmap bmSrc2,Bitmap bmSrc3){
		/** For taking an image of knit bottom to knit top **/
		if (num_imgs==2) {
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc1.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc2.getPixel(x, y));
	        	}
			}
	    } else if (num_imgs==3){
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc1.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc2.getPixel(x, y));
					bmDest.setPixel(x, y+(bmSrc2.getHeight()*2), bmSrc3.getPixel(x, y));
	        	}
			}
	    }
		
		/*if (num_imgs==2) {
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc2.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc1.getPixel(x, y));
	        	}
			}
	    } else if (num_imgs==3){
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc1.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc3.getPixel(x, y));
					bmDest.setPixel(x, y+(bmSrc2.getHeight()*2), bmSrc2.getPixel(x, y));
	        	}
			}
	    }
		*/
		/** For taking an image knit top to knit bottom **/
		/*if (num_imgs==2) {
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc1.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc2.getPixel(x, y));
	        	}
			}
	    } else if (num_imgs==3){
			for(int x = 0x00; x <  bmSrc1.getWidth(); x++){
				for(int y = 0x00; y < bmSrc1.getHeight(); y++){
					bmDest.setPixel(x, y, bmSrc1.getPixel(x, y));
					bmDest.setPixel(x, y+bmSrc2.getHeight(), bmSrc2.getPixel(x, y));
					bmDest.setPixel(x, y+(bmSrc2.getHeight()*2), bmSrc3.getPixel(x, y));
	        	}
			}
	    }*/
	}

	public Bitmap resizeBitmap(int num_imgs,Bitmap bitmap) {
		int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        int screenWidth = 320;
        int screenHeight = 480;
        int newWidth = (int)screenWidth/num_imgs; 
        int newHeight = (int)screenHeight/num_imgs;
        float scaleWidth = ((float) newWidth) / w;
        float scaleHeight = ((float) newHeight) / h;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w,h, matrix, true);
        return resizedBitmap;
	}
	
    public void getRowIDsAndRowCounts() {
    	boolean foundmessage = false;
    	Cursor notes = mDbHelper.fetchAllNotes();
    	notes.moveToNext();
    	//notes.isBeforeFirst()
    	while (!notes.isAfterLast()) {
    		try {
    			
    			int yPos;
    			int xPos = (int) ((SCREEN_WIDTH)*Math.random()) + 15; //old: int xPos = (int) (SCREEN_WIDTH*Math.random()) + 15;
    			// Adjust for middle of screen
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
    			
    			//Toast.makeText(this, "ROWS SCANNED:" + MAX_ROW+" Row: "+rowCount, Toast.LENGTH_LONG).show();
        		
    			if ((rowCount <= MAX_ROW)&&(rowCount!=0)) {
    				foundmessage = true;
    				float fract = (float)rowCount/MAX_ROW;
    				yPos = SCREEN_HEIGHT-(int)Math.round((fract)*SCREEN_HEIGHT);
    				//Toast.makeText(this, "Y Pos:" + yPos+" rowCount/MAX_ROW: "+fract, Toast.LENGTH_LONG).show();
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
    	if (foundmessage==false){
			Toast.makeText(this, "Spyn didn't find any messages. \nPlease try again.", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Spyn scanned "+MAX_ROW+" rows.", Toast.LENGTH_LONG).show();	
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
            	Toast.makeText(RecallMe.this, "Recalling message attached to row " + rowCount+".", Toast.LENGTH_SHORT).show();
            	
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
