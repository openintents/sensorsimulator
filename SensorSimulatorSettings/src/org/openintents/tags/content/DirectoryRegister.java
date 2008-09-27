package org.openintents.tags.content;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import org.openintents.provider.ContentIndex;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class DirectoryRegister {

	private static final String TAG_INTENT = "intent";
	private static final String TAG_DIR = "dir";
	private static final String TAG_PACKAGE = "package";

	private static final String ATTR_TEXT_COLUMNS = "text-columns";
	private static final String ATTR_ID_COLUMN = "id-column";
	private static final String ATTR_TIME_COLUMN = "time-column";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ACTION = "action";
	private static final String ATTR_URI = "uri";

	private static final String TAG = "DirectoryRegister";
	private ContentIndex mContentIndex;


	public DirectoryRegister(Context context) {		
		mContentIndex = new ContentIndex(context.getContentResolver());
	}

	public void fromXML(InputStream in) throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new InputStreamReader(in));

		register(xpp);
	}

	public void register(XmlPullParser xpp) throws Exception {
		Stack<Directory> stack = new Stack<Directory>();

		String package_name = null;

		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tag = xpp.getName();

			if (eventType == XmlPullParser.START_TAG) {
				if (tag.equals(TAG_PACKAGE)) {
					package_name = startPackage(xpp);
				} else if (tag.equals(TAG_DIR)) {
					startDirectory(xpp, stack, package_name);
				} else if (tag.equals(TAG_INTENT)) {
					startIntent(xpp, stack);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (tag.equals(TAG_DIR)) {
					endDirectory(stack);
				}
			}

			eventType = xpp.next();
		}
	}

	private void endDirectory(Stack<Directory> stack) {
		if (stack.size() > 0) {
			mContentIndex.updateDirectory(stack.pop());
		}
	}

	private void startIntent(XmlPullParser xpp, Stack<Directory> stack)
			throws DirectoryRegisterException {
		if (stack.size() > 0) {
			Directory peek = stack.peek();
			peek.intent_uri = xpp.getAttributeValue(null, ATTR_URI);
			peek.intent_action = xpp.getAttributeValue(null, ATTR_ACTION);
		}
	}

	private void startDirectory(XmlPullParser xpp, Stack<Directory> stack,
			String package_name) throws DirectoryRegisterException {
		Directory dir = new Directory();
		dir.parent_id = (stack.size() > 0) ? stack.peek().id : 0L;
		dir.package_name = package_name;
		dir.name = xpp.getAttributeValue(null, ATTR_NAME);
		dir.text_columns = xpp.getAttributeValue(null, ATTR_TEXT_COLUMNS);
		dir.id_column = xpp.getAttributeValue(null, ATTR_ID_COLUMN);
		dir.time_column = xpp.getAttributeValue(null, ATTR_TIME_COLUMN);
		dir.uri = xpp.getAttributeValue(null, ATTR_URI);

		dir.name = StringUtils.isBlank(dir.name) ? package_name : dir.name;
		dir.id_column = StringUtils.isBlank(dir.id_column) ? "_id"
				: dir.id_column;

		notBlank(xpp, ATTR_URI, dir.uri);

		mContentIndex.addDirectory(dir);
		stack.push(dir);
	}

	private String startPackage(XmlPullParser xpp)
			throws DirectoryRegisterException {
		String package_name = xpp.getAttributeValue(null, ATTR_NAME);

		notBlank(xpp, ATTR_NAME, package_name);

		mContentIndex.deletePackage(package_name);
		return package_name;
	}

	private void notBlank(XmlPullParser xpp, String attr, String value)
			throws DirectoryRegisterException {
		if (StringUtils.isBlank(value)) {
			int no = xpp.getLineNumber();
			throw new DirectoryRegisterException("Line " + no + " : '" + attr
					+ "' is required.");
		}
	}


}
