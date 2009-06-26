/*
 * "Borrowed" from Google's Example Code
 * 
 * Will Cole for Spyn for Daniela Rosner
 * 
 */

package com.spyn;

import java.io.File;
import java.io.OutputStream;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Spyn extends ListActivity {
	public static int SPYN_ROWCOUNT;
	
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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spyn_main);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());

        //buttons
        //------------------------------------------
        //PHOTOGRAPH
        final Button button_scan = (Button) findViewById(R.id.button_scan);
        //this will later change.
        button_scan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Toast.makeText(Spyn.this, "Scan Recall", Toast.LENGTH_SHORT).show();
        		callPhotographMe(ACTIVITY_RECALL_PHOTO);
        	}});
        //MAP
        final Button button_map = (Button) findViewById(R.id.button_map);
        button_map.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Toast.makeText(Spyn.this, "MAP!", Toast.LENGTH_SHORT).show();
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
    
    public void callPhotographMe(int activity) {
    	Toast.makeText(Spyn.this, "Tap \"Attach\" to save after you take the picture.", Toast.LENGTH_LONG).show();
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,activity);
    }
    
    public void callScanMe(Bitmap bitmap) {
    	Intent i = new Intent(this, ScanMe.class);
    	i.putExtra(ScanMe.INTENT_BITMAP, bitmap);
    	i.setAction(ScanMe.ACTION_STORE);
    	startActivityForResult(i, ACTIVITY_CREATE_SCAN);
    }
    
    public void callRecallMe(Bitmap bitmap) {
    	Intent i = new Intent(this, RecallMe.class);
    	i.putExtra(ScanMe.INTENT_BITMAP, bitmap);
    	i.setAction(ScanMe.ACTION_STORE);
    	startActivity(i);
    }
    
    public void callCreateMemory(int rowcount) {
    	Intent cmIntent = new Intent(this, NoteEdit.class);
    	SPYN_ROWCOUNT = rowcount;
    	cmIntent.setAction(NotesDbAdapter.ACTION_CREATE);
        startActivityForResult(cmIntent, ACTIVITY_CREATE_CREATE);
    }
    
    public void fillMap() {
    	 Intent i = new Intent(this, LocateWe.class);
         startActivity(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, MENU_ADD, 0,"Add New");
        menu.add(0, MENU_SCAN, 0, "SCAN New");
        menu.add(0, MENU_EDIT, 0, "Edit Memories");
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
    
//    private void editNote(long id) {
//    	Intent i = new Intent(this, NoteEdit.class);
//        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
//        i.setAction(NotesDbAdapter.ACTION_EDIT);
//        startActivityForResult(i, ACTIVITY_EDIT);
//    }
    
//  private void createNote() {
//  Intent i = new Intent(this, NoteEdit.class);
//  startActivityForResult(i, ACTIVITY_CREATE);
//}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        viewNote(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Spyn.ACTIVITY_RECALL_PHOTO&& resultCode==Activity.RESULT_OK) {
        	//recall: photo => recall
        	Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        	callRecallMe(bitmap);
        	Toast.makeText(this, "PHOTO returned to SCAN", Toast.LENGTH_LONG).show();
        } else if (requestCode==Spyn.ACTIVITY_CREATE_PHOTO && resultCode==Activity.RESULT_OK) {
        	Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        	Toast.makeText(this, "PHOTO returned to CREATE", Toast.LENGTH_LONG).show();
        	callScanMe(bitmap);
        } else if (requestCode==Spyn.ACTIVITY_CREATE_SCAN && resultCode==Activity.RESULT_OK) {
        	int rowcount = (int) data.getIntExtra(ScanMe.INTENT_AVGROW, -1);
        	Toast.makeText(this, "SCAN returned\n With Row:" + rowcount, Toast.LENGTH_LONG).show();
        	callCreateMemory(rowcount);
    	} else if (requestCode==Spyn.ACTIVITY_CREATE_CREATE && resultCode==Activity.RESULT_OK) {
    		Toast.makeText(this, "CREATE returned", Toast.LENGTH_LONG).show();
    	} else {
        	//Toast.makeText(this, "onActvityResult FAIL", Toast.LENGTH_LONG).show();
        	//Toast.makeText(this, "onActvityResult FAIL", Toast.LENGTH_LONG).show();
        	//Toast.makeText(this, "onActvityResult FAIL", Toast.LENGTH_LONG).show();
        	//Toast.makeText(this, requestCode + " " + resultCode, Toast.LENGTH_LONG).show();
        }
        
        
//        if (requestCode== Spyn.ACTIVITY_PHOTO && resultCode == Activity.RESULT_OK){
//        	Toast.makeText(this, "SPYN: Camera returned something", Toast.LENGTH_SHORT).show();
////        	try {        		
////        		Bitmap x = (Bitmap) data.getExtras().get("data");
////        		ImageView imageView = new ImageView(Spyn.this);
////        		imageView.setImageBitmap(x);
////        		setContentView(imageView);
////        	} catch (Exception e) {
////        		Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
////        	}
//        	try {
//        		Bitmap x = (Bitmap) data.getExtras().get("data");
//        		String path = Environment.getExternalStorageDirectory() + "/" + "TESTTEST" + ".png";
//				File recfile = new File(path);
//				if (recfile.exists()) { recfile.delete(); }
//				recfile.createNewFile();
//				Uri uri = Uri.fromFile(recfile);
//				OutputStream outstream;
//				outstream = getContentResolver().openOutputStream(uri);
//		        x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
//		        outstream.close();
//        	} catch (Exception e) {
//        		Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
//        	}
//        	Toast.makeText(this, "SPYN: Photo Saved", Toast.LENGTH_SHORT).show();
//        } else {
//        	fillData();
//        }
    }
}
