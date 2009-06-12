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

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class ScanMe extends Activity {
	
	public final static String ACTION_STORE = "store";
	public final static String ACTION_EXTRACT = "extract";
	public final static String IMAGE_PATH = "image_path";
	
	public final static String SCAN_ALL_LOCATIONS = "scan_all_locations";
	public final static String SCAN_LOCATION = "scan_location";
	
	public String myAction;
	public String myPath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		myPath = intent.getStringExtra(ScanMe.IMAGE_PATH);
		myAction = intent.getAction();
		
		//get the image however you want, here it is as a file
		File imageFile = new File(myPath);
		
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
		int[] location; // X,Y row count
		
		// XXX your code here
		
		
		// how to return data to the caller
		Intent returnIntent = new Intent();
		/*returnIntent.putExtra(ScanMe.SCAN_LOCATION, location);*/
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
	

	
}
