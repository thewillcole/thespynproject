/*
 * Daniela Rosner & Will Cole
 * 
 * Spyn!
 * 
 * ScanMe is passed an image path and called with an intent like so:
 * Intent i = new Intent(this, ScanMe.class);
 * i.putExtra(ScanMe.IMAGE_PATH, imagePath);
 * // imagePath is the path to the png that will be scanned
 * startActivityForResult(i, REQUEST_CODE);
 * // the REQUEST_CODE will be either ScanMe.ACTION_STORE or ACTION_EXTRACT
 * 
 * 
 * ScanMe returns either:
 *      the X,Y row count (if it was called with ACTION_STORE)
 *      or all the X,Y positions of memories (if it was called with ACTION_EXTRACT)
 * It returns this data by storing it into an intent and putting the intent into setResult.
 */

package com.spyn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanMe extends Activity {
	
	public final static String INTENT_BITMAP="bitmap";
	public final static String INTENT_AVGROW="avgrow";
	
	public final static String ACTION_STORE = "store";
	public final static String ACTION_EXTRACT = "extract";
	public final static String IMAGE_PATH = "image_path";
	
	public final static String SCAN_ALL_LOCATIONS = "scan_all_locations";
	public final static String SCAN_LOCATION = "scan_location";
	
	public String myAction;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		myAction = intent.getAction();
		
		if (myAction.equals(ScanMe.ACTION_STORE)) {
			// scan for a new memory
			callStoreScan();
		} else if (myAction.equals(ScanMe.ACTION_EXTRACT)) {
			// scan a completed knit for all memories in it
			callExtractScan();
		} else {
			Toast.makeText(ScanMe.this, "SCANME:\nERROR:\nIncorrect Action", Toast.LENGTH_LONG).show();
		}
	}
	
	
	// scan for a new memory
	public void callStoreScan() {
		Intent intent = getIntent();
		Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		int avgrow = countStitches(bitmap);
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra(INTENT_AVGROW, avgrow);
		setResult(RESULT_OK, returnIntent);
		
		//this is a good, cheap way to exit the ScanMe activity
		//it just emulates the user pressing the back button
		onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN));
	}
	
	
	// scan a completed knit for all memories in it
	public void callExtractScan() {
		int[] all_locations; // list of X,Y positions of memories
		
		// XXX your code here
		
		
		// how to return data to the caller:
		// put data into the returnIntent using putExtra(stringName, 
		Intent returnIntent = new Intent();
		/*returnIntent.putExtra(ScanMe.SCAN_ALL_LOCATIONS, all_locations);*/
		setResult(RESULT_OK, returnIntent);
		
		//this is a good, cheap way to exit the ScanMe activity
		//it just emulates the user pressing the back button
		onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN));
	}
	
	
	//---------------------
	public int countStitches(Bitmap bitmap) {
		//URL imgURL;
		//Bitmap bmImg = null;
		ImageView image = new ImageView(this); // display Object build into Android
		setContentView(image); // displays ImageView on screen
		return processImg(bitmap);
	}

	public int processImg(Bitmap mybit) {
		int width = mybit.getWidth();
		int height = mybit.getHeight();
		int[] pixels = new int[width*height];
		mybit.getPixels(pixels,0,width,0,0,width, height);
		//byte[] picData = mybit.getData();

		if(width==-1 || height==-1){
			// the image has not loaded.
			mybit = (Bitmap)null;
		}
		final int STHRESH = 10; // Threshold for stitch size ** this should change 
		final int MAXROWS = width;
		final int MAXCOLS = height;

		int[][] matrix = new int[MAXROWS][MAXCOLS];
		System.out.println("MAXROWS " + MAXROWS);
		System.out.println("MAXCOLS " + MAXCOLS);
		int count = 0;
		int countoff = 0;
		int edgeBool = 0;
		float avg1 =1;
		float avg2 =1;
		float avg3 =1;
		float avg4 =1;
		float avg5 =1;
		float avg6 =1;
		float avg7 =1;
		float avg8 =1;
		float avg9 =1;
		for (int row=1; row<MAXROWS-1; row++) {
			for (int col=1; col<MAXCOLS-1; col++) {
				avg1 = (((mybit.getPixel(row+1,col+1) >> 16) & 0xFF) + ((mybit.getPixel(row+1,col+1) >> 8) & 0xFF)+ ((mybit.getPixel(row+1,col+1)) & 0xFF))/3;
				avg2 = (((mybit.getPixel(row+1,col-1) >> 16) & 0xFF) + ((mybit.getPixel(row+1,col-1) >> 8) & 0xFF)+ ((mybit.getPixel(row+1,col-1)) & 0xFF))/3;
				avg3 = (((mybit.getPixel(row+1,col) >> 16) & 0xFF) + ((mybit.getPixel(row+1,col) >> 8) & 0xFF)+ ((mybit.getPixel(row+1,col)) & 0xFF))/3;

				avg4 = (((mybit.getPixel(row,col+1) >> 16) & 0xFF) + ((mybit.getPixel(row,col+1) >> 8) & 0xFF)+ ((mybit.getPixel(row,col+1)) & 0xFF))/3;
				avg5 = (((mybit.getPixel(row,col-1) >> 16) & 0xFF) + ((mybit.getPixel(row,col-1) >> 8) & 0xFF)+ ((mybit.getPixel(row,col-1)) & 0xFF))/3;;
				avg6 = (((mybit.getPixel(row,col) >> 16) & 0xFF) + ((mybit.getPixel(row,col) >> 8) & 0xFF)+ ((mybit.getPixel(row,col)) & 0xFF))/3;

				avg7 = (((mybit.getPixel(row-1,col+1) >> 16) & 0xFF) + ((mybit.getPixel(row-1,col+1) >> 8) & 0xFF)+ ((mybit.getPixel(row-1,col+1)) & 0xFF))/3;
				avg8 = (((mybit.getPixel(row-1,col-1) >> 16) & 0xFF) + ((mybit.getPixel(row-1,col-1) >> 8) & 0xFF)+ ((mybit.getPixel(row-1,col-1)) & 0xFF))/3;
				avg9 = (((mybit.getPixel(row-1,col) >> 16) & 0xFF) + ((mybit.getPixel(row-1,col) >> 8) & 0xFF)+ ((mybit.getPixel(row-1,col)) & 0xFF))/3;

				float avg = avg5;
				if ((((avg1+avg2+avg3)-(avg7+avg8+avg9))>1)||((avg3+avg6+avg9)-(avg1+avg4+avg7))>1)
				{
					matrix[row][col] = 1;
				} else {
					matrix[row][col] = 0;
				}
			}  // for col
			// for row

			float avgrow = 0;
			float totalrow = 0;
			for (row=1; row<MAXROWS-1; row++) {
				for (int col=1; col<MAXCOLS-1; col++) {
					if ((matrix[row][col]==1)&&(countoff<STHRESH)) {
						if ((edgeBool == 0)){//&&(countoff>0)) {
							count++;
							edgeBool = 1;
							countoff = 0;
						}
						countoff++;
						//System.out.println("Number of rows: " + count +", " +countoff);
					} else {
						if (edgeBool == 1) {
							edgeBool = 0;
						}
					}
				}
				totalrow += count;
				count=0;
				countoff=0;
			}
			avgrow = totalrow/(MAXROWS-1);
			System.out.println("Blue image num of rows: " + avgrow +", " +countoff);
			TextView tv = new TextView(this);
			tv.setText("Dimensions: "+width+" x "+height+"\n Test data:"+" "+((((mybit.getPixel(20,20) >> 16) & 0xFF) + ((mybit.getPixel(20,20) >> 8) & 0xFF)+ ((mybit.getPixel(20,20)) & 0xFF))/3)+".\n Total number of rows: " + totalrow+ " Average num: "+avgrow +", " +countoff+" "+matrix[20][20]+" "+matrix[21][21]+" "+matrix[22][22]+" "+matrix[23][23]+" "+avg5+" "+avg6+" "+avg7+" "+avg8+" "+avg9);
			setContentView(tv);
			// return row count (this takes int ceiling of the float)
			//Toast.makeText(this, "ROWCOUNTIZZLE: " + avgrow, Toast.LENGTH_LONG * 10).show();
			Float avgRow = new Float(avgrow);
			return avgRow.intValue() + 1;

		}
		// error: the for-loop was never executed
		return -1;
	}
	//---------------------

	
}
