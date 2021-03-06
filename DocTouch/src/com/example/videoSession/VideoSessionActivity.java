package com.example.videoSession;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.doctouch.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoSessionActivity extends FragmentActivity implements OnClickListener{
	
	// Activity codes.
	static final int REQUEST_TAKE_VIDEO_SESSION = 1;
	static final int REQUEST_PICK_VIDEO = 2;
	
	// Media type codes
	static final int MEDIA_TYPE_IMAGE = 1;
	static final int MEDIA_TYPE_VIDEO = 2;
	
	// Video Viewer
	private VideoView mVideoView;
	
	// Environment and application variables
	static Context context;
	static final String appName = "DocTouch";
	
	// Internal storage directory.
	static File mediaStorageDir;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_session);
        
        // Initialize application context
        context = this;
        
        // Initialize internal storage directory
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        
        mVideoView = (VideoView) findViewById(R.id.video_view);
        
        Button takeVideo = (Button) findViewById(R.id.request_take_video_session);
		takeVideo.setOnClickListener(this);
		
		Button reviewVideosToSend = (Button) findViewById(R.id.request_pick_video);
		reviewVideosToSend.setOnClickListener(this);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_TAKE_VIDEO_SESSION:
			
			// If video capture was successful
			if(resultCode == RESULT_OK) {
				//Uri videoUri = data.getData();
				//mVideoView.setVideoURI(videoUri);
			}
			
			// Otherwise, just begin the recording session again.
			else if(resultCode == RESULT_CANCELED) {
				new AlertDialog.Builder(this)
				.setMessage(
						"Do you want to continue current recording session?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// Begin another video recording session.
								beginVideoRecordingSession();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								// Go back to this activity's main screen.
								Toast.makeText(VideoSessionActivity.this, "The recording session was cancelled.", Toast.LENGTH_SHORT).show();
							}
						}).show();
			}
		case REQUEST_PICK_VIDEO:
		}
	}
	
	@Override
	public void onClick(View v) {
		
		// Begin patient video recording session
		if(v.getId() == R.id.request_take_video_session) {
			beginVideoRecordingSession();
		}
		else if(v.getId() == R.id.request_pick_video) {
			beginVideoGallery();
		}
	}
	
	/**
	 * beginVideoRecordingSession:
	 * Access the camera to begin the video recording session.
	 */
	public void beginVideoRecordingSession() {
		Uri fileUri;
		
		// Create intent to take video.
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		
		// Checks for the first activity component that can handle the event.  Only
		// proceed to start activity for video if there is an app that can take video.
		if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO_SESSION);
		}
	}
	
	/**
	 * beginVideoGallery:
	 * Access the gallery where all patient videos recorded through this application are saved.  The
	 * patient should select a video/videos that will be sent to their doctor.
	 */
	public void beginVideoGallery() {
		
		Intent videoChooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		
		//Intent videoChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
		//videoChooseIntent.setType("video/*");
		startActivityForResult(videoChooseIntent, REQUEST_PICK_VIDEO);
	}
	
	/**
	 * getOutputMediaFileUri:
	 * Create a file Uri for saving an image or video 
	 */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/**
	 * getOutputMediaFile:
	 * Create a File for saving an image or video
	 */
	private static File getOutputMediaFile(int type){
		
        // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(appName, "failed to create directory");
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
