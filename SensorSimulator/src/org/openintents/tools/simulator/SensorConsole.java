package org.openintents.tools.simulator;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import com.openintents.sensorsimulator.testlibrary.Sensor;
import com.openintents.sensorsimulator.testlibrary.SensorTester;

/**
 * Console input for SensorSimulator
 * 
 * @author Qui Don Ho
 */
public class SensorConsole {

	InputStream mIn;
	OutputStream mOut;

	public SensorConsole(InputStream in, OutputStream out) {
		mIn = in;
		mOut = out;
	}

	/**
	 * Start reading input from specified InputStream
	 */
	public void start() {
		System.out
				.println("SensorSimulator started!\nPlease type a command. 'h' or 'help' for help!");

		Scanner scanner = new Scanner(mIn);
		PrintStream printStream = new PrintStream(mOut);

		// init
		SensorTester st = new SensorTester();

		boolean quit = false;
		do {
			// read cmd
			String cmdInput = scanner.nextLine();
			String[] cmd = cmdInput.split("\\s+");

			// parse cmd
			if (cmd[0].equalsIgnoreCase("connect")
					|| cmd[0].equalsIgnoreCase("c")) {
				printStream.println("Connecting...");

				boolean success = false;
				if (cmd.length == 1) {
					success = st.connect("192.168.2.102");
				} else {
					success = st.connect(cmd[1]);
				}
				System.out.println(success ? "Done." : "Could not connect!");
			} else if (cmd[0].equalsIgnoreCase("shake")) {
				if (st.isConnected()) {
					printStream.println("shaking");
					st.shake();
					// st.sendSequenceFile("lesequence");
					// mDataSender.sendSensorEvents(mSequenceLoader
					// .loadFromFile("shortShake"));
				} else {
					printStream.println("not connected!");
				}
			} else if (cmd[0].equalsIgnoreCase("set")) {
				if (cmd[1].equalsIgnoreCase("acc")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.ACCELEROMETER,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("mag")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.MAGNETIC_FIELD,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("ori")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.ORIENTATION,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("gyr")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.GYROSCOPE,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("lig")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.LIGHT,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("pre")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.PRESSURE,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("tem")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.TEMPERATURE,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("pro")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.PROXIMITY,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("lac")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.LINEAR_ACCELERATION,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("grv")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.GRAVITY,
							new float[] { a, b, c });
				} else if (cmd[1].equalsIgnoreCase("rot")) {
					Float a = Float.parseFloat(cmd[2]);
					Float b = Float.parseFloat(cmd[3]);
					Float c = Float.parseFloat(cmd[4]);
					st.setSensor(Sensor.Type.ROTATION,
							new float[] { a, b, c });
				}
			} else if (cmd[0].equalsIgnoreCase("load")) {
			} else if (cmd[0].equalsIgnoreCase("disconnect")) {
				printStream.println("Disconnecting...");
				st.disconnect();
			} else if (cmd[0].equalsIgnoreCase("help")
					|| cmd[0].equalsIgnoreCase("h")) {
				printStream.println(helpMsg);
			} else if (cmd[0].equalsIgnoreCase("quit")
					|| cmd[0].equalsIgnoreCase("q")) {
				if (st.isConnected())
					st.disconnect();
				printStream.println(quitMsg[new Random(System
						.currentTimeMillis()).nextInt(quitMsg.length)]);
				quit = true;
			} else {
				printStream.println("Unknown command!");
			}
		} while (!quit);

	}

	// help
	String helpMsg = "Hellpppppp meeeee!!!!";

	// the most important thang in this program.
	String[] quitMsg = { "I wouldn't leave if i were you. work is much worse.",
			"You want to quit? Then, thou hast lost an eighth!",
			"Get outta here and go back to your boring programs.",
			"Don't quit now! We're  still spending your money!" };
}
