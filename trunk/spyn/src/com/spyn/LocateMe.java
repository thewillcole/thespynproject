package com.spyn;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class LocateMe extends Activity implements LocationListener {
	/** android.location Variables **/
	LocationManager locationManager; // location manager interfaces with hardware
	Location myLocation; // contains coordinates and time. RETURNABLE object.
	String returnName;
	double returnLat;
	double returnLon;
	
	
	@Override /** Called when activity is not visible **/
	public void onStop() {
		super.onStop();
		locationManager.removeUpdates(this); // detach location updates

		// Nullify instance vars to force the re-started app to re-create them
		locationManager = null; 
	}
	
	public void prepareReturnResult() {
		Intent i = new Intent();
		if (returnName != null) {
			//Toast.makeText(LocateMe.this, "LOCATEME: stored real values for return", Toast.LENGTH_SHORT).show();
			i.putExtra(NotesDbAdapter.KEY_LOCATION, returnName);
			i.putExtra(NotesDbAdapter.KEY_LOCATION_LAT, returnLat);
			i.putExtra(NotesDbAdapter.KEY_LOCATION_LON, returnLon);
		} else {
			//Toast.makeText(LocateMe.this, "LOCATEME: stored fake values for return", Toast.LENGTH_SHORT).show();
			i.putExtra(NotesDbAdapter.KEY_LOCATION, "");
			i.putExtra(NotesDbAdapter.KEY_LOCATION_LAT, 0);
			i.putExtra(NotesDbAdapter.KEY_LOCATION_LON, 0);
		}
		setResult(RESULT_OK, i); //change to be conditioned upon finding a location
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			prepareReturnResult();
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}
	
   
    @Override  /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //------------------------//
        /////// LOCATION ///////////
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // instantiation
        boolean gpsStatus = locationManager.isProviderEnabled("gps");
        boolean networkStatus = locationManager.isProviderEnabled("network");
        if (!gpsStatus && !networkStatus) {
        	Toast.makeText(LocateMe.this, "ERROR:\nBOTH PROVIDERS DISABLED."
        			+ "please enable GPS or WIFI's GPS", Toast.LENGTH_LONG).show();
        	startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));

        } else {
        	Toast.makeText(LocateMe.this, "GPS: " + gpsStatus + ", network: " + networkStatus 
        			+ "\nplease wait while you are located...", Toast.LENGTH_LONG).show();
        }
        locationManager.requestLocationUpdates("gps", 1000L, 0, this); // attach listener
        locationManager.requestLocationUpdates("network", 1000L, 0, this); // attach listener
    }
    
    
    
    // Any location change in location will call this method
    public void	onLocationChanged(Location location) {
    	
    	Toast.makeText(LocateMe.this, "Found You!"/*"Location Change: " + changeNum++*/, Toast.LENGTH_SHORT).show();
    	
    	locationManager.removeUpdates(this); // Because only one location is needed,
    										 //we detach the listeners once we get the first location.
    	
    	//------------------- RETURN RESULT TO CALLER -------------
    	Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
    	try {
    		//Toast.makeText(LocateMe.this, "LAT: " + location.getLatitude() + ", LON: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    		List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //10 results
    		Address address = addresses.get(0);
    		returnName = address.getLocality();
    		returnLat = location.getLatitude();
    		returnLon = location.getLongitude();
    	} catch (IllegalArgumentException e) {
    		Toast.makeText(LocateMe.this, "ERROR:\nLOCATEME:\nAddress exception: lat or lon", Toast.LENGTH_SHORT).show();
    	} catch (IOException eio) {
    		Toast.makeText(LocateMe.this, "ERROR:\nLOCATEME:\nIOException", Toast.LENGTH_SHORT).show();
    	}
    	onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN));
    }
    
    
    public void	onProviderDisabled(String provider) {
    	// overwritten to implement LocationListener interface.
    }
    public void onProviderEnabled(String provider) {
    	// overwritten to implement LocationListener interface.
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	// overwritten to implement LocationListener interface.
    }
    
   
}