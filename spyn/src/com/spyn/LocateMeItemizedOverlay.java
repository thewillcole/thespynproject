package com.spyn;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class LocateMeItemizedOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	// ^ holds the overlay items for the map

	public LocateMeItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker)); 
		// ^ defines the bounds on the default marker so it can be drawn
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
	    populate();
	}
	
	//clears the list.
	public void clear() {
		mOverlays.clear();
		populate();
	}
	
	@Override
	protected boolean onTap(int i) {
		//Toast.makeText(LocateMeItemizedOverlay.class.getSuperclass(),"Message",Toast.LENGTH_SHORT).show();
		
		return(true);
	}

}
