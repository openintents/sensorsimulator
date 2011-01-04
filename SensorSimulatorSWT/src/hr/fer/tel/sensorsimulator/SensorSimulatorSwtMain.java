/**
 *
 */
package hr.fer.tel.sensorsimulator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.openintents.tools.sensorsimulator.swt.SensorSimulatorSwt;

/**
 *
 * @author Lee Sanghoon
 */
public class SensorSimulatorSwtMain {

	public void run() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Sensor Simulator");
		shell.setSize(800, 600);
		createContents(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		System.exit(0);
	}

	/**
	 * @param shell
	 */
	private void createContents(Shell shell) {
		shell.setLayout(new FillLayout());

		new SensorSimulatorSwt(shell);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SensorSimulatorSwtMain().run();
	}

}
