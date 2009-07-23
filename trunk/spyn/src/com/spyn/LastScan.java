package com.spyn;

import com.spyn.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

public class LastScan extends Activity {

	public String myAction;
	private LinearLayout layout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent intent = getIntent();
		
		//Toast.makeText(NoteEdit.this, "SHOW", Toast.LENGTH_SHORT).show();
		String path = RecordMe.getLastScanPathFromId();
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if (bitmap == null) {
			Toast.makeText(LastScan.this, "GET BITMAP EXCEPTION:\nNull bitmap", Toast.LENGTH_SHORT).show();
		}
		
		layout = new LinearLayout(this);
        layout.setLayoutParams( new
                        ViewGroup.LayoutParams( LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT ) ); 
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        layout.setBackgroundDrawable(drawable);	
        this.setContentView(layout); 
		
		//ImageView iv = new ImageView(LastScan.this); 
		//iv.setImageBitmap(bitmap);
        //LastScan.this.setContentView(iv);
    	
		try {
			// Place pins
			for (int n=0; n<Spyn.PROJ_PINS.size(); n++) {
				boolean okay = addButton(n);
				if (okay == false) {
					Toast.makeText(LastScan.this, "Could not place pin #"+n,
        					Toast.LENGTH_LONG).show();	
				}
			}
		} catch (Exception e) {
				Toast.makeText(LastScan.this, "GET BITMAP EXCEPTION:\n" + e, Toast.LENGTH_LONG).show();
		}
		
	}
	

    
    public boolean addButton(int pinIndex) {
    	int[] pin = Spyn.PROJ_PINS.get(pinIndex);
    	if (pin.length != 4) {
    		return false;
    	}
    	final int xPos = pin[0];
    	final int yPos = pin[1];
    	final int rowCount = pin[2];
    	final int rowID = pin[3];
    	
    	Toast.makeText(LastScan.this, "PIN "+rowID+": " + xPos+", "+yPos, Toast.LENGTH_LONG).show();
		
    	final ImageView imgb = new ImageView(this);
        imgb.setImageResource(R.drawable.pin_v1);
        imgb.setId(rowID);
        imgb.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	callNoteEdit(rowID);
            	Toast.makeText(LastScan.this, "Message attached to row " + rowCount+".", Toast.LENGTH_SHORT).show();
            }
        });    
        
        try{
        	LinearLayout layout_tmp = new LinearLayout(LastScan.this);
        	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	params1.width = 75; params1.height = 75;
        	params1.leftMargin = xPos; params1.topMargin = yPos;
        	
        	layout_tmp.addView(imgb, params1);
        	LastScan.this.addContentView(layout_tmp, new
                        ViewGroup.LayoutParams( LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT ) );
        } catch (Exception e) {
        	Toast.makeText(LastScan.this, "Recall error 01: " + e.toString(), Toast.LENGTH_LONG).show();
        	
		}
        return true;
    }

    public void callNoteEdit(int rowID) {
    	Intent i = new Intent(LastScan.this, NoteEdit.class); // NoteView
        i.putExtra(NotesDbAdapter.KEY_ROWID, (long)rowID);
        i.setAction(NotesDbAdapter.ACTION_VIEW);
        startActivity(i);
        
    }

}
