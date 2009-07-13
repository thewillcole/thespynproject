/*
 * Will Cole for Spyn
 * From Google sample code
 */

package com.spyn;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NoteEdit extends Activity {

	private TextView mTitleText;
    private TextView mBodyText;
    private TextView mVideoText;
    private TextView mAudioText;
    private TextView mPhotoText;
    private TextView mKnitText;
	private TextView mLocationTitleText;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
	private TextView mLocationText;
    //private TextView mRowText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;
    private String mAction;
	private TextView mTimeText;
	
	public static double CURRENT_LAT=0;
	public static double CURRENT_LON=0;
	public static String CURRENT_TITLE="";
	public static String CURRENT_BODY="";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mAction = getIntent().getAction();
        if (mAction == null) {
        	Toast.makeText(NoteEdit.this, "ERROR: NO ACTION FOR NoteEdit", Toast.LENGTH_SHORT).show();
        } else if (mAction.equals(NotesDbAdapter.ACTION_CREATE)) {
        	setContentView(R.layout.note_edit);
        	
    		callLocateMe();
    		/*
        	// Added code to get location on create
        	//mLocationText.setText("Loading...");
    	    WhereamiLocationListener listener = new WhereamiLocationListener();
    	    LocationManager manager = (LocationManager)
    	    	getSystemService(NoteEdit.this.LOCATION_SERVICE);
    	    long updateTimeMsec = 1000L;
    	    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    	updateTimeMsec, 500.0f,
    	        listener);
    	    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    	updateTimeMsec, 500.0f,
    	        listener);
    	    */
        } else if (mAction.equals(NotesDbAdapter.ACTION_EDIT)) {
        	setContentView(R.layout.note_edit);
        } else if (mAction.equals(NotesDbAdapter.ACTION_VIEW)) {
        	setContentView(R.layout.note_view);
    	} else {
        	Toast.makeText(NoteEdit.this, "ERROR: BAD ACTION FOR NoteEdit", Toast.LENGTH_SHORT).show();
        }        
        //Toast.makeText(NoteEdit.this, "Action: " + mAction, Toast.LENGTH_SHORT).show();
       
        mTimeText = (TextView) findViewById(R.id.NOTE_time);
        mVideoText = (TextView) findViewById(R.id.NOTE_video);
        mAudioText = (TextView) findViewById(R.id.NOTE_audio);
        mPhotoText = (TextView) findViewById(R.id.NOTE_photo);
        mKnitText = (TextView) findViewById(R.id.NOTE_knit);
        mLatitudeText = (TextView) findViewById(R.id.NOTE_latitude);
        mLongitudeText = (TextView) findViewById(R.id.NOTE_longitude);
        mLocationTitleText = (TextView) findViewById(R.id.NOTE_location_title);
        mLocationText = (TextView) findViewById(R.id.NOTE_location);
        //mRowText = (TextView) findViewById(R.id.NOTE_row);
        if (!mAction.equals(NotesDbAdapter.ACTION_VIEW)) {
        	mTitleText = (EditText) findViewById(R.id.NOTE_title);
        	mBodyText = (EditText) findViewById(R.id.NOTE_body);
        } else {
        	mTitleText = (TextView) findViewById(R.id.NOTE_title);
        	mBodyText = (TextView) findViewById(R.id.NOTE_body);
        }
       

        mRowId = savedInstanceState != null ? savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID) 
        		: null;
        if (mRowId == null) {
        	Bundle extras = getIntent().getExtras(); 
        	mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }

        //-------------
        // BUTTONS
        // ------------
        Button confirmButton = (Button) findViewById(R.id.NOTE_confirm);
        //Button videoButton = (Button) findViewById(R.id.NOTE_record);
        Button audioCaptureButtonButton = (Button) findViewById(R.id.NOTE_audioCaptureButton);
        //Button stopButton = (Button) findViewById(R.id.NOTE_stopButton);
        Button audioPreviewButtonButton = (Button) findViewById(R.id.NOTE_audioPreviewButton);
        ImageButton locateMeButton = (ImageButton) findViewById(R.id.NOTE_locateMe);
        ImageButton photoCaptureButtonButton = (ImageButton) findViewById(R.id.NOTE_photoCaptureButton);
        Button videoCaptureButtonButton = (Button) findViewById(R.id.NOTE_videoCaptureButton);
        // locateMe
        locateMeButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
        		//Toast.makeText(NoteEdit.this, "LocateMe", Toast.LENGTH_SHORT).show();
        		callLocateMeOnMap();
        		saveState(); }});
        // save
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	    setResult(RESULT_OK);
        	    finish(); }});
        //record AUDIO
        audioCaptureButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
        		TextView tempAudioButtonText = (TextView) findViewById(R.id.NOTE_audioCaptureButton);
        		
        		if ((tempAudioButtonText.getText().toString().equals("record"))||(tempAudioButtonText.getText().toString().equals("re-record"))) {
        			tempAudioButtonText.setText("stop");        			
        			//String path = RecordMe.getAudioPathFromId(mRowId, mAudioText.getText().toString());
            		Toast.makeText(NoteEdit.this, "start"/*: " + path*/, Toast.LENGTH_SHORT).show();
            		String newAudioText = "" + RecordMe.startRecord(mRowId, mAudioText.getText().toString());
            		mAudioText.setText(newAudioText);
            		
        		} else {
        			
        			tempAudioButtonText.setText("re-record");
        			Toast.makeText(NoteEdit.this, "stop", Toast.LENGTH_SHORT).show();
            		RecordMe.stopRecord();
            		TextView tempPlayAudioButtonText = (TextView) findViewById(R.id.NOTE_audioPreviewButton);
            		if (tempPlayAudioButtonText.getVisibility() == View.INVISIBLE) {
            			tempPlayAudioButtonText.setVisibility(View.VISIBLE);
            		}
        		}
        		saveState(); }});
        //play AUDIO
        audioPreviewButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		String path = RecordMe.getAudioPathFromId(mRowId, mAudioText.getText().toString());
        		Toast.makeText(NoteEdit.this, "play"/*: " + path*/, Toast.LENGTH_SHORT).show();
        		RecordMe.playRecord(NoteEdit.this, path); }});
        
        //take PHOTO
        photoCaptureButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		
        		callPhoto();
        		
        	}});
        // preview PHOTO
        ImageButton photoPreviewButtonButton = (ImageButton) findViewById(R.id.NOTE_photoPreviewButton);
        photoPreviewButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		if (!mPhotoText.getText().toString().equals("0")) {
        			try {
        			//Toast.makeText(NoteEdit.this, "SHOW", Toast.LENGTH_SHORT).show();
        			String path = RecordMe.getPhotoPathFromId(mRowId,mPhotoText.getText().toString());
        			Bitmap bitmap = BitmapFactory.decodeFile(path);//BitmapFactory.decodeStream(openFileInput(path));//decodeFile(path);
        			if (bitmap == null) {
        				Toast.makeText(NoteEdit.this, "GET BITMAP EXCEPTION:\nNull bitmap", Toast.LENGTH_SHORT).show();
        			}
        			ImageView iv = new ImageView(NoteEdit.this); 
        			iv.setImageBitmap(bitmap);
        			//iv.setImageURI(Uri.(path));
        			setContentView(iv);
        			} catch (Exception e) {
        				Toast.makeText(NoteEdit.this, "GET BITMAP EXCEPTION:\n" + e, Toast.LENGTH_SHORT).show();
        			}
        		}
        		
        	}});
        // take VIDEO
        videoCaptureButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		callVideo();
        	}});
		
        
        // find LOCATION
        //if (mLocationText.getText().toString().equals("")) {
        	//callLocateMe();
        	/*saveState();
			Toast.makeText(NoteEdit.this, "LocateMe CALLED in OnCreate", Toast.LENGTH_SHORT).show();
			saveState();*/
        //}        
        //------------------
        // POPULATE AND SAVE
        //------------------
		populateFields();
		saveState();
    }
    //(end onCreate)
    
    private void callPhoto() {
    	Toast.makeText(NoteEdit.this, "take photo; hit \"attach\"", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,Spyn.ACTIVITY_PHOTO);
    }
    
    private void callVideo() {
    	Toast.makeText(NoteEdit.this, "take video; hit \"attach\"", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        startActivityForResult(intent,Spyn.ACTIVITY_VIDEO);
    }
    
    private void populateFields() {
    	
    	if (mRowId != null) {    		
    		Cursor note = mDbHelper.fetchNote(mRowId);
    		//Toast.makeText(NoteEdit.this, mRowId+" Populating fields: "+note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION), Toast.LENGTH_SHORT).show();
    		startManagingCursor(note);
    		mTitleText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
    		mTimeText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TIME)));
    		mBodyText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
    		mVideoText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_VIDEO)));
    		mAudioText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_AUDIO)));
    		mPhotoText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_PHOTO)));
    		mKnitText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_KNIT)));
    		mLocationText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION)));
    		mLatitudeText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LAT)));
    		mLongitudeText.setText(note.getString(
    				note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LON)));
    		//mRowText.setText(note.getString(
    		//		note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT)));
    		
    		//--------------------------
    		// Show playback buttons
    		//--------------------------
    		if (!mPhotoText.getText().toString().equals("0")) {
    			String path = RecordMe.getPhotoPathFromId(mRowId,mPhotoText.getText().toString());
    			Bitmap bitmap = BitmapFactory.decodeFile(path);//BitmapFactory.decodeStream(openFileInput(path));//decodeFile(path);
    			if (bitmap == null) {
    				Toast.makeText(NoteEdit.this, "GET BITMAP EXCEPTION:\nNull bitmap", Toast.LENGTH_SHORT).show();
    			}
    			ImageButton photoImageButton = (ImageButton) findViewById(R.id.NOTE_photoPreviewButton);
    			if (photoImageButton.getVisibility()!=View.VISIBLE) {
    				photoImageButton.setImageBitmap(bitmap);
    				photoImageButton.setVisibility(View.VISIBLE);
    			}
    		}
    		if (!mAudioText.getText().toString().equals("0")) { // show preview button: 
    			Button audioPreviewButtonButton = (Button) findViewById(R.id.NOTE_audioPreviewButton);
    			audioPreviewButtonButton.setVisibility(View.VISIBLE);
    		}
    		if (!mLocationText.getText().toString().equals("")) {
    			Button locationLocateMeButton = (Button) findViewById(R.id.NOTE_locateMe);
    			locationLocateMeButton.setVisibility(View.GONE);
    		}
    	
    		// Set the current location vars for map
    		CURRENT_LAT = note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LAT);
     		CURRENT_LON = note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LON);
     		CURRENT_TITLE = mTitleText.getText().toString();
     		CURRENT_BODY = mBodyText.getText().toString();
    		
    		//--------------------------
    	} else {
    		mTitleText.setText("[title]");
    		//mTimeText.setText("[body]"); //isn't editable for CREATE
    		mBodyText.setText("[body]");
    		//Toast.makeText(this, "FIRSTaction = " + mAction, Toast.LENGTH_SHORT).show();
    		mVideoText.setText("0");
    		//Toast.makeText(this, "SECONDaction = " + mAction, Toast.LENGTH_SHORT).show();
    		mAudioText.setText("0");
    		mPhotoText.setText("0");
    		mKnitText.setText("0");
    		mLatitudeText.setText("0.0");
    		mLongitudeText.setText("0.0");
    		//mRowText.setText("0");
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
    	String title = mTitleText.getText().toString();
    	String time;
    	String body = mBodyText.getText().toString();
    	int video = Integer.parseInt(mVideoText.getText().toString());
    	int audio = Integer.parseInt(mAudioText.getText().toString());
    	int photo = Integer.parseInt(mPhotoText.getText().toString());
    	int knit = Integer.parseInt(mKnitText.getText().toString());
    	String location = mLocationText.getText().toString();
    	double latitude= Double.parseDouble(mLatitudeText.getText().toString());
    	double longitude= Double.parseDouble(mLongitudeText.getText().toString());

    	if (mRowId == null) { // create 
    		Date myDate = new Date(System.currentTimeMillis());
    		time = myDate.toGMTString();
    		location = "";
    		int rowcount = Spyn.TOTAL_ROWCOUNT;
    		//Toast.makeText(this, "ROW COUNT" + rowcount, Toast.LENGTH_LONG).show();
    		long id = mDbHelper.createNote(title, time, body, video, audio,  
    				photo, knit, location, latitude, longitude, rowcount);
    		if (id > 0) {
    			mRowId = id;
    		}
    		//Toast.makeText(this, "SAVED WITH ROW: " + rowcount, Toast.LENGTH_LONG).show();
    	} else { // edit
    		time = mTimeText.getText().toString();
    		location = mLocationText.getText().toString();
    		mDbHelper.updateNote(mRowId, title, time, body, video, audio, 
    				photo, knit, location, latitude, longitude);

    	}

    }    
    
    //ACTIVITY RETURN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);
        if (resultCode == RESULT_OK) {
        	
        	if (requestCode == Spyn.ACTIVITY_VIDEO) {
        		// set play button visibility
        		Button video_VIS = (Button) findViewById(R.id.NOTE_videoPreviewButton);
        		if (video_VIS.getVisibility() != View.VISIBLE) { video_VIS.setVisibility(View.VISIBLE); }
        		try {
        			
        			File recfile = new File(RecordMe.getVideoPathFromId(mRowId, mVideoText.getText().toString()));
        			if (recfile.exists()) { recfile.delete(); }
        			recfile.createNewFile();
        			Uri uri = Uri.fromFile(recfile);
        			OutputStream outstream = getContentResolver().openOutputStream(uri);
        			outstream.write((byte[])returnIntent.getExtras().get("data"));
        			outstream.close();
        		} catch (Exception e) {
        			Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
        		}

        		
        	} else if (requestCode == Spyn.ACTIVITY_LOCATEME) {

            	if (returnIntent!=null) {
        			locateMeMethod(returnIntent);
        		} /*else {
        			Toast.makeText(this, "ERROR:\nNOTEEDIT: LOCATEME returned no intent", Toast.LENGTH_SHORT).show();
        		}*/

        	} else if (requestCode == Spyn.ACTIVITY_PHOTO) {
        		//Toast.makeText(this, "SPYN: Camera returned something", Toast.LENGTH_SHORT).show();
        		try {
        			Bitmap x = (Bitmap) returnIntent.getExtras().get("data");
        			//ImageView iv = new ImageView(this); iv.setImageBitmap(x);
        			//setContentView(iv);
        			
		        	mPhotoText.setText(
			        		"" + (1 + 
			        				Integer.parseInt(
			        						mPhotoText.getText().toString())));
		        	saveState();
   			
		        	String path = RecordMe.getPhotoPathFromId(mRowId, mPhotoText.getText().toString());
        			//Environment.getExternalStorageDirectory() + "/" + "TESTTEST" + ".png";
        			File recfile = new File(path);
        			if (recfile.exists()) { recfile.delete(); }
        			recfile.createNewFile();
        			Uri uri = Uri.fromFile(recfile);
        			OutputStream outstream;
        			outstream = getContentResolver().openOutputStream(uri);
        			x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
        			outstream.close();

        			ImageButton photoImageButton = (ImageButton) findViewById(R.id.NOTE_photoPreviewButton);
        			photoImageButton.setImageBitmap(x);
        			photoImageButton.setVisibility(View.VISIBLE);
        			
        			// Saved State, now reset row count
        			Spyn.TOTAL_ROWCOUNT = 0;

        		} catch (Exception e) {
        			Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
        		}

        		populateFields();
        		//Toast.makeText(this, "SPYN: Photo Saved", Toast.LENGTH_SHORT).show();

        	}	else {
        		//Toast.makeText(this, "ERROR:\nNOTEEDIT: unidentified resultCode: " + resultCode, Toast.LENGTH_SHORT).show();
        	}

        } else if (resultCode == RESULT_CANCELED){
        	//Toast.makeText(this, "ERROR:\nNOTEEDIT: resultCode \"cancelled\"", Toast.LENGTH_SHORT).show();
        }
    }

    public void callLocateMeOnMap() {
    	Intent i = new Intent(this, LocateMe.class);
    	startActivityForResult(i, Spyn.ACTIVITY_LOCATEME);
    }
    
    public void callLocateMe() {
    	// Added code to get location on create
    	//mLocationTitleText.setText("Loading...");
	    WhereamiLocationListener listener = new WhereamiLocationListener();
	    LocationManager manager = (LocationManager)
	    	getSystemService(NoteEdit.this.LOCATION_SERVICE);
	    long updateTimeMsec = 1000L;
	    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	updateTimeMsec, 500.0f,
	        listener);
	    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
	updateTimeMsec, 500.0f,
	        listener);
    	
    }
    
    public void locateMeMethod(Intent returnIntent) {
			//saveState();
			returnIntent.hasExtra(NotesDbAdapter.KEY_LOCATION_LAT);
			String myTempLoc = returnIntent.getStringExtra(NotesDbAdapter.KEY_LOCATION);
			double myTempLat = returnIntent.getDoubleExtra(NotesDbAdapter.KEY_LOCATION_LAT, 0.0);
			double myTempLon = returnIntent.getDoubleExtra(NotesDbAdapter.KEY_LOCATION_LON, 0.0);
			
			//mLocationText.setText(myTempLoc.toString());
			mLatitudeText.setText(Double.toString(myTempLat));
			mLongitudeText.setText(Double.toString(myTempLon));
			//mLocationTitleText.setText("lat/lon");
			
			Toast.makeText(this, "LOCATION " + myTempLoc + ", " + myTempLat + ", " + myTempLon, Toast.LENGTH_SHORT).show();	
			//saveState(); // THIS IS THE GOAL
    }
    

    public class WhereamiLocationListener implements LocationListener {

	    public void onLocationChanged(Location location) {
	      if (location != null) {
	        //messageView.setText("LOCATION: "+location.getLatitude()+" "
	        //		+location.getLongitude());
	    	//------------------- RETURN RESULT TO CALLER -------------
	      	
	    	try { 
	      		Geocoder geocoder = new Geocoder(NoteEdit.this, Locale.ENGLISH);
		      	List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2); //10 results
		      	//Toast.makeText(NoteEdit.this, "EMPTY? "+addresses.isEmpty(), Toast.LENGTH_SHORT).show();
		     	if (!(addresses.isEmpty())){
		     		Toast.makeText(NoteEdit.this, "FOUND LOCATION:  "+((Address) addresses).getLocality(), Toast.LENGTH_SHORT).show();
			     	Address address = addresses.get(1);
		     		mLocationText.setText(address.getLocality());
		     	}
		     	mLatitudeText.setText(Double.toString(location.getLatitude()));
	     		mLongitudeText.setText(Double.toString(location.getLongitude()));

	     		// Set the current location vars for map
	     		CURRENT_LAT = location.getLatitude();
	     		CURRENT_LON = location.getLongitude();
	     		CURRENT_TITLE = mTitleText.getText().toString();
	     		CURRENT_BODY = mBodyText.getText().toString();
	     	
	    	} catch (IllegalArgumentException e) {
	    		Toast.makeText(NoteEdit.this, "ERROR:\nLOCATEME:\nAddress exception: lat or lon", Toast.LENGTH_SHORT).show();
	     	} catch (IOException eio) {
	     		Toast.makeText(NoteEdit.this, "ERROR:\nLOCATEME:\nIOException", Toast.LENGTH_SHORT).show();
	     	}
	     	
	    	 //Geocoder geocoder = new Geocoder(NoteEdit.this); 
	     	 //mLocationText.setText(Double.toString(location.getLatitude())+", "+Double.toString(location.getLongitude()));
	     	 //mLat = location.getLatitude();
	     	 //mLon = location.getLongitude();
	     	 
	     	 
		
	     	 //setLocation(location.getLatitude(),location.getLongitude());
	    	  
	      }
	    }

	    public void onProviderDisabled(String provider) {
	      // TODO Auto-generated method stub

	    }

	    public void onProviderEnabled(String provider) {
	      // TODO Auto-generated method stub

	    }

	    public void onStatusChanged(String provider, int status, Bundle extras)
	{
	      // TODO Auto-generated method stub

	    }

	  }

   }


