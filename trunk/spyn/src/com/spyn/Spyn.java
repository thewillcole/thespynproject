/*
 * "Borrowed" from Google's Example Code
 * 
 * Will Cole for Spyn for Daniela Rosner
 * 
 */

package com.spyn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Spyn extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_VIEW=2;
    public static final int ACTIVITY_PHOTO=3;
    
    private static final int INSERT_ID = Menu.FIRST;
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

        //------------------------------------------
        //buttons
        //------------------------------------------
        //VIEW
        final Button button_view = (Button) findViewById(R.id.button_view);
        button_view.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName("com.spyn", "com.spyn.Notepadv3");
                i.setAction(NotesDbAdapter.ACTION_VIEW);
                startActivityForResult(i, ACTIVITY_VIEW); }});
        //EDIT
        final Button button_edit = (Button) findViewById(R.id.button_edit);
        button_edit.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName("com.spyn", "com.spyn.Notepadv3");
                i.setAction(NotesDbAdapter.ACTION_EDIT);
                startActivityForResult(i, ACTIVITY_EDIT); }});
        //CREATE
        final Button button_create = (Button) findViewById(R.id.button_create);
        button_create.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName("com.spyn", "com.spyn.NoteEdit");
                i.setAction(NotesDbAdapter.ACTION_CREATE);
                //i.putExtra(NotesDbAdapter.KEY_ROWID, id);
                startActivityForResult(i, ACTIVITY_CREATE); }});
        //PHOTOGRAPH
        final Button button_scan = (Button) findViewById(R.id.button_scan);
        //this will later change.
        button_scan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		callPhotographMe();
        		
        	}});
        //MAP
        final Button button_map = (Button) findViewById(R.id.button_map);
        button_map.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Toast.makeText(Spyn.this, "MAP!", Toast.LENGTH_SHORT).show();
        		fillMap();
        		}});
        //------------------------------------------
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
    
    public void callPhotographMe() {
    	//Intent intent = new Intent("com.google.zxing.client.android.SCAN");
    	//startActivityForResult(intent, 0);
    	//Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    	//startActivityForResult(intent, 0);
    	
    	//Intent i = new Intent(this, PhotographMe.class);
    	//startActivity(i);
    	Toast.makeText(Spyn.this, "Tap \"Attach\" to save after you take the picture.", Toast.LENGTH_LONG).show();
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,Spyn.ACTIVITY_PHOTO);
    }
    
    public void fillMap() {
    	 Intent i = new Intent(this, LocateWe.class);
         startActivity(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createNote();
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
	
    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
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
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        viewNote(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode== Spyn.ACTIVITY_PHOTO && resultCode == Activity.RESULT_OK){
        	Toast.makeText(this, "SPYN: Camera returned something", Toast.LENGTH_SHORT).show();
//        	try {        		
//        		Bitmap x = (Bitmap) data.getExtras().get("data");
//        		ImageView imageView = new ImageView(Spyn.this);
//        		imageView.setImageBitmap(x);
//        		setContentView(imageView);
//        	} catch (Exception e) {
//        		Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
//        	}
        	try {
        		Bitmap x = (Bitmap) data.getExtras().get("data");
        		String path = Environment.getExternalStorageDirectory() + "/" + "TESTTEST" + ".png";
				File recfile = new File(path);
				if (recfile.exists()) { recfile.delete(); }
				recfile.createNewFile();
				Uri uri = Uri.fromFile(recfile);
				OutputStream outstream;
				outstream = getContentResolver().openOutputStream(uri);
		        x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
		        outstream.close();
        	} catch (Exception e) {
        		Toast.makeText(this, "SPYN:\nEXCEPTION:\n" + e, Toast.LENGTH_LONG * 10).show();
        	}
        	Toast.makeText(this, "SPYN: Photo Saved", Toast.LENGTH_SHORT).show();
        } else {
        	fillData();
        }
    }
}
