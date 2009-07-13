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
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Spyn extends ListActivity {
	
	// For recalling and scanning knit
	public static int TOTAL_ROWCOUNT=0; // total row count for all images of knit
	public static int LAST_ROWCOUNT=0; // row count for most recent image of knit
	public static int NUM_SCANS=0; // number of scans user wants to take of the knit
	public static int SCANS_LEFT = 0; // how many scans left
	
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
    
    //private static final int INSERT_ID = Menu.FIRST;
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
            .setNegativeButton("Stockinette Stitch", new
            		DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int whichButton) {
                	ROW_RATIO = 1;
                }
            })
            .setNeutralButton("Seed Stitch", new
            		DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int whichButton) {
                	ROW_RATIO = 2;
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
    	
    	// Check if scanning more than one image of knit
    	new AlertDialog.Builder(this)
    	.setTitle("How many scans?")
        .setPositiveButton("One", new
        		DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int whichButton) {
            	setNumberScans(1);
            	//Toast.makeText(Spyn.this, "Scanning one image of knit...", Toast.LENGTH_SHORT).show();	
   			 	photoHandler(activity);
            }
        })
        .setNegativeButton("More", new
        		DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int whichButton) {
            	multiPhotoHandler(activity);
            }
        })
        .show(); 
    	
    	
    	/*
        Button btn2 = new Button(this);
    	btn2.setText("One Image of Knit");
    	btn2.setId(btnId);
    	//Button btn1 = (Button) findViewById(R.id.SCAN_one_image);
    	btn2.setOnClickListener(new OnClickListener() {
    		 public void onClick(View v) {
    			 //dg.hide(); //visibility.gone(); 
    			 Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    			 startActivityForResult(intent,activity);
    		 }
    	});	
        dg.addContentView(btn2, params1);
		*/
    	
    	//Intent intent = new Intent("android.media.action.");
    	//intent.putExtra(MediaStore.MEDIA_SCANNER_VOLUME, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"scan_photo.jpg")));
		/*
		 * Daniela's Code to Lay Images on Top of Camera Preview
		 */
    	//setContentView(R.layout.photographme_main);
    	/*preview=(SurfaceView)findViewById(R.id.surface);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		*/
		//DrawOnTop mDraw = new DrawOnTop(this);
        //LayoutParams params = new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //addContentView(mDraw, params); 
    	
    	
    	// OLD CODE (Will): 
    	//Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    	//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FILE_PATH)));
    	
    	// Daniela: //
    	// Toast.makeText(this, "CALLING DrawOnTopMethod", Toast.LENGTH_LONG).show();
    	// LayoutParams params = new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	// DrawOnTop mDraw = new DrawOnTop(this);
		// addContentView(mDraw,params);
        
    }
    
    private void setNumberScans(int num) {
    	NUM_SCANS = num;
    	SCANS_LEFT = NUM_SCANS;
    }
    
    private void resetRowCount() {
    	NUM_SCANS = 0;
    	SCANS_LEFT = 0;
    	TOTAL_ROWCOUNT = 0;
    }
    private void multiPhotoHandler(final int activity) {
    	final Dialog dg = new Dialog(this);
    	final String[] btns = {"one image","two images","three images"};
    	final int TOADD = 1;
    	
    	dg.setTitle("Scan Preferences");
    	dg.setContentView(R.layout.preferences);
    	dg.show(); 

    	RadioGroup rg = new RadioGroup(this);
    	//rg.setGravity(Gravity.LEFT);
    	rg.setPadding(10,20,0,0);
    	rg.setMinimumWidth(300);
    	dg.addContentView(rg, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	
    	for (int b=0; b<btns.length; b++) {
    		final int num_imgs = b+TOADD;
        	RadioButton btn = new RadioButton(this);
        	btn.setText("Scan "+btns[b]+" of knit");
        	btn.setId(b+TOADD);
        	btn.setOnClickListener(new OnClickListener() {
        		 public void onClick(View v) {
        			 dg.hide();
        			 setNumberScans(num_imgs);
        			 //Toast.makeText(Spyn.this, "multiPhotoHandler: Scanning "+btns[num_imgs-TOADD]+" of knit...", Toast.LENGTH_LONG).show();	
        			 photoHandler(activity);
        		 }
        	});	
        	rg.addView(btn);
    	}
    }
    
    private void photoHandler(int activity) {
     	if (TOTAL_ROWCOUNT!=0) {
    		Toast.makeText(Spyn.this, "You scanned "+TOTAL_ROWCOUNT+" rows of your knit.", Toast.LENGTH_SHORT).show();	
    	}
    	
    	Toast.makeText(Spyn.this, "Tap \"Attach\" to save after you take the picture.", Toast.LENGTH_SHORT).show();	
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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
        menu.add(0, MENU_ADD, 0, "TEST");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==Spyn.ACTIVITY_RECALL_PHOTO&& resultCode==Activity.RESULT_OK)
        		|| (requestCode==Spyn.ACTIVITY_CREATE_PHOTO && resultCode==Activity.RESULT_OK)){
        	// Recall: photo => recall
        	// check which scan we're on so that we can take photos of entire knit
        	if (SCANS_LEFT == 3) { // for debugging
        		SCANS_LEFT = SCANS_LEFT - 1;
        		bitmap3 = (Bitmap) data.getExtras().get("data");
        		// SCAN THIS IMAGE
        		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap3)[0];
        		photoHandler(requestCode);
        	} else if (SCANS_LEFT == 2) {
        		SCANS_LEFT = SCANS_LEFT - 1;
        		bitmap2 = (Bitmap) data.getExtras().get("data");
        		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap2)[0];
        		photoHandler(requestCode);
        	} else if (SCANS_LEFT == 1) {
        		SCANS_LEFT = SCANS_LEFT - 1;
        		bitmap1 = (Bitmap) data.getExtras().get("data");
        		TOTAL_ROWCOUNT = TOTAL_ROWCOUNT + (int)ScanMe.processImg(bitmap1)[0];
        		// On last scan
        		//Toast.makeText(this, "This was the final scan. \nSpyn counted "+TOTAL_ROWCOUNT+" rows.", Toast.LENGTH_SHORT).show();
        		if (requestCode==Spyn.ACTIVITY_RECALL_PHOTO) {
        			callRecallMeFromRowCount();
        		} else {
        			callScanMeFromRowCount();
        		}
        	}
        } else if (requestCode==Spyn.ACTIVITY_CREATE_SCAN && resultCode==Activity.RESULT_OK) {
        	//int rowcount = (int) data.getIntExtra(ScanMe.INTENT_AVGROW, -1);
        	//Toast.makeText(this, "SCAN returned\n With Row:" + rowcount, Toast.LENGTH_LONG).show();
        	//callCreateMemory(rowcount);
        	callCreateMemory(TOTAL_ROWCOUNT);
    	} else if (requestCode==Spyn.ACTIVITY_CREATE_CREATE && resultCode==Activity.RESULT_OK) {

    		//Toast.makeText(this, "CREATE CREATE: reset row count", Toast.LENGTH_LONG).show();
    		//resetRowCount();
    	} else {
    		///Toast.makeText(this, "Back...", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "onActvityResult FAIL", Toast.LENGTH_LONG).show();
        }
    }
    
    public void storeBitmap(Bitmap bmp) {

		//callRecallMe(bmp);
    }

    public void combineBitmaps(Bitmap bmp1, Bitmap bmp2) {
    	Bitmap cmb = bmp1.createBitmap(bmp1.getWidth(), bmp1.getHeight()+bmp2.getHeight(), Bitmap.Config.RGB_565); //.copy(Config.RGB_565, true);

    	// use createBitmap for cropping
    			
    }
}


