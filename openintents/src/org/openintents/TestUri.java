package org.openintents;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.http.client.methods.HttpGet;

public class TestUri extends TestCase {
	public void testUri1() throws UnsupportedEncodingException,
			URISyntaxException {
		System.out
				.println("http://www.openintents.org/s4p6udke/php/sync-server/media/Kopie von OpenIntents01f.mid"
						.replaceAll(" ", "%20"));
		HttpGet m = new HttpGet(
				URLEncoder
						.encode(
								"http://www.openintents.org/s4p6udke/php/sync-server/media/Kopie von OpenIntents01f.mid",
								"ascii"));
		m.getURI();
	}

}
