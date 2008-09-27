package org.openintents.media;


import java.io.IOException;
import java.text.DecimalFormat;

import org.openintents.R;
import org.openintents.widget.Slider;
import org.openintents.widget.Slider.OnPositionChangedListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MediaPlayerActivity extends Activity implements 
	OnBufferingUpdateListener, OnCompletionListener, 
	MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
	
	/** TAG for log messages. */
	private static final String TAG = "PlayerActivity"; 
	
	private static final int MENU_OPEN_AUDIO = Menu.FIRST;
	private static final int MENU_OPEN_VIDEO = Menu.FIRST + 1;
	
	/**
	 * One of the different states this activity can run.
	 */
	private static final int STATE_MAIN = 0;
	private static final int STATE_VIEW = 1;
	
	/* Definition of the requestCode for the subactivity. */
    static final private int SUBACTIVITY_MEDIA_BROWSER = 0;


	/** Current state */
	private int mState;
	
	/**
	 * One of the different media types the user can play.
	 * VOID: no selection has been done yet.
	 */
	private static final int MEDIA_TYPE_VOID = 0;
	private static final int MEDIA_TYPE_AUDIO = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	
	/** Current media type for browsing. */
	private int mMediaType;
	
	/** 
	 * Whether a media file is being played.
	 * 
	 *  This helps to stop mHandler from being called
	 *  when there is no music playing and no slider 
	 *  moving. Only the playMedia() routine sets
	 *  mPlaying = true. 
	 */
	private boolean mPlaying;
	
	/** 
	 * Play the file as soon as the video surface has been created.
	 * 
	 * 
	 */
	private boolean mPlayIfSurfaceCreated;
	
	/**
	 * One of the different views being presented.
	 */
	private static final int MEDIA_VIEW_VIDEO = 1;
	private static final int MEDIA_VIEW_INFO = 2;
	
	
    /** Specifies the relevant columns. */
    String[] mProjection = new String[] {
        android.provider.BaseColumns._ID,
        android.provider.MediaStore.MediaColumns.TITLE,
        android.provider.MediaStore.MediaColumns.DATA
    };
    
	/** URI of current media file. */
	private Uri mURI;
	
	/** Location of the media file. */
	private String mFilename;
	
	/** Title of the media file. */
	private String mTitle;
    
	private Cursor mCursor;
	
	/** Message for message handler. */
	private static final int UPDATE_POSITION = 1;
    
	private MediaPlayer mp; 
	private SurfaceView mPreview; 
    private SurfaceHolder mHolder; 
    private boolean mSurfaceCreated;
	
	private Button mPlay; 
	private Button mPause; 
	private Button mStop; 
	private Button mReset; 
	private TextView mPositionText;
	private Slider mSlider;
	
	/**
	 * Widgets corresponding to info layout.
	 * 
	 * Info layout shows song name, artist, album, etc.
	 */
	private LinearLayout mInfoView;
	private TextView mNameField;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        /* TODO The following does not seem to work. 
         * 
         * 
        // Before we can inflate the view, we have to set
        // the correct R-values:
        Slider.R.styleable.Slider = R.styleable.Slider;
        Slider.R.styleable.Slider_max = R.styleable.Slider_max;
        Slider.R.styleable.Slider_min = R.styleable.Slider_min;
        Slider.R.styleable.Slider_pos = R.styleable.Slider_pos;
        Slider.R.styleable.Slider_background = R.styleable.Slider_background;
        Slider.R.styleable.Slider_knob = R.styleable.Slider_knob;
        */
        
        setContentView(R.layout.mediaplayer);
        
        mp = null;
        mSurfaceCreated = false;
        mMediaType = MEDIA_TYPE_VOID;
        mPlaying = false;
        mPlayIfSurfaceCreated = false;
        
        // Handle the calling intent
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_MAIN)) {
            mState = STATE_MAIN;
            mURI = null;
            
            // TODO: Here we could load a fild
            // that the user watched last time.
        } else if (action.equals(Intent.ACTION_VIEW)) {
            mState = STATE_VIEW;
            mURI = intent.getData();
            setMediaTypeFromUri(mURI);
        } else {
            // Unknown action.
            Log.e(TAG, "Player: Unknown action, exiting");
            finish();
            return;
        }
                
        ////////////////////////////////////////////
        // Set up widgets
        mPreview = (SurfaceView) findViewById(R.id.surface); 
        mInfoView = (LinearLayout) findViewById(R.id.info);
        
        if (mMediaType == MEDIA_TYPE_VIDEO) {
	        // Set the transparency 
	        getWindow().setFormat(PixelFormat.TRANSPARENT); 
	        mPreview.setVisibility(View.VISIBLE);
        	mInfoView.setVisibility(View.GONE);
        } else {
        	getWindow().setFormat(PixelFormat.OPAQUE); 
        	mPreview.setVisibility(View.GONE);
        	mInfoView.setVisibility(View.VISIBLE);
        }
        
        // Set a size for the video screen 
        mHolder = mPreview.getHolder(); 
        mHolder.addCallback(this); 
        //mHolder.setFixedSize(200, 150); 
        
        mPlay = (Button) findViewById(R.id.play); 
        mPlay.setOnClickListener(new View.OnClickListener() { 
            public void onClick(View view) { 
                playMedia(); 
            } 
        }); 
        
        mPause = (Button) findViewById(R.id.pause); 
        mPause.setOnClickListener(new View.OnClickListener() { 
            public void onClick(View view) { 
                pauseMedia(); 
            } 
        }); 
        
        mStop = (Button) findViewById(R.id.stop); 
        mStop.setOnClickListener(new View.OnClickListener() { 
            public void onClick(View view) { 
                stopMedia(); 
            } 
        }); 
        
        mReset = (Button) findViewById(R.id.reset); 
        mReset.setOnClickListener(new View.OnClickListener() { 
            public void onClick(View view) { 
            	
            	// TODO Actually, this button could be "setLooping()"
            	
                resetMedia(); 
            } 
        }); 
        

        mPositionText = (TextView) findViewById(R.id.position); 
        mPositionText.setText("00:00 / 00:00");
        mPositionText.setTextColor(0xff000088);
        
        mNameField = (TextView) findViewById(R.id.name_field); 
        mNameField.setText("");
        
        mSlider = (Slider) findViewById(R.id.slider);
        mSlider.setBackground(getResources().getDrawable(R.drawable.shiny_slider_background001c));
        mSlider.setKnob(getResources().getDrawable(R.drawable.shiny_slider_knob001a));
        mSlider.setPosition(0);
        mSlider.setOnPositionChangedListener(
        		new OnPositionChangedListener() {

					/**
					 * Changed slider to new position.
					 * @see org.openintents.widget.Slider.OnPositionChangedListener#onPositionChangeCompleted()
					 */
					public void onPositionChangeCompleted() {
						int newPos = mSlider.pos;
						if (mp != null) {
							mp.seekTo(newPos);
						}
					}

					/* (non-Javadoc)
					 * @see org.openintents.widget.Slider.OnPositionChangedListener#onPositionChanged(org.openintents.widget.Slider, int, int)
					 */
					public void onPositionChanged(Slider slider,
							int oldPosition, int newPosition) {
						// Update text field:
						if (mp != null) {
							int timeMax = mp.getDuration();
							mPositionText.setText("" 
			            			+ formatTime(newPosition) + " / " 
			            			+ formatTime(timeMax));		
						}
					}
        			
        		});
        

        // 
        loadFileFromUri();
    }

    @Override    
    protected void
    onPause() {
    	super.onPause();
    	// Stop the Media Player
    	// TODO: Later it would be nice to have music run in service.
    	// Let us clean up
    	if (mp != null) {
	    	mp.release();
	    	mp = null;
    	}
    }
    
	/** Sets the media type from an intent. */
	private void setMediaTypeFromUri(Uri data) {
		// Chop off last part (that contains the row number in the database)
		String uriString = data.toString();
		int split_at = uriString.lastIndexOf('/');
		uriString = uriString.substring(0, split_at);
		data = Uri.parse(uriString);
		
		if (data.compareTo(
				android.provider.MediaStore.Audio.Media
				.INTERNAL_CONTENT_URI) == 0) {
			mMediaType = MEDIA_TYPE_AUDIO;
		} else if (data.compareTo(
		        android.provider.MediaStore.Audio.Media
		        .EXTERNAL_CONTENT_URI) == 0) {
			// We pick video data:
			mMediaType = MEDIA_TYPE_AUDIO;
		} else if (data.compareTo(
				android.provider.MediaStore.Video.Media
				.INTERNAL_CONTENT_URI) == 0) {
			mMediaType = MEDIA_TYPE_VIDEO;
		} else if (data.compareTo(
		        android.provider.MediaStore.Video.Media
		        .EXTERNAL_CONTENT_URI) == 0) {
			// We pick video data:
			mMediaType = MEDIA_TYPE_VIDEO;
		}
	}

    /**
     * Load file from URI if it has been set by the calling activity.
     */
	private void loadFileFromUri() {
		// Get the media content
        if (mURI != null) {
	        mCursor = managedQuery(mURI, mProjection, null, null, null);
	        if (mCursor != null && mCursor.moveToFirst()) {
		        int indexDATA = mCursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DATA);
				mFilename = mCursor.getString(indexDATA);
				
				int indexTitle = mCursor.getColumnIndex(android.provider.MediaStore.MediaColumns.TITLE);
				mTitle = mCursor.getString(indexTitle);
				setTitle(mTitle);
				
				// Set information in info view:
				mNameField.setText(mTitle);
				
				if (mSurfaceCreated || mMediaType == MEDIA_TYPE_AUDIO) {
					// We have already created the surface.
					// Or we don't need it since we play Audio.
					// Let's play the file immediately:
					playMedia();	
				} else {
					// Let us wait until the surface is created
					// and start playing then
					mPlayIfSurfaceCreated = true;
				}
				return;
	        }
        }
 
        // No result:
    	mFilename = "";
    	setTitle(getText(R.string.media_player));
	}
    

    private void playMedia() { 
	    try { 
	    	Log.i(TAG,"Starting music");
	        // If the path has not changed, just start the media player 
	        if (mp != null) { 
	        	Log.i(TAG,"Re-start music");
	            
	            mp.start(); 
	            if (! mPlaying ) {
		            mPlaying = true;
			        mHandler.sendMessage(mHandler.obtainMessage(UPDATE_POSITION));
	            }
	            return; 
	        } 
	        
	        if (mFilename.equals("")) {
	        	Log.i(TAG, "No file chosen yet");
	        	
	        	return;
	        }
	        
	        // Create a new media player and set the listeners 
	        mp = new MediaPlayer(); 
	        //mp.setOnErrorListener(this); 
	        mp.setOnBufferingUpdateListener(this); 
	        mp.setOnCompletionListener(this); 
	        mp.setOnPreparedListener(this); 
	        mp.setAudioStreamType(2); 
	        if (mSurfaceCreated && mMediaType == MEDIA_TYPE_VIDEO) {
	        	mp.setDisplay(mHolder); 
	        }
	        
	        if (mSurfaceCreated && mMediaType == MEDIA_TYPE_VIDEO) {
    	        // Set the transparency 
    	        getWindow().setFormat(PixelFormat.TRANSPARENT); 
    	        mPreview.setVisibility(View.VISIBLE);
            	mInfoView.setVisibility(View.GONE);
            } else {
            	getWindow().setFormat(PixelFormat.OPAQUE); 
            	mPreview.setVisibility(View.GONE);
            	mInfoView.setVisibility(View.VISIBLE);
            }
	        
	        try { 
	        	mp.setDataSource(mFilename); 
	        	
	        	Log.i(TAG,"setDataSource OK");
	        } catch (IOException e) { 
	        	Log.e(TAG, e.getMessage(), e);
	        }
	        try{ 
	               mp.prepare(); 
	               Log.i(TAG,"prepare OK");
	        } catch(Exception e) { 
	          Log.e("\n\nprepare",e.toString()); 
	        } 
	        
	        mp.start(); 
	        Log.i(TAG,"start OK");
	        
	        mPlaying = true;
	        mHandler.sendMessage(mHandler.obtainMessage(UPDATE_POSITION));
	
	    } catch (Exception e) { 
	        Log.e(TAG, "error: " + e.getMessage(), e); 
	    } 
    } 

    public void pauseMedia() {
    	if (mp != null) {
    		mp.pause();
    	}
    	mPlaying = false;	       
    }
    
    public void stopMedia() {
    	if (mp != null) {
    		//mp.reset();
    		mp.stop();
    		mp.release();
    		mp = null;
    	}
    	mPlaying = false;
    }
    
    public void resetMedia() {
    	if (mp != null) {
    		//mp.reset();
    		mp.seekTo(0);
    	}
    }
    
    public void onBufferingUpdate(MediaPlayer arg0, int percent) { 
    	Log.d(TAG, "onBufferingUpdate percent:" + percent); 
    } 


    /**
     * Is called when the song reached is final position.
     */
    public void onCompletion(MediaPlayer arg0) { 
    	Log.d(TAG, "onCompletion called"); 
    	
    	// Let us clean up
    	if (mp != null) {
	    	mp.release();
	    	mp = null;
    	}
    	mPlaying = false;
    } 


    public void onPrepared(MediaPlayer mediaplayer) { 
	    Log.d(TAG, "onPrepared called"); 
	    mediaplayer.start(); 
    } 
    

    public void surfaceChanged(SurfaceHolder surfaceholder, 
    		int format, int width, int height) { 
        Log.d(TAG, "surfaceChanged called"); 
    } 
    
    public void surfaceCreated(SurfaceHolder holder) { 
      Log.d(TAG, "surfaceCreated called");
      // Start playing as soon as surface is ready.
		mSurfaceCreated = true;
		if (mPlayIfSurfaceCreated) {
			// A playMedia() command had to wait for surface,
			// so now we can start playing.
			mPlayIfSurfaceCreated = false;
			playMedia();			
		}
    } 
    
    public void surfaceDestroyed(SurfaceHolder surfaceholder) { 
        Log.d(TAG, "surfaceDestroyed called"); 
        mSurfaceCreated = false;
    } 

    /** Handler for timing messages. */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_POSITION) {
            	if (mp == null) {
            		mPositionText.setText("00:00 / 00:00");
            		mSlider.min = 0;
	            	mSlider.max = 100;
	            	mSlider.setPosition(0);
	            	mPlaying = false;
            	} else {
	            	int time = mp.getCurrentPosition();
	            	int timeMax = mp.getDuration();
	            	if (mSlider.mTouchState == Slider.STATE_RELEASED) {
		            	mPositionText.setText("" 
		            			+ formatTime(time) + " / " 
		            			+ formatTime(timeMax));
	            	}
	            	
	            	mSlider.min = 0;
	            	mSlider.max = timeMax;
	            	mSlider.setPosition(time);
	            	
	            	if (mPlaying) {
	            		sendMessageDelayed(obtainMessage(UPDATE_POSITION), 200);
	            	}
            	}
            }
        }
    };
    
    static DecimalFormat mTimeDecimalFormat = new DecimalFormat("00");
	
    public String formatTime(int ms) {
    	int s = ms / 1000; // seconds
    	int m = s / 60;
    	s = s - 60 * m;
    	int h = m / 60;
    	m = m - 60 * h;
    	String m_s = mTimeDecimalFormat.format(m) + ":" 
    		+ mTimeDecimalFormat.format(s);
    	if (h > 0) {
    		// show also hour
    		return "" + h + ":" + m_s;
    	} else {
    		// Show only minute:second
    		return m_s;
    	}
    }

	///////////////////////////////////////////////////////
	//
	// Menu

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Standard menu
		menu.add(0, MENU_OPEN_AUDIO, 0, R.string.open_audio)
			.setIcon(R.drawable.music001a)
			.setShortcut('0', 'o');
		menu.add(0, MENU_OPEN_VIDEO, 0, R.string.open_video)
			.setIcon(R.drawable.video002a)
		.setShortcut('0', 'o');
		
	
		// Generate any additional actions that can be performed on the
        // overall list.  This allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
            Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, MediaPlayerActivity.class),
            null, intent, 0, null);
        
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPEN_AUDIO:
			// Start subactivity for media browser:
			
			return startSubActivityFromCurrentUri(
					android.provider.MediaStore
					.Audio.Media.INTERNAL_CONTENT_URI);
			
		case MENU_OPEN_VIDEO:
			// Start subactivity for media browser:
			
			return startSubActivityFromCurrentUri(
					android.provider.MediaStore
					.Video.Media.INTERNAL_CONTENT_URI);
		}
		return super.onOptionsItemSelected(item);
	}


	private boolean startSubActivityFromCurrentUri(Uri defaultUri) {
		if ((mState == STATE_VIEW) && (mURI != null)) {
			// Pick the same type that we had
			
			// Split off last piece containing the data row number
			String uriString = mURI.toString();
			int split_at = uriString.lastIndexOf('/');
			uriString = uriString.substring(0, split_at);
			
			Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse(uriString));
			startActivityForResult(intent, SUBACTIVITY_MEDIA_BROWSER);
		} else {
			// Let us pick a generic type given through the parameter.
			// TODO: If user used EXTERNAL_CONTENT_URI last time,
			// one may provide external content; same with Video.
			Intent intent = new Intent(Intent.ACTION_PICK, 
					defaultUri);
			startActivityForResult(intent, SUBACTIVITY_MEDIA_BROWSER);
		}
		
		return true;
	}


	/**
     * This method is called when the sending activity has finished, with the
     * result it supplied.
     * 
     * @param requestCode The original request code as given to
     *                    startActivity().
     * @param resultCode From sending activity as per setResult().
     * @param data From sending activity as per setResult().
     * @param extras From sending activity as per setResult().
     * 
     * @see android.app.Activity#onActivityResult(int, int, java.lang.String, android.os.Bundle)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {

        if (requestCode == SUBACTIVITY_MEDIA_BROWSER) {

        	if (resultCode == RESULT_CANCELED) {
                // Don't do anything.

        		// Except for:
            	// Set view back to info view
        		// (Otherwise the video background
        		//  may not be available anymore
        		//  and some random remaining background
        		//  displayed).
            	getWindow().setFormat(PixelFormat.OPAQUE); 
            	mPreview.setVisibility(View.GONE);
            	mInfoView.setVisibility(View.VISIBLE);
            } else {
                // Start playing the file:
            	
            	// First stop the old one
            	stopMedia();
            	
            	mURI = resultIntent.getData();
            	
            	// Set video settings
            	setMediaTypeFromUri(mURI);

                if (mMediaType == MEDIA_TYPE_VIDEO) {
        	        // Set the transparency 
        	        getWindow().setFormat(PixelFormat.TRANSPARENT); 
        	        mPreview.setVisibility(View.VISIBLE);
                	mInfoView.setVisibility(View.GONE);
                } else {
                	getWindow().setFormat(PixelFormat.OPAQUE); 
                	mPreview.setVisibility(View.GONE);
                	mInfoView.setVisibility(View.VISIBLE);
                }
                
            	loadFileFromUri();
                
            }

        }
	}


}
