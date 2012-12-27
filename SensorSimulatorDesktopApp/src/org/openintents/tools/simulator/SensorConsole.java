package org.openintents.tools.simulator;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import org.openintents.sensorsimulator.testlibrary.RecordServer;
import org.openintents.sensorsimulator.testlibrary.Sensor;
import org.openintents.sensorsimulator.testlibrary.SensorTester;
import org.openintents.sensorsimulator.testlibrary.SequenceSaver;

/**
 * Console input for SensorSimulator
 * 
 * @author Qui Don Ho
 */
public class SensorConsole {

	private SensorTester mSimulator;
	private PrintStream mPrintStream;

	public SensorConsole(InputStream in, OutputStream out) {
		mScanner = new Scanner(in);
		mPrintStream = new PrintStream(out);
		mSimulator = new SensorTester();
	}

	/**
	 * Start reading input from specified InputStream
	 */
	public void start() {
		System.out
				.println("SensorSimulator started!" + nl +
						"Please type a command or 'help' for help!");

		boolean quit = false;
		do {
			// read cmd
			mPrintStream.print(">>");
			String cmdInput = mScanner.nextLine();
			String[] cmd = cmdInput.split("\\s+");

			// parse cmd
			if (cmd[0].equalsIgnoreCase("connect")
					|| cmd[0].equalsIgnoreCase("c"))
				doConnectCmd(cmd);
			else if (cmd[0].equalsIgnoreCase("shake"))
				doGestureCmd(cmd);
			else if (cmd[0].equalsIgnoreCase("set")) {
				doSetCmd(cmd);
			} else if (cmd[0].equalsIgnoreCase("rec")
					|| cmd[0].equalsIgnoreCase("r")
					|| cmd[0].equalsIgnoreCase("record"))
				doRecordCmd(cmd);
			else if (cmd[0].equalsIgnoreCase("load")
					|| cmd[0].equalsIgnoreCase("l"))
				doLoadCmd(cmd);
			else if (cmd[0].equalsIgnoreCase("disconnect")
					|| cmd[0].equalsIgnoreCase("d"))
				doDisconnectCmd();
			else if (cmd[0].equalsIgnoreCase("help")
					|| cmd[0].equalsIgnoreCase("h"))
				doHelpCmd();
			else if (cmd[0].equalsIgnoreCase("quit")
					|| cmd[0].equalsIgnoreCase("q"))
				quit = doQuitCmd();
			else {
				mPrintStream.println("Unknown command!");
			}
		} while (!quit);
	}

	// commands ///////////////////////////////////////////////////////

	private boolean doQuitCmd() {
		boolean quit;
		{
			if (mSimulator.isConnected())
				mSimulator.disconnect();
			mPrintStream.println(quitMsg[new Random(System
					.currentTimeMillis()).nextInt(quitMsg.length)]);
			quit = true;
		}
		return quit;
	}

	private void doHelpCmd() {
		{
			mPrintStream.println(helpMsg);
		}
	}

	private void doDisconnectCmd() {
		{
			mPrintStream.println("Disconnecting...");
			mSimulator.disconnect();
		}
	}

	private void doLoadCmd(String[] cmd) {
		{
			mSimulator.sendSequenceFile(cmd[1]);
		}
	}

	private void doRecordCmd(String[] cmd) {
		{
			RecordServer recServer;
			if (cmd.length == 2)
				recServer = new RecordServer(new SequenceSaver(cmd[1]));
			else
				recServer = new RecordServer(new SequenceSaver());
			recServer.start();
		}
	}

	private void doGestureCmd(String[] cmd) {
		if (cmd[0].equalsIgnoreCase("shake")) {
			if (mSimulator.isConnected()) {
				mPrintStream.println("shaking");
				mSimulator.shake();
				// st.sendSequenceFile("lesequence");
				// mDataSender.sendSensorEvents(mSequenceLoader
				// .loadFromFile("shortShake"));
			} else {
				mPrintStream.println("not connected!");
			}
		}
	}

	private void doConnectCmd(String[] cmd) {
		{
			mPrintStream.println("Connecting...");

			boolean success = false;
			if (cmd.length == 1) {
				// TODO implement connection to last ip address for
				// convenience
				// printStream.println("Please provide an IP address.");
				success = mSimulator.connect("192.168.2.101");
			} else {
				success = mSimulator.connect(cmd[1]);
			}
			mPrintStream.println(success ? "Done." : "Could not connect!");
		}
	}

	private void doSetCmd(String[] cmd) {
		try {
			if (cmd[1].equalsIgnoreCase("acc")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.ACCELEROMETER,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("mag")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.MAGNETIC_FIELD,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("ori")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.ORIENTATION,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("gyr")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.GYROSCOPE,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("lig")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.LIGHT,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("pre")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.PRESSURE,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("tem")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.TEMPERATURE,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("pro")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.PROXIMITY,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("lac")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.LINEAR_ACCELERATION,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("grv")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.GRAVITY,
						new float[] { a, b, c });
			} else if (cmd[1].equalsIgnoreCase("rot")) {
				Float a = Float.parseFloat(cmd[2]);
				Float b = Float.parseFloat(cmd[3]);
				Float c = Float.parseFloat(cmd[4]);
				mSimulator.setSensor(Sensor.Type.ROTATION,
						new float[] { a, b, c });
			}
		} catch (NumberFormatException e) {
			mPrintStream.println();
		} catch (ArrayIndexOutOfBoundsException e) {
			mPrintStream.println();
		}
	}

	// messages ////////////////////////////////////////////////////

	// newline
	String nl = System.getProperty("line.separator");

	// help
	String helpMsg = "SensorSimulator"
			+ nl
			+ "connect [ipaddress]: connect to a running app with SensorSimulator"
			+ nl
			+ "disconnect: disconnect, duh!"
			+ nl
			+ "load <file>: load sequence file in current directory and send to app"
			+ nl
			+ "set <sensorcode> <value>: set specified sensor value"
			+ nl
			+ "shake|...: send predefined sequence";

	// the most important thang in this program.
	String[] quitMsg = { "I wouldn't leave if i were you. work is much worse.",
			"You want to quit? Then, thou hast lost an eighth!",
			"Get outta here and go back to your boring programs.",
			"Don't quit now! We're  still spending your money!" };
	private Scanner mScanner;
}
