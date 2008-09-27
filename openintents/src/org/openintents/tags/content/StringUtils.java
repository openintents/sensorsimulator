package org.openintents.tags.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	private static final Pattern BLANK_PATTERN = Pattern.compile("^\\p{javaWhitespace}*$");

	public static boolean isBlank(CharSequence string) {
		if ((string == null) || (string.length() == 0)) {
			return true;
		}
		
		Matcher m = BLANK_PATTERN.matcher(string);
		return m.matches();
	}
	
	public static boolean isNotBlank(CharSequence string) {
		return !isBlank(string);
	}
}
