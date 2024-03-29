package com.spyn;

import java.util.List;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.spyn.R;

public class LocateWe extends MapActivity {
	/** Zoom Widget Variables **/
	LinearLayout linearLayout; //handle for layout
	MapView mapView; // handle for Map View
	ZoomControls mZoom; // zoom control object // m stands for member or something
	
	/** Map Overlay Variables **/
	List<Overlay> mapOverlays; // list of things to overlay on map
	Drawable drawable; // this object holds an image (the marker)
	LocateMeItemizedOverlay itemizedOverlay; // 
	
	/** android.location Variables **/
	LocationManager locationManager; // location manager interfaces with hardware
	Location myLocation; // contains coordinates and time. RETURNABLE object.
	
	private NotesDbAdapter mDbHelper;

	
	/***********************************************/
	/*********** Map Activity Methods **************/
	/***********************************************/
	/** Called when activity is not visible **/
	@Override
	public void onStop() {
		super.onStop();
		itemizedOverlay.clear();
    	mapOverlays.clear(); // remove previous overlays

		// Nullify instance vars to force the re-started app to re-create them
		locationManager = null; itemizedOverlay = null; drawable = null; mapOverlays = null;
		mZoom = null; mapView = null; linearLayout = null;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.locateme_main);
        
        //----------------------//
        ///////// MAP (ZOOM) /////
        linearLayout = (LinearLayout) findViewById(R.id.zoomview);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.getController().setZoom(12);
        mZoom = (ZoomControls) mapView.getZoomControls(); //get zoom control from map view
        // ^ this will work out of the box because it is already hooked up to the MapView
        linearLayout.addView(mZoom); // plug ZoomControls into the LinearLayout
        mapOverlays = mapView.getOverlays(); // returns arraylist's contents
        drawable = this.getResources().getDrawable(R.drawable.pin_v1); // marker image (android bot)
        itemizedOverlay = new LocateMeItemizedOverlay(drawable, LocateWe.this);   
        
        placeMarkers();
        mDbHelper.close();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void placeMarkers() {
    	Cursor notes = mDbHelper.fetchAllNotes();
    	int[] rowIDs = new int[notes.getCount()];
    	GeoPoint[] geopoints = new GeoPoint[notes.getCount()];
    	OverlayItem myLocationOverlay;
    	notes.moveToNext();
    	
    	String[] mTitles = new String[notes.getCount()];
    	String[] mBodiess = new String[notes.getCount()];
    	String[] mRowIDs = new String[notes.getCount()];
    	
    	for (int i = 0; !notes.isAfterLast(); i++) {
    		try {	
    			int templat = (int) (1E6 * Double.parseDouble(
						notes.getString(
							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LAT))));
    			int templon = (int) (1E6 * Double.parseDouble(
					    notes.getString(
								notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LON))));
    			rowIDs[i] = Integer.parseInt(
    					notes.getString(
    							notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
        		geopoints[i] = new GeoPoint(templat,templon);
        		
        		
        		mTitles[i] = notes.getString(
						notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));

        		mBodiess[i] = notes.getString(
						notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
        		mRowIDs[i] = notes.getString(
						notes.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
        		
        		// Center map around last point
        		if (notes.isLast()) {
        			mapView.getController().setCenter(geopoints[i]);
        		}
    		} catch (NumberFormatException nfe) {
    			Toast.makeText(this, "LOCATEWE:\nERROR:\nNFE\n" + nfe, Toast.LENGTH_LONG).show();
    		} catch (IllegalArgumentException iae) {
    			Toast.makeText(this, "LOCATEWE:\nERROR:\nIAE\n" + iae, Toast.LENGTH_LONG).show();
    		} catch (CursorIndexOutOfBoundsException cioobe) {
    			Toast.makeText(this, "LOCATEWE:\nERROR:\nCIOOB\n" + cioobe, Toast.LENGTH_LONG).show();
    		} catch (Exception e) {
    			Toast.makeText(this, "LOCATEWE:\nERROR:\nE\n" + e, Toast.LENGTH_LONG).show();
    		}
    		
    		notes.moveToNext();
    		myLocationOverlay = new OverlayItem(geopoints[i], mTitles[i], mBodiess[i]);
    		itemizedOverlay.addOverlay(myLocationOverlay);
//    		public final boolean myLocationOverlay.onTouchEvent(MotionEvent e) {
//    			
//    		}
    	}
    	mapOverlays.add(itemizedOverlay);
    }
    
    /*
    public boolean onTouchEvent(android.view.MotionEvent e, MapView mapView) {
    	super.onTouchEvent(e);
    	Toast.makeText(this, "TOUCHED", Toast.LENGTH_LONG).show();
	  	return true;
	}*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	Toast.makeText(this, "TOUCHED", Toast.LENGTH_LONG).show();
		return super.onTouchEvent(event);
    }
   

}