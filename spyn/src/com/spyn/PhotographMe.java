/**
 * Copyright (c) 2007, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.spyn;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotographMe extends Activity implements SurfaceHolder.Callback
{
	private static final String TAG = "CameraApiTest";
	Camera mCamera;
	boolean mPreviewRunning = false;
	public String myPhotoName = "EdwardM";

	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		//Log.e(TAG, "onCreate");
		//getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.photographme_main);
		mSurfaceView = (SurfaceView)findViewById(R.id.surface);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuItem item = menu.add(0, 0, 0, "goto gallery");
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Uri target = Uri.parse("content://media/external/images/media");
				Intent intent = new Intent(Intent.ACTION_VIEW, target);
				startActivity(intent);
				return true;
			}
		});
		return true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			Bitmap bitmap;
			ImageView imageView;

			try {
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				imageView = new ImageView(PhotographMe.this);
				imageView.setImageBitmap(bitmap);
				setContentView(imageView);
				// saving the file
				String path = Environment.getExternalStorageDirectory() + "/" + myPhotoName + ".png";
				File recfile = new File(path);
				if (recfile.exists()) { recfile.delete(); }
				recfile.createNewFile();
				FileOutputStream fos = new FileOutputStream(recfile);
				fos.write(data);
				fos.close();
	
				Toast.makeText(PhotographMe.this, "Image File Saved!", Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Toast.makeText(PhotographMe.this, "PHOTOGRAPHME:\nEXCEPTION:\n" + e, Toast.LENGTH_SHORT);
			}
			
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}

		if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_CAMERA) {
			mCamera.takePicture(null, mPictureCallback, mPictureCallback);
			return true;
		}

		return false;
	}

	protected void onResume() {
		//Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		//Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();
		//mCamera.startPreview();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		//Log.e(TAG, "surfaceChanged");
		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
		mCamera.setPreviewDisplay(holder);
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder){
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
}

