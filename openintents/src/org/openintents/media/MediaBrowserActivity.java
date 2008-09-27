package org.openintents.media;

import org.openintents.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

/**
 * Displays available audio content in the ContentProvider.
 * 
 * @author Peli,Zero
 *
 */
public class MediaBrowserActivity extends Activity {
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "MediaBrowserActivity";
	
	private static final int MENU_AUDIO = Menu.FIRST;
	private static final int MENU_VIDEO = Menu.FIRST + 1;
	private static final int MENU_IMAGES = Menu.FIRST + 2;
	
	private static final int MENU_MEDIA_SCANNER = Menu.FIRST + 3;
	
	
	/**
	 * STATE_MAIN performs the main activity.
	 * If an item is clicked, the VIEW activity is invoked
	 * (which starts the MediaPlayer).
	 */
	private static final int STATE_MAIN = 0;
	
	/**
	 * PICK picks an item from the directory of items.
	 * If the item is clicked, it is returned to the calling
	 * activity.
	 */
	private static final int STATE_PICK = 1;
	
	/**
	 * GET_CONTENT gets an item from the directory of items.
	 * The only different to PICK is the MIME type.
	 * (PICK works on the directory, while GET_CONTENT works
	 *  on the item MIME type).
	 * If the item is clicked, it is returned to the calling
	 * activity.
	 */
	private static final int STATE_GET_CONTENT = 2;
	
	/**
	 * STATE_VIEW performs the view activity.
	 * If an item is clicked, the VIEW activity is invoked
	 * (which starts the MediaPlayer).
	 */
	private static final int STATE_VIEW = 3;
	
	/** Current state of Activity. */
	private int mState;
	

	/* Definition of the requestCode for the subactivity. */
    static final private int SUBACTIVITY_MEDIA_SCANNER = 0;
	
	/**
	 * One of the different media types the user can browse.
	 */
	private static final int MEDIA_TYPE_AUDIO = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static final int MEDIA_TYPE_IMAGES = 3;
	
	/** Current media type for browsing. */
	private int mMediaType;
	
	/**
	 * One of the different media locations the user can browse.
	 */
	private static final int MEDIA_LOCATION_INTERNAL = 1;
	private static final int MEDIA_LOCATION_EXTERNAL = 2;
	
	/** Current media location for browsing. */
	private int mMediaLocation;
	
	/** Media URIs */
	Uri mInternalUri;
	Uri mExternalUri;
	
    /** Specifies the relevant columns. */
    private String[] mProjection = new String[] {
        android.provider.BaseColumns._ID,
        android.provider.MediaStore.MediaColumns.TITLE,
        android.provider.MediaStore.MediaColumns.DATA
    };
	/** Specifies the columns displayed in list. */
	private String[] mDisplayProjection= new String[] {
        android.provider.MediaStore.MediaColumns.TITLE,
        android.provider.MediaStore.MediaColumns.DATA
    };
	/** Specifies the views used for columns displayed in list. */
	private int[] mViewList =new int[] {android.R.id.text1, android.R.id.text2};

	private int mLayoutID;

	/** Contains internal media */
	private ListView mInternalList;
	
	/** Contains external media */
	private ListView mExternalList;
	
	private Cursor mInternalCursor;
	private Cursor mExternalCursor;
	
	private ListAdapter mInternalAdapter;
	private ListAdapter mExternalAdapter;
	

