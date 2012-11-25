package org.openintents.tools.simulator.logging;

import java.util.LinkedList;
import java.util.List;

public class Logg {

	private static List<LoggPrintable> mLoggPrintables;

	// for testing
	static {
		mLoggPrintables = new LinkedList<LoggPrintable>();
		mLoggPrintables.add(new LoggPrintable() {

			@Override
			public void println(String msg) {
				System.out.println(msg);
			}
		});
	}

	public static void addLoggPrintable(LoggPrintable printable) {
		mLoggPrintables.add(printable);
	}
	
	/**
	 * Debug level logging.
	 * 
	 * @param tag
	 *            name
	 * @param msg
	 *            message
	 */
	public static void d(String tag, String msg) {
		for (LoggPrintable printable: mLoggPrintables)
			printable.println(tag + ": " + msg);
	}

	/**
	 * Error level logging.
	 * 
	 * @param tag
	 *            name
	 * @param msg
	 *            message
	 */
	public static void e(String tag, String msg) {
		for (LoggPrintable printable: mLoggPrintables)
			printable.println(tag + ": " + msg);
	}

	/**
	 * Info level logging.
	 * 
	 * @param tag
	 *            name
	 * @param msg
	 *            message
	 */
	public static void i(String tag, String msg) {
		for (LoggPrintable printable: mLoggPrintables)
			printable.println(tag + ": " + msg);
	}
}
