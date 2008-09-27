package org.openintents.media;

import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.util.Log;

public class MediaUtils {
	/** TAG for log messages. */
	static final String TAG = "MediaUtils";

	/**
	 * Returns the MIME type for a given file name, based on its extension.
	 * 
	 * @param filename
	 * @return MIME type; "" if unknown; null if filename is null.
	 */
	public static String getMimeType(String filename) {
		String mimeType = null;

		if (filename == null) {
			return mimeType;
		}
		if (filename.endsWith(".3gp")) {
			mimeType = "video/3gpp";
		} else if (filename.endsWith(".mid")) {
			mimeType = "audio/mid";
		} else if (filename.endsWith(".mp3")) {
			mimeType = "audio/mpeg";
		} else if (filename.endsWith(".xml")) {
			mimeType = "text/xml";
		} else {
			Log.i("TAG", "Unknown media type of file '" + filename + "'");
			mimeType = "";
		}
		return mimeType;
	}

	/**
	 * Whether the filename is a video file.
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isVideo(String filename) {
		String mimeType = getMimeType(filename);
		if (mimeType != null && mimeType.startsWith("video/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Whether the URI is a local one.
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isLocal(String uri) {
		if (uri != null && !uri.startsWith("http://")) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the extension of a file name, like ".png" or ".jpg".
	 * 
	 * @param uri
	 * @return Extension including the dot("."); "" if there is no extension;
	 *         null if uri was null.
	 */
	public static String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0) {
			return uri.substring(dot);
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * Returns true if uri is a media uri.
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isMediaUri(String uri) {
		if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
			return true;
		} else {
			return false;
		}
	}
}
