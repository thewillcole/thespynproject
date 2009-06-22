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
		    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	 }
	public static String getAudioPathFromId(long rowID, String audioID){
		return sanitizePath("SPYN_rowID_" + rowID + "_audio_" + audioID + ".3gp");
	}
	
	public static String getPhotoPathFromId(long rowID, String photoID) {
		return sanitizePath("SPYN_rowID_" + rowID + "_photo_" + photoID + ".png");
	}
	
	public static String getVideoPathFromId(long rowID, String videoID) {
		return sanitizePath("SPYN_rowID_" + rowID + "_audio_" + videoID + ".3gp");
	}
	
	public static int getIdFromPath(String path) {
		path = path.substring(path.lastIndexOf("_") + 1); //remove all before Id
		path = path.substring(0, path.lastIndexOf(".") - 1); //remove extension
		return Integer.parseInt(path);
	}
	
	public static int startRecord(long rowID, String audioID) {
		//-----------------------------------------------------------------
		//http://www.benmccann.com/dev-blog/android-audio-recording-tutorial/
		//-----------------------------------------------------------------
		int audioIdInt = Integer.parseInt(audioID);
		if (!isRecording) {			
			audioIdInt++;
			MediaRecorder recorder = getMediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(getAudioPathFromId(rowID, Integer.toString(audioIdInt)));
			recorder.prepare();
			recorder.start();
			isRecording = true;
			return audioIdInt;
		} else {
			stopRecord();
			return audioIdInt;
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
		MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(new File(path)));
		mp.start();
	}
}