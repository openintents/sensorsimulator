package org.openintents.provider;

import android.net.Uri;

public abstract class Intents {
	public static final Uri CONTENT_URI = Uri.parse("openintents://intents");
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_ACTION = "action";
	public static final String EXTRA_URI = "uri";
	
	/** boolean extra flag indicating whether action list should include all android actions.*/ 
	public static final String EXTRA_ANDROID_ACTIONS = "androidActions";
	/** string extra containing comma separated list of actions that should be included in action list.*/
	public static final String EXTRA_ACTION_LIST = "actionList";
	
	public static final String TYPE_PREFIX_DIR = "vnd.android.cursor.dir";
	public static final String TYPE_PREFIX_ITEM = "vnd.android.cursor.item";

}
