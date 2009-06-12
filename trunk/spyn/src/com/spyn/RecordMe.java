package com.spyn;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;

public class RecordMe {
	
	//fake singleton
	private static MediaRecorder mediaRecorder = null;
	public static MediaRecorder getMediaRecorder() {
		//if (mediaRecorder == null) {
		if (isRecording) {
			return mediaRecorder;
		} else {
			mediaRecorder = new MediaRecorder();
			//return mediaRecorder;
		//} else {
			return mediaRecorder;
		//}
		}
	}
	
	protected static boolean isRecording = false;
	
	//PATH PARSING METHODS
	public static String sanitizePath(String path) {
		    if (!path.startsWith("/")) {
		      path = "/" + path;
		    }
		    if (!path.contains(".")) {
		      path += ".3gp";
		    }
		    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	 }
	public static String getVideoPathFromId(long rowID, String videoID){
		return "SPYN_rowID_" + rowID + "_audio_" + videoID;
	}
	public static int getVideoIdFromPath(String path) {
		return Integer.parseInt(path.substring(path.lastIndexOf("_") + 1));
	}
	
	public static String getPhotoPathFromId(long rowID, String photoID) {
		return sanitizePath("SPYN_rowID_" + rowID + "_photo_" + photoID + ".png");
	}
	
	public static int getPhotoIdFromPath(String path) {
		return Integer.parseInt(path.substring(path.lastIndexOf("_") + 1));
	}
	
	public static int startRecord(String path) {
		//-----------------------------------------------------------------
		//http://www.benmccann.com/dev-blog/android-audio-recording-tutorial/
		//-----------------------------------------------------------------
		int videoID = getVideoIdFromPath(path);
		String videoPath = path.substring(0, path.lastIndexOf("_") + 1);
		if (!isRecording) {			
			videoID++;
			videoPath += videoID;
			MediaRecorder recorder = getMediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(sanitizePath(videoPath));
			recorder.prepare();
			recorder.start();
			isRecording = true;
			return  videoID;
		} else {
			stopRecord();
			return videoID;
		}
		//------------------------------------------------------------------
		/*
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
			//throw new IOException("SD Card is not mounted.  It is " + state + ".");
			//Toast.makeText(RecordMe.this, "SD Card is not mounted.  It is " + state + ".", Toast.LENGTH_SHORT).show();
		}		
		if (!directory.exists() && !directory.mkdirs()) {
			//throw new IOException("Path to file could not be created.");
			//Toast.makeText(RecordMe.this, "Path to file could not be created.", Toast.LENGTH_SHORT).show();
		}
		 */

	}
	
	public static void stopRecord() {
		if (isRecording) {
			MediaRecorder recorder = getMediaRecorder();
			recorder.stop();
			isRecording = false;
		}
	}
	
	public static void playRecord(Context context, String path) {
		if (isRecording) {
			stopRecord();
		}
		MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(new File(sanitizePath(path))));
		mp.start();
	}
}