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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanMe extends Activity {
	
	public final static String INTENT_BITMAP="bitmap";
	public final static String INTENT_BITMAP_2="bitmap2";
	public final static String INTENT_BITMAP_3="bitmap3";
	
	public final static String INTENT_AVGROW="avgrow";
	
	public final static String ACTION_STORE = "store";
	public final static String ACTION_EXTRACT = "extract";
	public final static String IMAGE_PATH = "image_path";
	
	public final static String SCAN_ALL_LOCATIONS = "scan_all_locations";
	public final static String SCAN_LOCATION = "scan_location";

	public final static String DRAW_ON_TOP = "draw_on_top";
	
	public String myAction;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		myAction = intent.getAction();
		
		if (myAction.equals(ScanMe.DRAW_ON_TOP)) {
			DrawOnTopMethod mDraw = new DrawOnTopMethod(this);
			addContentView(mDraw, new LayoutParams
			(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		} else if (myAction.equals(ScanMe.ACTION_STORE)) {
			// scan for a new memory
			callStoreMultiScan();
			
			// Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
			// callStoreScan(bitmap);
		} else if (myAction.equals(ScanMe.ACTION_EXTRACT)) {
			
		// scan a completed knit for all memories in it
			
		} else {
			Toast.makeText(ScanMe.this, "SCANME:\nERROR:\nIncorrect Action", Toast.LENGTH_LONG).show();
		}
	}
	/*
	class DrawOnTop extends View {

    	public DrawOnTop(Context context) {
    	super(context);
    	// TODO Auto-generated constructor stub
    	}

    	@Override
    	protected void onDraw(Canvas canvas) {
    	// TODO Auto-generated method stub

    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL);
    	paint.setColor(Color.RED);
    	paint.setTextAlign(Align.LEFT);
    	paint.setTextSize(24);
    	canvas.drawText("Place bottom of knit here.", 20, 440, paint);

    	super.onDraw(canvas);
    	}

    }*/
    
	// multiple scans for a new memory
	public void callStoreMultiScan() {
		Intent intent = getIntent();
		Intent returnIntent = new Intent();
		returnIntent.putExtra(INTENT_AVGROW, Spyn.TOTAL_ROWCOUNT);
		// Spyn.TOTAL_ROWCOUNT = 0;
		setResult(RESULT_OK, returnIntent);
		//this is a good, cheap way to exit the ScanMe activity
		//it just emulates the user pressing the back button
		onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN));
	}
	
	/* scan for a new memory
	public void callStoreScan(Bitmap bitmap) {
		Intent intent = getIntent();
		//Bitmap bitmap = (Bitmap) intent.getExtras().get(ScanMe.INTENT_BITMAP);
		
		int avgrow = countStitches(bitmap)[0];
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra(INTENT_AVGROW, avgrow);
		setResult(RESULT_OK, returnIntent);
		
		//this is a good, cheap way to exit the ScanMe activity
		//it just emulates the user pressing the back button
		onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN));
	}*/
	

	// scan a completed knit for all memories in it
	public void callExtractScan() {
		//int[] all_locations; // list of X,Y positions of memories
		
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
	public int[] countStitches(Bitmap bitmap) {
		ImageView image = new ImageView(this); // display Object build into Android
		setContentView(image); // displays ImageView on screen
		int[] res = processImg(bitmap);
		//Toast.makeText(ScanMe.this, "Attaching a message to row "+res[0] + ".", Toast.LENGTH_LONG).show();
		return res;
	}

	public static int[] processImg(Bitmap mybit) {
		int width = mybit.getWidth();
		int height = mybit.getHeight();
		int[] pixels = new int[width*height];
		//mybit.getPixels(pixels,0,width,0,0,width, height);
		//byte[] picData = mybit.getData();

		if(width==-1 || height==-1){
			// the image has not loaded.
			mybit = (Bitmap)null;
		}
		  final int MAXROWS = width;
	      final int MAXCOLS = height;
	      int H = MAXROWS;
	      int W = MAXCOLS;
	      
	      /** 
           *  Create a grayscale array
           *  Gray  = Green * 0.59 + Blue * 0.30 + Red * 0.11;
           */
	      System.out.println("MAXROWS " + MAXROWS);
	      System.out.println("MAXCOLS " + MAXCOLS);
	      int count = 0;
		  double[] matrix = new double[MAXROWS*MAXCOLS];
		  int mi = 0; // matrix index
		  double mygreen =0;
		  double myred = 0;
		  double myblue = 0;
		  double grayPixel;
			for (int row=0; row<MAXROWS; row++) {
	            for (int col=0; col<MAXCOLS; col++) {
	              
	            	mygreen = (mybit.getPixel(row,col) >> 16) & 0xFF; 
	            	myblue = (mybit.getPixel(row,col) >> 8) & 0xFF; 
	            	myred = mybit.getPixel(row,col) & 0xFF; 
	            	                              
	                grayPixel = (mygreen*0.59)+(myblue*0.30)+(myred*0.11);
	                matrix[mi] = grayPixel;
	                mi++;
	            }  // for col
	        }  // for row
			
			/**
			 * Normalize image
			 */
			
			double nmax = 0;
			double nmin = 0;
			for (int n1=0; n1<matrix.length; n1++) {
				nmax = Math.max(matrix[n1], nmax);
				nmin = Math.min(matrix[n1], nmin);
			}
			for (int n=1; n<matrix.length; n++) {
				matrix[n] = matrix[n]-nmin;
				matrix[n] = matrix[n]*255/(nmax -nmin);
			}
			System.out.println("Min and Max: "+nmin+" "+nmax);
			
			/** 
			 * Threshold image
			 */
			final int THRESH = 178;
			
			int[] binaryM = new int[W*H]; // binary matrix for edge detection
			
			for (int l=0; l<matrix.length; l++) {
				if (matrix[l] > THRESH) {
					binaryM[l] = 1;
				} else {
					binaryM[l] = 0;
				}
			}

			/**
			 * 
			 * More image processing:
			 * 1) Run through binary columns and count zeros
			 * 2) Make sure zero-count is less than and more than some constant (optional)
			 * 3) Find average count
			 * 4) Count the number of 0counts -- stitchCnt
			 * 5) Find average of stitchCnt
			 * 6) This is the row
			 */
			int[] zeroCnt = new int[W];
			int[] startBool = new int[W];
			int total = 0;
			int avg = 0;
			int med = 0;
			int max= 0;
			int min = 0;
			int cnt = 0;

			int mode=-1;
			int modefreq=-1;
			
			for (int k=0; k<binaryM.length; k++) {
				cnt = Math.abs(k%W);
				if (binaryM[k] == 0) {
					if (startBool[cnt] != 1) {
						startBool[cnt]=1;
						zeroCnt[cnt] = zeroCnt[cnt] +1;
					}
				} else {
					startBool[cnt]=0;
				}
			}
			
			for (int l=1; l<zeroCnt.length; l++) {
				max = Math.max(zeroCnt[l], max);
				min = Math.min(zeroCnt[l], min);
				total += zeroCnt[l];
			}
			med = (max+min/2);
			avg = total/W;
			

			for (int m=0;m<zeroCnt.length;m++){
				if (zeroCnt[m]>3) {
					int current=zeroCnt[m];
					int howm=0;
					for (int m2=0;m2<zeroCnt.length;m2++){
						if (zeroCnt[m2]==current) howm++;
					}
					int freq=howm;
					if (freq>modefreq){
						mode=current;
						modefreq=freq;
					}
				}
			}

		Float avgRow;
		if (med>1) {
			avgRow = new Float(mode);
		} else if (mode>1) {
			avgRow = new Float (med);
		} else {
			avgRow = new Float (avg);
		}
		
		int answer = 0;
		if ((mode <= 5)&&(mode != -1)) {
			answer = (int)(avg+mode)/2;
		} else if ((med < 15)&&(mode != -1)) {
			answer = (int)(mode+med)/2;
		} else if (((mode*2)<med)&&(mode != -1)) {
			answer = (int)(mode+med)/2;
		} else {
			answer = med;
		}
		
		// Account for row ratio
		answer = answer * Spyn.ROW_RATIO;
		
		int[] res = {answer,mode,med,avg};
		//return avgRow.intValue() + 1
		return res;
		// error: the for-loop was never executed
		// return -1;
	}
	//---------------------

	
}
