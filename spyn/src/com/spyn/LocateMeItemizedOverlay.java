package com.spyn;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocateMeItemizedOverlay extends ItemizedOverlay {
	public static int PIN_NUM = 0;
	public MapActivity mContext;
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	// ^ holds the overlay items for the map

	public LocateMeItemizedOverlay(Drawable defaultMarker, MapActivity app) {
		super(boundCenterBottom(defaultMarker)); 
		// ^ defines the bounds on the default marker so it can be drawn
		mContext  = app;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	//adds overlay items to the arraylist above
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    //PIN_NUM = Integer.parseInt(overlay.getTitle());
	    populate();
	}
	
	//clears the list.
	public void clear() {
		mOverlays.clear();
		populate();
	}
	
	
	@Override
	protected boolean onTap(int i) {
		//Spyn.callToastforPoint(i);
		//PIN_NUM = Integer.parseInt(mOverlays.get(i).getTitle());
		String mTitle = mOverlays.get(i).getTitle();
		
		Toast.makeText(mContext, "Message: "+mTitle, Toast.LENGTH_SHORT).show();
		
		return(true);
	}
}
