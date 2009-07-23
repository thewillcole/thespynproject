/*
 * "Borrowed" from Google's Example Code
 * 
 * Will Cole for Spyn for Daniela Rosner
 * 
 */

package com.spyn;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.spyn.Knit.Knits;
import com.spyn.Knit.Memories;
import com.spyn.R;

import android.hardware.Camera;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Spyn extends ListActivity {
	
	private static final String TAG = "MemoriesList";

    // Menu item ids
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;

    /**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
            Memories._ID, // 0
            Memories.TITLE, // 1
            Knits._ID, // 0
            Knits.TITLE, // 1
    };

    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;

	
	// For recalling and scanning knit
	public static int TOTAL_ROWCOUNT=0; // total row count for all images of knit
	public static int TOTAL_STITCHCOUNT=0; // total stitch count for all images of knit
	public static int LAST_ROWCOUNT=0; // row count for most recent image of knit
	public static int NUM_SCANS=0; // number of scans user wants to take of the knit
	public static int SCANS_LEFT = 0; // how many scans left
	public static int REQUEST_CODE = 0;
	
	// Variables for determining the row ratio with a dialog on launch
	private static boolean LAUNCH = false; // has a project been launched, only change once
	
	// PROJECT SETTINGS
	private static String PROJ_BACK = "";
	private TextView mTitleText;
	private static String PROJ_TITLE = "";
	public static String PROJ_FOLDER = "/spyn/"+Spyn.PROJ_ID+"/";
	public static long PROJ_ID = -1;
	
	public static ArrayList<int[]> PROJ_PINS = new ArrayList();
	// Stitch values
	public static String[] STITCH_ARRAY = {"Mixed","Seed", "Stockinette", "Knit", "Purl"}; 
	public static int PROJ_STITCH = 1; 

	// Date format
	public static final int DATE_VERBAL=0;
	public static final int DATE_NUMERICAL=1;
	public static final int DATE_FILEPATH=2;
	
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
    private static final int MENU_BACKUP= -4;
    private static final int MENU_PREF= -5;
    private static final int MENU_START= -6;
    private static final int MENU_HELP= -7;
    private static final int MENU_NEW= -8;
    private static final int MENU_DELETE= -9;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    //log file
    private static final String LOG_FILENAME = "log.txt";
    private static final File LOG_FILE = new File(Environment.getExternalStorageDirectory(),LOG_FILENAME); 
    
    private NotesDbAdapter mDbHelper;
    private Dialog mPrefDialog;
    private boolean prefDialogOpen = false;
    private Dialog mOpenDialog;
    private boolean openDialogOpen = false;
    
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
        registerForContextMenu(getListView());
        
    	/**
    	 * Database
    	 */
    	mDbHelper = new NotesDbAdapter(this);
    	mDbHelper.open();
    
    	/**
    	 * Set up Dialogs
    	 */
    	mPrefDialog = new Dialog(Spyn.this);
    	mPrefDialog.setTitle("Project Preferences");
    	mPrefDialog.setContentView(R.layout.preferences);
    	final Spinner s2 = (Spinner) mPrefDialog.findViewById(R.id.spinner2);
    	ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.stitches,
    			android.R.layout.simple_spinner_item);
    	adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	s2.setAdapter(adapter2);
    	setUpPreferences();
    
    	mOpenDialog = new Dialog(Spyn.this);
    	mOpenDialog.setTitle("Choose your Spyn project");
    	mOpenDialog.setContentView(R.layout.start_up);
    	// Create star-up dialog Spinner
    	Cursor c = mDbHelper.fetchAllProjects();
    	startManagingCursor(c);
    	c.moveToFirst(); 
    	
        try {
        	if ((c.getCount()> 0)||(c!=null)) {
        		final int[] id_array = new int[c.getCount()+1]; 
            	final ArrayList<String> proj_array = new ArrayList<String>(); 
            	id_array[0] = 0;

            	if (LAUNCH == true) {
            		fillData();
            	}else {
            		Toast.makeText(Spyn.this,"Launching Spyn... ", Toast.LENGTH_LONG).show();	
            		LAUNCH = true; // has a project been launched, only change once
             		
            		/**
            		 * Open Last Project
            		 */	
            		Spyn.PROJ_ID = setUpOpenDialog();
            		Toast.makeText(Spyn.this,"Opening Last Project (Project"+Spyn.PROJ_ID+" out of"+c.getCount()+")", Toast.LENGTH_LONG).show();	
            		updateProjectSettings();
            	}
        	} else {
        		Toast.makeText(Spyn.this,"Number of memories: "+c.getCount(), Toast.LENGTH_LONG).show();	
        		createNewProject();
        	}
        } catch (Exception e) {
        	// Deal with the creation of first project
        	Toast.makeText(Spyn.this, "Creating first Spyn project. \n ("+e.toString()+")", Toast.LENGTH_LONG).show();
		} 

    	c.close();
    	
        //buttons
        //------------------------------------------

			
        //CREATE NEW
        final Button button_create = (Button) findViewById(R.id.button_create);
        button_create.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		callPhotographMe(Spyn.ACTIVITY_CREATE_PHOTO);
        	}});
        
        //RECALL
        final Button button_scan = (Button) findViewById(R.id.button_scan);
        //this will later change.
        button_scan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		callPhotographMe(ACTIVITY_RECALL_PHOTO);
        	}});
       
         //LAST SCAN
        final Button button_lastscan = (Button) findViewById(R.id.button_lastscan);
        button_lastscan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (Spyn.PROJ_PINS.isEmpty()==true) {
        			// FIX LATER  - store last scan in database //
        			Toast.makeText(Spyn.this, "You must scan once before returning to last scans",
        					Toast.LENGTH_LONG).show();	
                } else {
                	createLastScan();
                }
        		
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

    
    private void backupData() {
    	Cursor notesCursor = mDbHelper.fetchAllNotesforBackup();
    	startManagingCursor(notesCursor);
        // String to save in file
        String file_content = "DATA: \n";
        if (notesCursor.getCount() > 0) {
        	Toast.makeText(Spyn.this, "Number of memories backing up: "+
        			notesCursor.getCount(), Toast.LENGTH_LONG).show();	
        	try {
        		notesCursor.moveToFirst();
        		while (!notesCursor.isAfterLast()) {
        			/**
        			 * Store data for each message
        			 */
        			String mTitle =notesCursor.getString(
        				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        			String mBody =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
        			String mRowiD =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
        			String mTime =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TIME));
        			String mPhoto =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_PHOTO));
        			String mVideo =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_VIDEO));
        			String mAudio =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_AUDIO));
        			String mKnit =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_KNIT));
        			String mLocation =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION));
        			String mLat =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LAT));
        			String mLon =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCATION_LON));
        			String mRowCount =notesCursor.getString(
            				notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWCOUNT));
        			
        			file_content = file_content+"\n knit="+mKnit+", _id="+mRowiD+", rowcount"+mRowCount+
        				", time="+mTime+", title="+mTitle+", body="+mBody+", knit="+mKnit+
        				", photo="+mPhoto+", video="+mVideo+", audio="+mAudio+", latitude="+mLat+
        				", longitude="+mLon+", location="+mLocation;
                	notesCursor.moveToNext();
            	}
        	} catch (Exception e) {
        		Toast.makeText(Spyn.this, "ERROR 03: "+
            			e.toString(), Toast.LENGTH_LONG).show();
        		Log.w("backup",e.toString());
			}
        }
        notesCursor.close();
        /**
    	 * Save file
    	 */
        Date myDate = new Date(System.currentTimeMillis());
		String time = myDate.toGMTString();
		// Create data

		String mTime = getDate(Spyn.DATE_FILEPATH);
		makeFile("/sdcard/spyn/data_"+mTime+".txt", file_content);
		
    }
    
    private void fillData() {
    	 updateProjectVariables();
    	try {
    		Cursor notesCursor = mDbHelper.fetchAllNotes();
    		startManagingCursor(notesCursor);
    		 
    		// Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_TIME};
            
            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[]{R.id.text1, R.id.date1};
            
            // Now create a simple cursor adapter and set it to display
            SimpleCursorAdapter notes = 
            	    new SimpleCursorAdapter(this, R.layout.notes_row_titleonly, notesCursor, from, to);
            setListAdapter(notes);
    	} catch (Exception e) {
    		Toast.makeText(Spyn.this, "Error (filldata):"+e.toString(), Toast.LENGTH_LONG).show();	
            
		}

    }
    
    private void updateProjectVariables() {
    	// TITLE
    	mTitleText = (TextView) findViewById(R.id.project_title);
 		if (Spyn.PROJ_ID==-1) {
 			Spyn.PROJ_TITLE = "My Spyn Project"; 
 			mTitleText.setText(Spyn.PROJ_TITLE); 
 	 	} else if (Spyn.PROJ_TITLE.length() < 1) {
 			Spyn.PROJ_TITLE = mTitleText.getText().toString();
 		} else {
 			mTitleText.setText(Spyn.PROJ_TITLE); 
 	 	}
 		// FOLDER
 		Spyn.PROJ_FOLDER = "/spyn/"+Spyn.PROJ_ID+"/";
 		
 		// SET BACKGROUND IMAGE
 		Bitmap bitmap;
        final LinearLayout ln = (LinearLayout) findViewById(R.id.spyn_main);
 		if (Spyn.PROJ_BACK.length()>1){
 	 		bitmap = BitmapFactory.decodeFile(Spyn.PROJ_BACK);
 		} else {
 			InputStream is_knit = this.getResources().openRawResource(R.drawable.background);
 			bitmap = BitmapFactory.decodeStream(is_knit);
 		}
		Drawable mDrawable = new BitmapDrawable(bitmap);
		ln.setBackgroundDrawable(mDrawable);
		
    }
    
    public void callPhotographMe(final int activity) {
    	String mAction = "";
    	if (activity == Spyn.ACTIVITY_CREATE_PHOTO) {
    		mAction = "To pin memory"; 
    	} else {
    		mAction = "To find memories";
    	}
    	resetRowCount();
    	Toast.makeText(Spyn.this, mAction+" align bottom of knit to \nbottom of camera display.", 
    			Toast.LENGTH_LONG).show();	
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
    	
     	//Toast.makeText(Spyn.this, "Tap \"Attach\" to save after you take the picture.", Toast.LENGTH_SHORT).show();	
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
		/**
		 * Save (tiled) bitmap
		 */
		saveBitmap(bitmap_to_show);
		
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
    
    private void saveBitmap(Bitmap bitmap_to_show) {
    	try {
    		/*
    		Cursor c = mDbHelper.fetchLastNoteRowID();c.moveToFirst(); 
        	int mrowID = c.getColumnIndexOrThrow(mDbHelper.KEY_ROWID) ; 
            int okay = c.getInt(mrowID);
        	Toast.makeText(this, "RECEIVED: "+okay+ " "+mrowID, Toast.LENGTH_LONG).show();
        	*/
    		String path = RecordMe.getTestMemoryScanPathFromId();
    		File recfile = new File(path);
    		if (recfile.exists()) { recfile.delete(); }
    		
    		
    		recfile.createNewFile();
    		Uri uri = Uri.fromFile(recfile);
    		OutputStream outstream;
			outstream = getContentResolver().openOutputStream(uri);
			bitmap_to_show.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			outstream.close();
		} catch (Exception e) {
			Toast.makeText(this, "Error creating photo (recallme): "+
					e.toString(), Toast.LENGTH_LONG).show();
    	}
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
    
    public void createLastScan() {
    	Intent i = new Intent(this, LastScan.class);
        startActivity(i);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        //menu.add(0, MENU_SCAN, 0, R.string.menu_insert_new);
        menu.add(0, MENU_PREF, 0, "Preferences")
        .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ADD, 0, "Add Un-pinned Memory")
        .setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_START, 0, "Switch Projects") //TEST
        .setIcon(R.drawable.spyn_menu_switch);
        menu.add(0, MENU_EDIT, 0, R.string.menu_insert_edit)
        .setIcon(android.R.drawable.ic_menu_edit);
        menu.add(0, MENU_HELP, 0, "Help")
        .setIcon(android.R.drawable.ic_menu_help);
        menu.add(0, MENU_BACKUP, 0, "Backup data")
        .setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, MENU_NEW, 0, "New Project")
        .setIcon(android.R.drawable.ic_input_add);
        menu.add(0, MENU_DELETE, 0, "Delete Project")
        .setIcon(android.R.drawable.ic_menu_delete);
        
        return true;
        // ic_menu_mylocation = map for "my location"
        // ic_menu_revert
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	Intent i = new Intent();
    	switch(item.getItemId()) {
    	case MENU_START:
    		setUpOpenDialog();
        	showOpenDialog();
        	return true;
        case MENU_ADD:
        	i.setClassName("com.spyn.db", "com.spyn.db.NoteEdit");
            i.setAction(NotesDbAdapter.ACTION_CREATE);
            startActivityForResult(i, ACTIVITY_CREATE);
        	return true;
        case MENU_PREF:
        	showPreferences();
        	return true;
        case MENU_SCAN:
        	//
        	callPhotographMe(Spyn.ACTIVITY_CREATE_PHOTO);
        	return true;
        case MENU_EDIT:
        	//
            i.setClassName("com.spyn.db", "com.spyn.db.Notepadv3");
            i.setAction(NotesDbAdapter.ACTION_EDIT);
            startActivityForResult(i, ACTIVITY_EDIT);
        	return true;
        case MENU_BACKUP:
        	backupData();
        	return true;
        case MENU_NEW:
        	createNewProject();
           	return true;
        case MENU_DELETE:
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
    	// canvas.drawText("Place bottom of knit here.", 20, 440, paint);

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

    		if (REQUEST_CODE != Spyn.ACTIVITY_CREATE_PHOTO) {
    			new AlertDialog.Builder(this)
        		.setTitle(R.string.recall_dialog_title)
        		.setMessage(R.string.recall_dialog_question)
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
    		} else {
    			new AlertDialog.Builder(this)
        		.setTitle(R.string.create_dialog_title)
        		.setMessage(R.string.create_dialog_question)
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
    		
    	}
        
    }
    
    public void end_scanning() {
    	if (REQUEST_CODE==Spyn.ACTIVITY_RECALL_PHOTO) {
    		Toast.makeText(this, "Spyn will recall memories \nfrom "+TOTAL_ROWCOUNT+" rows.", Toast.LENGTH_LONG).show();
    		//Toast.makeText(this, "Bitmaps: "+bitmap1.toString()+", "+bitmap2.toString(), Toast.LENGTH_SHORT).show();
    		callRecallMeFromRowCount();
		} else {
			Toast.makeText(this, "Touch the screen to pin your knit!", Toast.LENGTH_LONG).show();
			callPlaceMeFromRowCount();
		}
    }
    
    public void continue_scanning() {
    	multi_photo_handler(REQUEST_CODE);
    }
    
    // Make text file 
    public void makeFile(String strFullPathName,String str) { 
    	File fileOut = new File(strFullPathName); 
		fileOut.getParentFile().mkdirs(); 
        FileWriter fw = null; 
        BufferedWriter bw = null; 
        try { 
            fw = new FileWriter(fileOut); 
            bw = new BufferedWriter(fw); 
            bw.write(str); 
            bw.newLine(); 
        } 
        catch (IOException ioe) { 
            ioe.printStackTrace(); 
        } 
        finally { 
            if (bw != null) { 
                try { 
                    bw.close(); 
                    fw.close(); 
                } catch (IOException e) { 
                	Toast.makeText(this, "Error makeFile: "+e.toString(), Toast.LENGTH_LONG).show();	
            		e.printStackTrace(); 
                } 
            } 
        } 
    } 
    
    
    
    /**
     * UTILITIES
     */
    private void showPreferences(){
    	prefDialogOpen = true;
    	mPrefDialog.show();
    }
    
    private void hidePreferences(){
    	prefDialogOpen = false;
    	mPrefDialog.dismiss();
    }
    
    private void showOpenDialog(){
    	openDialogOpen = true;
    	mOpenDialog.show();
    }
    
    private void hideOpenDialog(){
    	openDialogOpen = false;
    	mOpenDialog.dismiss();
    }
    
    public static int getStitchFactor() {
    	if (Spyn.PROJ_STITCH < 3) {
			return 2;
		}
		return 1;
    }
    
    public void setStitch(String mStitch) {
    	Toast.makeText(Spyn.this, "Changing stitch to: "+mStitch, Toast.LENGTH_LONG).show();	
		if (mStitch.equals("Seed")) {
				
    	} else if (mStitch.equals("Stockinette")){
    		
    	} else if (mStitch.equals("Purl")){
    	
    	} else if (mStitch=="Knit") {
    		
    	}
    }
    
    public static String getDate(int mFormat) {
    	String time = "";
    	if ((mFormat == Spyn.DATE_NUMERICAL)||(mFormat == Spyn.DATE_FILEPATH)) {
    		final Calendar cal = Calendar.getInstance();
    		String yr = Integer.toString(cal.get(Calendar.YEAR));
    		String mon = Integer.toString(cal.get(Calendar.MONTH)+1);
    		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
    		String hr = Integer.toString(cal.get(Calendar.HOUR));
    		String min = Integer.toString(cal.get(Calendar.MINUTE));
    		String pm = Integer.toString(cal.get(Calendar.AM_PM));
    		time = mon+"/"+day+"/"+yr+" @ "+hr+":"+min;
    		if (mFormat == Spyn.DATE_FILEPATH) {
    			time = yr+"_"+mon+"_"+day+"_"+hr+"_"+min;
        	} else {
        		time = mon+"/"+day+"/"+yr+" @ "+hr+":"+min;
        	}
    	} else if (mFormat == Spyn.DATE_VERBAL) {
    		Date myDate = new Date(System.currentTimeMillis());
    		time = myDate.toGMTString();
    	}
    	return time;
    }
    private void updateProjectSettings() {
		// Update project settings
		Cursor c = mDbHelper.fetchProject(Spyn.PROJ_ID);
		startManagingCursor(c);
        c.moveToFirst(); 
        int mproject_title = c.getColumnIndexOrThrow(mDbHelper.PROJ_TITLE) ; 
        Spyn.PROJ_TITLE = c.getString(mproject_title);
        int mproject_back = c.getColumnIndexOrThrow(mDbHelper.PROJ_BACKGROUND) ; 
        Spyn.PROJ_BACK = c.getString(mproject_back);
        int mproject_stitch = c.getColumnIndexOrThrow(mDbHelper.PROJ_STITCH) ; 
        Spyn.PROJ_STITCH = c.getInt(mproject_stitch);
        c.close();
		//Toast.makeText(Spyn.this,"Opening project \""+Spyn.PROJ_TITLE+
		//		"\" \n(Spyn Project #"+Spyn.PROJ_ID+")", Toast.LENGTH_LONG).show();	

		fillData();
    }
    
    private int setUpOpenDialog() {
    	boolean allow_open = true;
    	int returnInt  = 0;
    	int pId1 = 0;
    	Cursor c = mDbHelper.fetchAllProjects();
    	startManagingCursor(c);
         c.moveToFirst(); 
        final int[] id_array = new int[c.getCount()]; 
        final ArrayList<String> proj_array = new ArrayList<String>(); 
        int project_title; 
        int project_id; 
        project_title = c.getColumnIndexOrThrow(mDbHelper.PROJ_TITLE) ; 
		project_id = c.getColumnIndexOrThrow(mDbHelper.PROJ_ROWID) ; 
		if ((c != null)||(c.getCount() < 1)) { 
            /* See if we got any results */ 
            if (c.isFirst()) { 
            	int i = 0; 
                //int i = 1; 
                do { 
                		String pName = c.getString(project_title); 
                    	proj_array.add(pName); 
                    	pId1 = c.getInt(project_id); 
                    	id_array[i] = pId1; 
                    	
                        i++; 
                } while(c.moveToNext()); 
            }
        } else {
        	allow_open = false;
        }
        // Store last project id
        returnInt = pId1;
        
        // Start-up Dialog for Choosing projects
      	final Spinner s1 = (Spinner) mOpenDialog.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter3 = new ArrayAdapter(this, 
        		android.R.layout.simple_spinner_item, proj_array); 
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter3);
        showOpenDialog();
        
        // Clicked NEW
        Button button_new = (Button) mOpenDialog.findViewById(R.id.new_button);
        button_new.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		createNewProject();
    		}
        });
        
        // Clicked OPEN
        final boolean allow = allow_open;
    	Button button_open = (Button) mOpenDialog.findViewById(R.id.open_button);
        button_open.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
        		if (allow==true) {
        			// TITLE
        			Spyn.PROJ_TITLE = proj_array.get(s1.getSelectedItemPosition()); 
                    // PROJ ID
        			Spyn.PROJ_ID = id_array[s1.getSelectedItemPosition()];
        			
        			updateProjectSettings();
        			hideOpenDialog();	
        		} else {
        			Toast.makeText(Spyn.this,"No Spyn projects to open.\nPlease create a new project.", 
        					Toast.LENGTH_LONG).show();	
        		}
        			
        	}
        
        });
        c.close();
        updateProjectVariables();
        return returnInt;
    }
    
    private void createNewProject() {
    	String mTime = getDate(Spyn.DATE_NUMERICAL);
		String mName = "My Spyn Knit";
		/**
		 * CREATE NEW PROJECT
		 */
		long id = mDbHelper.createProject(mName, mTime, " ", " ",1);
		if (id > 0) {
			Spyn.PROJ_ID = id;
			Spyn.PROJ_TITLE = mName;
			
			Toast.makeText(Spyn.this,"Creating new project "+Spyn.PROJ_TITLE, 
					Toast.LENGTH_LONG).show();	
			
		} else {
			Toast.makeText(Spyn.this,"You cannot make project with id: "+id,
					Toast.LENGTH_LONG).show();	
		}
		updateProjectSettings();
		hideOpenDialog();
    }
    
    private void setUpPreferences() {
    	// Set up next dialog box
    	Button okButton = (Button) mPrefDialog.findViewById(R.id.okButton);
    	Button cancelButton = (Button) mPrefDialog.findViewById(R.id.cancelButton);
    	final EditText nameEditText = (EditText) mPrefDialog.findViewById(R.id.nameEditText);
		nameEditText.setText(Spyn.PROJ_TITLE);
		final Spinner stitchText = (Spinner) mPrefDialog.findViewById(R.id.spinner2);
		ArrayAdapter<String> adapter = new ArrayAdapter(Spyn.this, 
				android.R.layout.simple_spinner_item, STITCH_ARRAY); 
		adapter.setDropDownViewResource 
				(android.R.layout.simple_spinner_dropdown_item); 
		stitchText.setAdapter(adapter);
		
		ImageButton photoCaptureButtonButton = (ImageButton) 
		mPrefDialog.findViewById(R.id.PROJ_photoCaptureButton);
		//take PHOTO
        photoCaptureButtonButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		callPhoto();
        	}});
        
        // CANCEL BUTTON
        cancelButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		hidePreferences();
        	}
        });
        
        // SUBMIT BUTTON
		okButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					String mName = nameEditText.getText().toString();
					switch (view.getId()) {
			    		case R.id.okButton:
			    			// Set stitch
			    			String stitchSelect = stitchText.getSelectedItem().toString();
			    			setStitch(stitchSelect);
			    			// Set image
			    			
			    			try {
			    				
			    				String mdate = Spyn.getDate(Spyn.DATE_VERBAL);
		    					Spyn.PROJ_STITCH = stitchText.getSelectedItemPosition();
		    					
		    					/**
		    					 *  UPDATE PROJECT
		    					 */
		    					boolean results = mDbHelper.updateProject(Spyn.PROJ_ID,
		    							mName,mdate,"This is my project", 
		    							Spyn.PROJ_BACK,Spyn.PROJ_STITCH);
		    					if (results==false) {
		    						// Problem inserting into table
		    						Toast.makeText(Spyn.this,
		        							"Problem: Could not insert "+mName+
		        							" at "+Spyn.PROJ_ID+" into table.", 
		        							Toast.LENGTH_LONG).show();	
		    						Spyn.PROJ_TITLE = mName;
		    						mTitleText.setText(mName);
		    					} else {
		    						Toast.makeText(Spyn.this,
		        							"Updated project name to "
		    								+mName, 
		        							Toast.LENGTH_LONG).show();	
		    						// UPDATE TITLE
		    						Spyn.PROJ_TITLE = mName;
		    						mTitleText.setText(mName);
		    					}
		    					updateProjectVariables();
		    					hidePreferences();
		    					break;
			    			} catch (Exception e) {
			    				Toast.makeText(Spyn.this,
	        							"Error 01: "+e.toString(), 
	        							Toast.LENGTH_LONG).show();	
	        				
							}
			    		case R.id.cancelButton:
			    			mPrefDialog.cancel();
			    			break;

					}
				}
			});
		
		}
    
    private void callPhoto() {
    	Toast.makeText(Spyn.this, "take photo; hit \"attach\"", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,Spyn.ACTIVITY_PHOTO);
    }
    
    //_______________________________________________________
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Toast.makeText(Spyn.this, "Saving", Toast.LENGTH_SHORT).show();
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
      savedInstanceState.putString("PROJ_TITLE", Spyn.PROJ_TITLE);
      savedInstanceState.putLong("PROJ_ID",Spyn.PROJ_ID);
      savedInstanceState.putInt("PROJ_STITCH", Spyn.PROJ_STITCH);
      savedInstanceState.putString("PROJ_FOLDER", Spyn.PROJ_FOLDER);
      savedInstanceState.putString("PROJ_BACK", Spyn.PROJ_BACK);
      savedInstanceState.putBoolean("PREF_DIALOG", prefDialogOpen);
      savedInstanceState.putBoolean("OPEN_DIALOG", openDialogOpen);
      super.onSaveInstanceState(savedInstanceState);

    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      // Toast.makeText(Spyn.this, "Restoring", Toast.LENGTH_SHORT).show();
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      super.onRestoreInstanceState(savedInstanceState);
      Spyn.PROJ_TITLE = savedInstanceState.getString("PROJ_TITLE");
      Spyn.PROJ_ID = savedInstanceState.getLong("PROJ_ID");
      Spyn.PROJ_FOLDER = savedInstanceState.getString("PROJ_FOLDER");
      Spyn.PROJ_STITCH = savedInstanceState.getInt("PROJ_STITCH");
      Spyn.PROJ_BACK = savedInstanceState.getString("PROJ_BACK");
      prefDialogOpen = savedInstanceState.getBoolean("PREF_DIALOG");
      openDialogOpen = savedInstanceState.getBoolean("OPEN_DIALOG");
      if (prefDialogOpen) {
    	  showPreferences();
      } else if (openDialogOpen) {
    	  showOpenDialog();
      }
    }
    
    
    //-------------------------------------------------------
    
    
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
        	
        } else if (requestCode == Spyn.ACTIVITY_PHOTO) {
    		Bitmap x = (Bitmap) data.getExtras().get("data");
			String path = RecordMe.getProjPhotoPathFromId();
			Spyn.PROJ_BACK = path;
			File recfile = new File(path);
			if (recfile.exists()) { recfile.delete(); }
			try {
	    		recfile.createNewFile();
	    		Uri uri = Uri.fromFile(recfile);
	    		OutputStream outstream;
				outstream = getContentResolver().openOutputStream(uri);
				x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
				outstream.close();
			} catch (Exception e) {
    			Toast.makeText(this, "Error 01: "+
    					e.toString(), Toast.LENGTH_LONG).show();
        	}
        	try {
    			
    			ImageButton photoImageButton = (ImageButton) 
    				mPrefDialog.findViewById(R.id.PROJ_photoPreviewButton);
    			photoImageButton.setImageBitmap(x);
    			photoImageButton.setVisibility(View.VISIBLE);
    			//mPrefDialog.show();
    		} catch (Exception e) {
    			Toast.makeText(this, "Error 02: "+
    					e.toString(), Toast.LENGTH_LONG).show();
        	}
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
    