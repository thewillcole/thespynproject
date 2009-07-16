/*
 * "Borrowed" from Google's Example Code
 * 
 * Will Cole for Spyn for Daniela Rosner
 * 
 */

package com.spyn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.hardware.Camera;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Spyn extends ListActivity {
	
	// For recalling and scanning knit
	public static int TOTAL_ROWCOUNT=0; // total row count for all images of knit
	public static int LAST_ROWCOUNT=0; // row count for most recent image of knit
	public static int NUM_SCANS=0; // number of scans user wants to take of the knit
	public static int SCANS_LEFT = 0; // how many scans left
	public static int REQUEST_CODE = 0;
	
	// Variables for determining the row ratio with a dialog on launch
	private static boolean LAUNCH = false;
	public static int ROW_RATIO = 0;
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    public static final int ACTIVITY_VIEW=2;
    public static final int ACTIVITY_PHOTO=3;
    public static final int ACTIVITY_VIDEO=4;
    public static final int ACTIVITY_LOCATEME=5;
    
    //create new memeory
    public static final int ACTIVITY_CREATE_PHOTO= 11;
    public static final int ACTIVITY_CREATE_SCAN= 12;
    public static final int ACTIVITY_CREATE_CREATE= 13;
    
    //scan recall
    public static final int ACTIVITY_RECALL_PHOTO= 21;
    public static final int ACTIVITY_START_SCAN = 40;
    
    private static final int MENU_ADD = -1;
    private static final int MENU_EDIT = -2;
    private static final int MENU_SCAN = -3;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    private NotesDbAdapter mDbHelper;
    
    // Daniela's code for camera app
	private Camera mCamera;
	private boolean mPreviewRunning = false;
    
    //Image capture variables
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	
	// Variables for scanning multiple imgaes of knit
	public boolean k_scanning= false; // set true if multiple photos need to be taken for knit scan
	private Bitmap bitmap1 = null;
	private Bitmap bitmap2 = null;
	private Bitmap bitmap3 = null;
	

	
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.spyn_main);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());

        // Dialog box for specifying type of stitch for this application
        //------------------------------------------
        if (LAUNCH==false) {
        	LAUNCH = true; // set launch true so that this only happens once
        	new AlertDialog.Builder(this)
        	.setTitle("Choose your stitch")
            .setPositiveButton("Knit or Purl stitch", new
            		DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int whichButton) {
                	ROW_RATIO = 2;
                }
            })
            /*
            .setNegativeButton("Stockinette Stitch", new
            		DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int whichButton) {
                	ROW_RATIO = 1;
                }
            })*/
            .setNeutralButton("Seed or Stockinette Stitch", new
            		DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int whichButton) {
                	ROW_RATIO = 1;
                }
            })
            .show(); 
        	
        }
        
        //buttons
        //------------------------------------------

        //PHOTOGRAPH
        final Button button_scan = (Button) findViewById(R.id.button_scan);
        //this will later change.
        button_scan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//Toast.makeText(Spyn.this, "Scan Recall", Toast.LENGTH_SHORT).show();
        		callPhotographMe(ACTIVITY_RECALL_PHOTO);
        	}});
        //MAP
        final Button button_map = (Button) findViewById(R.id.button_map);
        button_map.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Toast.makeText(Spyn.this, "Opening map...", Toast.LENGTH_SHORT).show();
        		fillMap();
        		}});
        //------------------------------------------
    }

    
    
    
    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
        	    new SimpleCursorAdapter(this, R.layout.notes_row_titleonly, notesCursor, from, to);
        setListAdapter(notes);
    }
    
    public void callPhotographMe(final int activity) {
    	resetRowCount();
    	Toast.makeText(Spyn.this, "Align bottom of knit to \nbottom of camera display.", Toast.LENGTH_LONG).show();	
        multi_photo_handler(activity);
    }
    
    public static void resetRowCount() {
    	NUM_SCANS = 0;
    	SCANS_LEFT = 0;
    	TOTAL_ROWCOUNT = 0;
    }
    
    
    
    private void multi_photo_handler(int activity) {
     	if (TOTAL_ROWCOUNT!=0) {
     		int scan_left = 3-NUM_SCANS;
    		//Toast.makeText(Spyn.this, "You scanned "+TOTAL_ROWCOUNT+" rows.\n You have "+scan_left+" scans left.", Toast.LENGTH_SHORT).show();	
    	} 
    	
    	Toast.makeText(Spyn.this, "Tap \"Attach\" to save after you take the picture.", Toast.LENGTH_SHORT).show();	
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    	//Intent intent = new Intent("INTENT_ACTION_STILL_IMAGE_CAMERA");
    	startActivityForResult(intent,activity);

    }
    
    
    
    public void callScanMeFromRowCount() {
		Intent i = new Intent(this, ScanMe.class);
    	i.putExtra(ScanMe.INTENT_BITMAP, bitmap1);
    	if (NUM_SCANS == 2) {
    		i.putExtra(ScanMe.INTENT_BITMAP_2, bitmap2);
    	} else if (NUM_SCANS == 3) {
    		i.putExtra(ScanMe.INTENT_BITMAP_3, bitmap3);
    	}
    	i.setAction(ScanMe.ACTION_STORE);
    	startActivityForResult(i, ACTIVITY_CREATE_SCAN);
    }
    
    public void callPlaceMeFromRowCount() {
    	Intent i = new Intent(this, ScanMe.class);
    	Bitmap bitmap_to_show = null;
		if (NUM_SCANS == 1) {
        	i.putExtra(ScanMe.INTENT_BITMAP, bitmap1);
        	bitmap_to_show = bitmap1;
    	} else  if (NUM_SCANS == 2) {
    		i.putExtra(ScanMe.INTENT_BITMAP_2, bitmap2);
    		bitmap_to_show = bitmap2;
    	} else if (NUM_SCANS == 3) {
        	i.putExtra(ScanMe.INTENT_BITMAP_3, bitmap3);
        	bitmap_to_show = bitmap3;
    	}
		
    	i.setAction(ScanMe.ACTION_STORE);
    	startActivityForResult(i, ACTIVITY_CREATE_SCAN);
    }
    
    public void callRecallMeFromRowCount() {
    	Intent i = new Intent(this, RecallMe.class);
    	i.putExtra(ScanMe.INTENT_BITMAP, bitmap1);
    	if (NUM_SCANS == 2) {
    		i.putExtra(ScanMe.INTENT_BITMAP_2, bitmap2);
    	} else if (NUM_SCANS == 3) {
    		i.putExtra(ScanMe.INTENT_BITMAP_2, bitmap2);
        	i.putExtra(ScanMe.INTENT_BITMAP_3, bitmap3);
    	}
    	i.setAction(ScanMe.ACTION_STORE);
    	startActivity(i);
    }
    
    
    public void callCreateMemory(int rowcount) {
    	Intent cmIntent = new Intent(this, NoteEdit.class);
    	cmIntent.setAction(NotesDbAdapter.ACTION_CREATE);
    	startActivityForResult(cmIntent, ACTIVITY_CREATE_CREATE);
    	//resetRowCount();
    	//Toast.makeText(Spyn.this, "reset row counts", Toast.LENGTH_SHORT).show();	
    	
    }
    
    public void fillMap() {
    	 Intent i = new Intent(this, LocateWe.class);
         startActivity(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, MENU_SCAN, 0, R.string.menu_insert_new);
        menu.add(0, MENU_EDIT, 0, R.string.menu_insert_edit);
        //menu.add(0, MENU_ADD, 0, "TEST");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	Intent i = new Intent();
    	switch(item.getItemId()) {
        case MENU_ADD:
        	//
            i.setClassName("com.spyn", "com.spyn.NoteEdit");
            i.setAction(NotesDbAdapter.ACTION_CREATE);
            startActivityForResult(i, ACTIVITY_CREATE);
        	return true;
        case MENU_SCAN:
        	//
        	callPhotographMe(Spyn.ACTIVITY_CREATE_PHOTO);
        	return true;
        case MENU_EDIT:
        	//
            i.setClassName("com.spyn", "com.spyn.Notepadv3");
            i.setAction(NotesDbAdapter.ACTION_EDIT);
            startActivityForResult(i, ACTIVITY_EDIT);
        	return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case DELETE_ID:
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.deleteNote(info.id);
	        fillData();
	        return true;
		}
		return super.onContextItemSelected(item);
	}
    
    private void viewNote(long id) {
    	Intent i = new Intent(this, NoteEdit.class); // NoteView
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        i.setAction(NotesDbAdapter.ACTION_VIEW);
        startActivityForResult(i, ACTIVITY_VIEW);
    }
    
    class DrawOnTop extends View {

    	public DrawOnTop(Context context) {
    	super(context);
    	}

    	@Override
    	protected void onDraw(Canvas canvas) {

    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL);
    	paint.setColor(Color.RED);
    	paint.setTextAlign(Align.LEFT);
    	paint.setTextSize(24);
    	canvas.drawText("Place bottom of knit here.", 20, 440, paint);

    	super.onDraw(canvas);
    	}

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        viewNote(id);
    }
    
    private void call_dialog_handler(int resultCode, Intent data) {
    	// Show image
    	NUM_SCANS++;
    	if(NUM_SCANS==3) {
    		bitmap3 = (Bitmap) data.getExtras().get("data");
    		//mDrawable = new BitmapDrawable(bitmap3);
    		//layout.setBackgroundDrawable(mDrawable);	
    		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap3)[0];
    		end_scanning();
    	} else {
    		if (NUM_SCANS==1) {
        		bitmap1 = (Bitmap) data.getExtras().get("data");
        		//mDrawable = new BitmapDrawable(bitmap1);
        		//layout.setBackgroundDrawable(mDrawable);	
        		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap1)[0];
        	} else if (NUM_SCANS==2) {
        		bitmap2 = (Bitmap) data.getExtras().get("data");
        		//mDrawable = new BitmapDrawable(bitmap2);
        		//layout.setBackgroundDrawable(mDrawable);	
        		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap2)[0];
        	}
    		new AlertDialog.Builder(this)
    		.setTitle("Another scan?")
    		.setPositiveButton("Yes", new
    				DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog,int whichButton) {
    				continue_scanning();
    			}
    		})
    		.setNegativeButton("No", new
    				DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog,int whichButton) {
    				end_scanning();
    			}
    		})
    		.show(); 
    	}
    	
        //this.setContentView(layout); 
        
    }
    
    public void end_scanning() {
    	if (REQUEST_CODE==Spyn.ACTIVITY_RECALL_PHOTO) {
    		Toast.makeText(this, "Spyn will recall messages \nfrom "+TOTAL_ROWCOUNT+" rows.", Toast.LENGTH_SHORT).show();
    		callRecallMeFromRowCount();
		} else {
			Toast.makeText(this, "Touch the screen to pin your knit!", Toast.LENGTH_SHORT).show();
			callPlaceMeFromRowCount();
			//callScanMeFromRowCount();
		}
    }
    
    public void continue_scanning() {
    	multi_photo_handler(REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==Spyn.ACTIVITY_RECALL_PHOTO&& resultCode==Activity.RESULT_OK)
        		|| (requestCode==Spyn.ACTIVITY_CREATE_PHOTO && resultCode==Activity.RESULT_OK)){
        	// Recall: photo => recall
        	// check which scan we're on so that we can take photos of entire knit
        	
        	// TEST CODE
        	REQUEST_CODE = requestCode;
        	call_dialog_handler(resultCode, data);
        	
        } else if (requestCode==Spyn.ACTIVITY_START_SCAN && resultCode==Activity.RESULT_OK) {
        	Toast.makeText(this, "GOT TO RETURN RESULTS", Toast.LENGTH_SHORT).show();
    		callScanMeFromRowCount();
    	} else if (requestCode==Spyn.ACTIVITY_CREATE_SCAN && resultCode==Activity.RESULT_OK) {
        	callCreateMemory(TOTAL_ROWCOUNT);
    	} else if (requestCode==Spyn.ACTIVITY_CREATE_CREATE && resultCode==Activity.RESULT_OK) {
    		//Toast.makeText(this, "CREATE CREATE: reset row count", Toast.LENGTH_LONG).show();
    	} else {
    		///Toast.makeText(this, "Back...", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "onActvityResult FAIL", Toast.LENGTH_LONG).show();
        }
    }
    
}