	private TabHost mTabHost;
    
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.mediabrowser);
		
        // Default media: Audio
        mMediaType = MEDIA_TYPE_AUDIO;

		// Handle the calling intent
		final Intent intent = getIntent();
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_MAIN)) {
            mState = STATE_MAIN;
        } else if (action.equals(Intent.ACTION_VIEW)) {
            mState = STATE_VIEW;
            
            setMediaTypeFromIntent(intent);
            
        } else if (action.equals(Intent.ACTION_PICK)) {
            mState = STATE_PICK;
            
            setMediaTypeFromIntent(intent);
            
        } else if (action.equals(Intent.ACTION_GET_CONTENT)) {
        	mState = STATE_GET_CONTENT;
        	
        	setMediaTypeFromIntent(intent);
        } else {
            // Unknown action.
            Log.e(TAG, "AudioBrowser: Unknown action, exiting");
            finish();
            return;
        }
        
        
		////////////////////////////////////////////
		// Set up the Tabs

		Context context = this;
        // Get the Resources object from our context
        Resources res = context.getResources();
    
		mTabHost = (TabHost)findViewById(R.id.tabhost);
		mTabHost.setup();
		
		TabSpec tabspec = mTabHost.newTabSpec("internal");
		tabspec.setIndicator(res.getString(R.string.internal_content), 
				res.getDrawable(R.drawable.mobile001a_32));
		tabspec.setContent(R.id.content1);
		mTabHost.addTab(tabspec);
		
		tabspec = mTabHost.newTabSpec("external");
		tabspec.setIndicator(res.getString(R.string.external_content), 
				res.getDrawable(R.drawable.sdcard002a_32));
		tabspec.setContent(R.id.content2);
		mTabHost.addTab(tabspec);
		
		mTabHost.setCurrentTab(0);
		
		if (mMediaLocation == MEDIA_LOCATION_EXTERNAL) {
			// Asking for external content, so we switch to 
			// external tab.
			mTabHost.setCurrentTab(1);
		}
	
		// Find other widgets
		mInternalList = (ListView) findViewById(R.id.internal);
		mExternalList = (ListView) findViewById(R.id.external);
	    
		// Set up lists
		updateLists();

		
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		
		// Modify our overall title depending on the mode we are running in.
        if (mState == STATE_MAIN || mState == STATE_VIEW) {
            setTitle(getText(R.string.media_browser));
        } else if (mState == STATE_PICK) {
            setTitle(getText(R.string.pick_media_file));
            setTitleColor(0xFFAAAAFF);
        } else if (mState == STATE_GET_CONTENT) {
            setTitle(getText(R.string.get_content_media_file));
            setTitleColor(0xFFAAAAFF);
        }

	}
	
	/** Set up the internal and external media content lists. */
	private void updateLists() {
		//updateUri();
		decideUriAndLayout();

	    /////////////////////////////////////////////////
		// First set up the internal media content lists
		
	    mInternalCursor = managedQuery( 
	    		mInternalUri,
	    		mProjection, //Which columns to return. 
	            null,       // WHERE clause--we won't specify.
	            null, null); // Order-by clause.

	    if (mInternalCursor.getCount() < 1) {
	    	// No items in list
	    	mInternalList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1,
					new String[] { "(no " + getMediaTypeString() + " found)" }));
	    	mInternalList.setOnItemClickListener(null);
	    	
	    } else {
	    	// Connect media to list
		    mInternalAdapter = new SimpleCursorAdapter(this,
		             //android.R.layout.simple_list_item_2, // Use a template
		             mLayoutID, // Use a template
		                                                     // that displays a
		                                                     // text view
		             mInternalCursor, // Give the cursor to the list adapter
		             mDisplayProjection,
		             mViewList
					 ); // The "text1" view defined in
		                                         // the XML template
		    
		    mInternalList.setAdapter(mInternalAdapter);
		    
		    mInternalList.setOnItemClickListener(
				new OnItemClickListener() {
					
					public void onItemClick(AdapterView parent, 
							View v, int pos, long id) {
						Cursor c = (Cursor) parent.getItemAtPosition (pos);
						if ((mState == STATE_PICK) || (mState == STATE_GET_CONTENT)) {
							pickItem(mInternalUri,
									c);
						} else {
							playItem(mInternalUri,
									c);
						}
					}
			});
	    }
		
	    ////////////////////////////////////////////
		// Set up the external media content lists
		
	    mExternalCursor = managedQuery( 
	    		mExternalUri,
	            mProjection, //Which columns to return. 
	            null,       // WHERE clause--we won't specify.
	            null, null); // Order-by clause.

	    if (mExternalCursor == null) {
	    	// No SD card available
	    	mExternalList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1,
					new String[] { "(no SD card found)" }));
	    	mExternalList.setOnItemClickListener(null);
	    } else if (mExternalCursor.getCount() < 1) {
	    	// No items in list
	    	mExternalList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1,
					new String[] { "(no " + getMediaTypeString() + " found)" }));
	    	mExternalList.setOnItemClickListener(null);
	    	
	    } else {
	    	// Connect media to list
			mExternalAdapter = new SimpleCursorAdapter(this,
		             //android.R.layout.simple_list_item_2, // Use a template
		             mLayoutID, // Use a template
		             mExternalCursor, // Give the cursor to the list adapter
		             mDisplayProjection, // Map Column Names to..
		             mViewList //List of Views
					 ); // The "text1" view defined in
		                                         // the XML template
		    
		    mExternalList.setAdapter(mExternalAdapter);
			
		    mExternalList.setOnItemClickListener(
				new OnItemClickListener() {
					
					public void onItemClick(AdapterView parent, 
							View v, int pos, long id) {
						Cursor c = (Cursor) parent.getItemAtPosition (pos);
						if ((mState == STATE_PICK) || (mState == STATE_GET_CONTENT)) {
							pickItem(mExternalUri,
									c);
						} else {
							playItem(mExternalUri,
									c);
						}
					}
			});
	    }
	}

	/** Sets the media type from an intent. */
	private void setMediaTypeFromIntent(final Intent intent) {
		// Select media:
		Uri data = intent.getData();
		String type = intent.getType();
		
		// To avoid failure in the following comparisons:
		if (data == null) data = Uri.parse("");
		if (type == null) type = "";
		
		Log.i(TAG, "MediaBrowserActivity: data. " + data + ", type: " + type + ".");
		if (data.compareTo(
				android.provider.MediaStore.Audio.Media
				.INTERNAL_CONTENT_URI) == 0
			|| type.startsWith("audio/") || type.equals("audio")) {
			mMediaType = MEDIA_TYPE_AUDIO;
			mMediaLocation = MEDIA_LOCATION_INTERNAL;
		} else if (data.compareTo(
		        android.provider.MediaStore.Audio.Media
		        .EXTERNAL_CONTENT_URI) == 0) {
			// We pick video data:
			mMediaType = MEDIA_TYPE_AUDIO;
			mMediaLocation = MEDIA_LOCATION_EXTERNAL;
		} else if ((data.compareTo(
				android.provider.MediaStore.Video.Media
				.INTERNAL_CONTENT_URI) == 0)
			|| type.startsWith("video/") || type.equals("video")) {
			mMediaType = MEDIA_TYPE_VIDEO;
			mMediaLocation = MEDIA_LOCATION_INTERNAL;
		} else if (data.compareTo(
		        android.provider.MediaStore.Video.Media
		        .EXTERNAL_CONTENT_URI) == 0) {
			// We pick video data:
			mMediaType = MEDIA_TYPE_VIDEO;
			mMediaLocation = MEDIA_LOCATION_EXTERNAL;
		} else if ((data.compareTo(
				android.provider.MediaStore.Images.Media
				.INTERNAL_CONTENT_URI) == 0)
			|| type.startsWith("image/") || type.equals("image")) {
			mMediaType = MEDIA_TYPE_IMAGES;
			mMediaLocation = MEDIA_LOCATION_INTERNAL;
		} else if (data.compareTo(
		        android.provider.MediaStore.Images.Media
		        .EXTERNAL_CONTENT_URI) == 0) {
			// We pick images data:
			mMediaType = MEDIA_TYPE_IMAGES;
			mMediaLocation = MEDIA_LOCATION_EXTERNAL;
		}
	}
	
	/**
	 * Updates the internal and external URI corresponding to the current media type.
	 */
	private void decideUriAndLayout() {
		switch (mMediaType) {
		case MEDIA_TYPE_AUDIO:
			mInternalUri = android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
		    mExternalUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			mProjection  = new String[] {
				android.provider.MediaStore.Audio.Media._ID,
				android.provider.MediaStore.Audio.Media.TITLE,
				android.provider.MediaStore.Audio.Media.DATA,
				android.provider.MediaStore.Audio.Media.ARTIST,
				android.provider.MediaStore.Audio.Media.ALBUM,				
			};
			mDisplayProjection= new String[] {
				android.provider.MediaStore.Audio.Media.TITLE,
				android.provider.MediaStore.Audio.Media.ARTIST,
				android.provider.MediaStore.Audio.Media.DATA,
				//android.provider.MediaStore.Audio.Media.ALBUM,				
			};
			mViewList= new int[]{
				R.id.title, 
				R.id.artist, 
				R.id.data
			};
			mLayoutID=R.layout.mediabrowser_audio_row;
		    break;
		    
		case MEDIA_TYPE_VIDEO:
			mInternalUri = android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;
		    mExternalUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			break;
			
		case MEDIA_TYPE_IMAGES:
			mInternalUri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
		    mExternalUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			break; 
			
		default:
			throw new java.lang.InternalError("MediaBrowserActivity: Unsupported media type: " + mMediaType);
		}
	}
	
	/** Returns current media type string */
	private String getMediaTypeString() {
		switch (mMediaType) {
		case MEDIA_TYPE_AUDIO:
			return getText(R.string.audio).toString();
		    
		case MEDIA_TYPE_VIDEO:
			return getText(R.string.video).toString();
		    
		case MEDIA_TYPE_IMAGES:
			return getText(R.string.images).toString();
		    
		default:
			return getText(R.string.media).toString();
		}
	}

	/** Picks an item and returns to calling activity. */
	private void pickItem(Uri baseUri, Cursor c) {

        int indexID = c.getColumnIndex(android.provider.BaseColumns._ID);
		long itemId = c.getLong(indexID);
		Uri url = ContentUris.withAppendedId(baseUri, itemId);
		
		setResult(RESULT_OK, new Intent(Intent.ACTION_PICK, url));
		finish();
	}
	
	/** Plays an item. */
	private void playItem(Uri baseUri, Cursor c) {

        int indexID = c.getColumnIndex(android.provider.BaseColumns._ID);
		long itemId = c.getLong(indexID);
		Uri url = ContentUris.withAppendedId(baseUri, itemId);
		
		Intent intent = new Intent(Intent.ACTION_VIEW, url);
		startActivity(intent);
	}
	

	///////////////////////////////////////////////////////
	//
	// Menu

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Standard menu
		menu.add(0, MENU_AUDIO, 0, R.string.audio)
			.setIcon(R.drawable.music001a)
			.setShortcut('0', 'a');
		menu.add(0, MENU_VIDEO, 0, R.string.video)
			.setIcon(R.drawable.video002a)
			.setShortcut('1', 'v');
		menu.add(0, MENU_IMAGES, 0, R.string.images)
			.setIcon(R.drawable.image001a)
			.setShortcut('2', 'i');
		menu.add(0, MENU_MEDIA_SCANNER, 0, R.string.media_scanner)
			.setIcon(R.drawable.sdcard002a)
			.setShortcut('2', 'i');
		
	
		// Generate any additional actions that can be performed on the
        // overall list.  This allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
            Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, MediaBrowserActivity.class),
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
		case MENU_AUDIO:
			mMediaType = MEDIA_TYPE_AUDIO;
			updateLists();
			return true;
			
		case MENU_VIDEO:
			mMediaType = MEDIA_TYPE_VIDEO;
			updateLists();
			return true;
		
		case MENU_IMAGES:
			mMediaType = MEDIA_TYPE_IMAGES;
			updateLists();
			return true;
		
		case MENU_MEDIA_SCANNER:
			MediaScannerEngine mediaScanner = new MediaScannerEngine(this);

			mediaScanner.scanInThread();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			Intent resaultIntent) {

        if (requestCode == SUBACTIVITY_MEDIA_SCANNER) {
        	// In any case, repopulate the lists
        	// independent of resultCode == RESULT_CANCELED
        	updateLists();
        }
	}



}
