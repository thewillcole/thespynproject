package com.spyn;

//Sample of an activity which does not properly handle lack of GPS

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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class LocateMeHelper extends Activity {

	//private TextView messageView;
	/** android.location Variables **/
	LocationManager manager; // location manager interfaces with hardware
	Location myLocation; // contains coordinates and time. RETURNABLE object.
	String returnName;
	double returnLat=0;
	double returnLon=0;
	
	  public class WhereamiLocationListener implements LocationListener {

	    public void onLocationChanged(Location location) {
	    	if (location != null) {
	  	      
	    		Geocoder geocoder = new Geocoder(LocateMeHelper.this, Locale.ENGLISH);
	    		try {
	    			//Toast.makeText(LocateMe.this, "LAT: " + location.getLatitude() + ", LON: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
	    			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //10 results
	    			Address address = addresses.get(0);
	    			returnName = address.getLocality();
	    			returnLat = location.getLatitude();
	    			returnLon = location.getLongitude();
	    		} catch (IllegalArgumentException e) {
	    			Toast.makeText(LocateMeHelper.this, "ERROR:\nLOCATEME:\nAddress exception: lat or lon", Toast.LENGTH_SHORT).show();
	    		} catch (IOException eio) {
	    			Toast.makeText(LocateMeHelper.this, "ERROR:\nLOCATEME:\nIOException", Toast.LENGTH_SHORT).show();
	    		}
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

	  @Override
	  public void onResume() {
		  super.onResume();
	    
	  }

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.main);
	    //setContentView(makeContentView());
	    WhereamiLocationListener listener = new WhereamiLocationListener();
	    //LocationManager manager = (LocationManager)
	    manager = (LocationManager)
	getSystemService(Context.LOCATION_SERVICE);
	    long updateTimeMsec = 1000L;
	    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	updateTimeMsec, 500.0f,
	        listener);
	    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
	updateTimeMsec, 500.0f,
	        listener);
	  }
	  /*
	  private View makeContentView() {
		LinearLayout panel = new LinearLayout(this);
	    panel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	        LayoutParams.WRAP_CONTENT));
	    panel.setOrientation(LinearLayout.VERTICAL);
	    //panel.addView(makeMessageView());
	    return panel;
	  }

	  private View makeMessageView() {
	    messageView = new TextView(this);
	    messageView.setText("Loading...");
	    return messageView;
	  }
*/
	  private void updateWithNewLocation(Location location) {
	    String latLongString;
	    if (location != null) {
	      double lat = location.getLatitude();
	      double lng = location.getLongitude();
	      latLongString = "Lat: " + lat + "\nLng: " + lng;
	    } else {
	      latLongString = "No Location Found";
	    }
	    //messageView.setText("Your Current Location is:\n" + latLongString);
	  }
	  
	  public void prepareReturnResult() {
			Intent i = new Intent();//getIntent();
			if (returnName != null) {
				//Toast.makeText(LocateMe.this, "LOCATEME: stored real values for return", Toast.LENGTH_SHORT).show();
				i.putExtra(NotesDbAdapter.KEY_LOCATION, returnName);
				i.putExtra(NotesDbAdapter.KEY_LOCATION_LAT, returnLat);
				i.putExtra(NotesDbAdapter.KEY_LOCATION_LON, returnLon);
				setResult(RESULT_OK, i);
			} else {
				//Toast.makeText(LocateMe.this, "LOCATEME: stored fake values for return", Toast.LENGTH_SHORT).show();
				i.putExtra(NotesDbAdapter.KEY_LOCATION, (String)"");
				i.putExtra(NotesDbAdapter.KEY_LOCATION_LAT, (double)0.0);
				i.putExtra(NotesDbAdapter.KEY_LOCATION_LON, (double)0.0);
				setResult(RESULT_CANCELED, i);
			}
			//change to be conditioned upon finding a location
		}

	}
