package org.openintents.tags.content;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HTTPUtils {

	public static class StreamAndHeader {

		public String mHeaderValue;
		public InputStream mStream;

		public StreamAndHeader(InputStream in, String headerValue) {
			mStream = in;
			mHeaderValue = headerValue;
		}

	}

	private static final String TAG = "HTTPUtils";
	private static final int STATUS_OK = 200;
	
	public static InputStream open(String url) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		url = encodeUrl(url);
		Log.v(TAG, "encoded url: " + url);

		HttpResponse response = null;
		//try {
			HttpGet getMethod = new HttpGet(url);
			response = client.execute(getMethod);

		//} catch (URISyntaxException e) {
		//	Log.e(TAG, e.getMessage(), e);
		//}

		if (response != null
				&& response.getStatusLine().getStatusCode() == STATUS_OK) {
			InputStream in = response.getEntity().getContent();
			return in;
		} else {
			return null;
		}
	}

	public static HttpEntity getEntity(String url) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		url = encodeUrl(url);
		Log.v(TAG, "encoded url: " + url);

		HttpResponse response = null;
		//try {
			HttpGet getMethod = new HttpGet(url);
			response = client.execute(getMethod);

		//} catch (URISyntaxException e) {
		//	Log.e(TAG, e.getMessage(), e);
		//}

		if (response != null
				&& response.getStatusLine().getStatusCode() == STATUS_OK) {
			HttpEntity entity = response.getEntity();
			return entity;
		} else {
			return null;
		}
	}
	
	public static StreamAndHeader openWithHeader(String url, String headerName)
			throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		url = encodeUrl(url);
		Log.v(TAG, "encoded url: " + url);

		HttpGet getMethod = null;
		HttpResponse response = null;
		//try {
			getMethod = new HttpGet(url);
			response = client.execute(getMethod);
		//} catch (URISyntaxException e) {
		//	Log.e(TAG, e.getMessage(), e);
		//}

		if (response != null
				&& response.getStatusLine().getStatusCode() == STATUS_OK) {
			Log.i(TAG, "HTTP GET: Status OK");
			InputStream in = response.getEntity().getContent();
			Log.i(TAG, "Content length: " + response.getEntity().getContentLength());
			Log.i(TAG, "Input stream available: " + in.available());

			Header header = response.getFirstHeader(headerName);
			
			String headerValue = null;
			
			if (header != null) {
				headerValue = header.getValue();
				Log.v(TAG, "md5 of opened url: " + headerValue);
			}

			return new StreamAndHeader(in, headerValue);
		} else {
			return null;
		}

	}

	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	private static String encodeUrl(String url) {
		if (url != null) {
			url.replaceAll(" ", "%20");
		}
		return url;
	}
}
