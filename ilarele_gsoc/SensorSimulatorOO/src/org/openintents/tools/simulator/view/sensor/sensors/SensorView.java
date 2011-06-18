package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.help.HelpWindow;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public abstract class SensorView extends JScrollPane {
	private static final long serialVersionUID = 6732292499469735861L;
	private static final String EMPTY_LABEL = "                 -                ";
	private static Random rand = new Random();

	protected JButton mEnabled;
	private HelpWindow helpWindow;

	// Simulation update
	protected JTextField mUpdateRatesText;
	protected JTextField mDefaultUpdateRateText;
	protected JTextField mCurrentUpdateRateText;

	/** Whether to form an average at each update */
	protected JCheckBox mUpdateAverage;

	// Random contribution
	protected JTextField mRandomText;

	protected JLabel mRefreshEmulatorLabel;

	protected SensorModel model;
	private JPanel insidePanel;
	private JButton helpBtn;

	public SensorView(SensorModel model) {
		super();
		this.model = model;
		setPreferredSize(new Dimension(
				(int) (Global.WIDTH * Global.SENSOR_SPLIT_RIGHT),
				(int) (Global.HEIGHT * Global.SENSOR_SPLIT_UP)));
		mEnabled = new JButton(model.getName());
		if (model.isEnabled())
			mEnabled.setBackground(Global.ENABLE);
		else
			mEnabled.setBackground(Global.DISABLE);

		mCurrentUpdateRateText = new JTextField(5);

		mUpdateAverage = new JCheckBox("average");
		mUpdateAverage.setSelected(true);

		helpWindow = new HelpWindow(this);

		fillSensorPanel();
	}

	private void fillSensorPanel() {
		SpringLayout layout = new SpringLayout();
		insidePanel = new JPanel(layout);
		getViewport().add(insidePanel);
		// help label
		helpBtn = new JButton(Global.ICON_HELP);
		helpBtn.setOpaque(false);
		helpBtn.setContentAreaFilled(false);
		helpBtn.setBorderPainted(false);
		insidePanel.add(helpBtn);

		// update rates
		JPanel sensorUpdate = fillSensorUpdatePanel();
		insidePanel.add(sensorUpdate);

		// random component and update simulation
		JPanel updateRandomPanel = new JPanel(new BorderLayout());
		updateRandomPanel.add(fillSensorRandomPanel(), BorderLayout.NORTH);
		updateRandomPanel.add(updateSimulationField(), BorderLayout.SOUTH);
		insidePanel.add(updateRandomPanel);

		// panel settings
		JPanel sensorSettings = fillSensorSettingsPanel();
		insidePanel.add(sensorSettings);

		// sensorUpdate
		layout.putConstraint(SpringLayout.NORTH, sensorUpdate, 10,
				SpringLayout.NORTH, insidePanel);
		layout.putConstraint(SpringLayout.WEST, sensorUpdate, 10,
				SpringLayout.WEST, insidePanel);

		// updateRandomPanel
		layout.putConstraint(SpringLayout.NORTH, updateRandomPanel, 10,
				SpringLayout.SOUTH, sensorUpdate);
		layout.putConstraint(SpringLayout.WEST, updateRandomPanel, 10,
				SpringLayout.WEST, insidePanel);

		// helpBtn
		layout.putConstraint(SpringLayout.NORTH, helpBtn, 10,
				SpringLayout.SOUTH, updateRandomPanel);
		layout.putConstraint(SpringLayout.WEST, insidePanel, 10,
				SpringLayout.WEST, helpBtn);

		// sensorSettings
		layout.putConstraint(SpringLayout.SOUTH, insidePanel, 10,
				SpringLayout.SOUTH, sensorSettings);
		layout.putConstraint(SpringLayout.EAST, sensorSettings, -10,
				SpringLayout.EAST, insidePanel);

		Dimension size1 = sensorSettings.getPreferredSize();
		Dimension size2 = sensorUpdate.getPreferredSize();
		setMinimumSize(new Dimension(40 + size1.width + size2.width, 100));

		Dimension size3 = helpBtn.getPreferredSize();
		Dimension size4 = updateRandomPanel.getPreferredSize();
		sensorSettings
				.setPreferredSize(new Dimension(size1.width, Math.max(
						size1.height, 20 + size2.height + size3.height
								+ size4.height)));
	}

	public boolean isSensorEnabled() {
		return mEnabled.isSelected();
	}

	public void setEnabled(boolean enable) {
		mEnabled.setSelected(enable);
		mRefreshEmulatorLabel.setText("-");
	}

	public double[] getUpdateRates() {
		return getSafeDoubleList(mUpdateRatesText);
	}

	public double getDefaultUpdateRate() {
		return getSafeDouble(mDefaultUpdateRateText);
	}

	public double getCurrentUpdateRate() {
		return getSafeDouble(mCurrentUpdateRateText, 0);
	}

	public boolean updateAverage() {
		return mUpdateAverage.isSelected();
	}

	public double getRandom() {
		return getSafeDouble(mRandomText);
	}

	/**
	 * get a random number in the range -random to +random
	 * 
	 * @param random
	 *            range of random number
	 * @return random number
	 */
	public static double getRandom(double random) {
		double val;
		val = rand.nextDouble();
		return (2 * val - 1) * random;
	}

	public double getSafeDouble(JTextField textfield) {
		return getSafeDouble(textfield, 0);
	}

	/**
	 * Safely retries the double value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return double value.
	 */
	public double getSafeDouble(JTextField textfield, double defaultValue) {
		double value = defaultValue;

		try {
			value = Double.parseDouble(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	/**
	 * Safely retries the a list of double values of a text field. If the list
	 * contains errors, null is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @return list double[] with values or null.
	 */
	public static double[] getSafeDoubleList(JTextField textfield) {
		double[] valuelist = null;
		try {
			String t = textfield.getText();
			// Now we have to split this into pieces
			String[] tlist = t.split(",");
			int len = tlist.length;
			if (len > 0) {
				valuelist = new double[len];
				for (int i = 0; i < len; i++) {
					valuelist[i] = Double.parseDouble(tlist[i]);
				}
			} else {
				valuelist = null;
			}
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			valuelist = null;
			textfield.setBackground(Color.RED);
		}
		return valuelist;
	}

	// public abstract String toString(DecimalFormat mf);

	protected double getRandomFromText() {
		return getSafeDouble(mRandomText);
	}

	public JButton getEnabled() {
		return mEnabled;
	}

	public JPanel fillSensorUpdatePanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		if (!model.isUpdating())
			return resultPanel;
		GridBagConstraints layout = new GridBagConstraints();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Update rate"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		JLabel nameLabel = new JLabel("Update rates: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy = 0;
		resultPanel.add(nameLabel, layout);

		mUpdateRatesText = new JTextField(10);
		layout.gridx = 1;
		resultPanel.add(mUpdateRatesText, layout);

		nameLabel = new JLabel("1/s", JLabel.LEFT);
		layout.gridx = 2;
		resultPanel.add(nameLabel, layout);

		nameLabel = new JLabel("Default rate: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		resultPanel.add(nameLabel, layout);

		mDefaultUpdateRateText = new JTextField(5);
		layout.gridx = 1;
		resultPanel.add(mDefaultUpdateRateText, layout);

		nameLabel = new JLabel("1/s", JLabel.LEFT);
		layout.gridx = 2;
		resultPanel.add(nameLabel, layout);

		nameLabel = new JLabel("Current rate: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		resultPanel.add(nameLabel, layout);

		layout.gridx = 1;
		resultPanel.add(mCurrentUpdateRateText, layout);

		nameLabel = new JLabel("1/s", JLabel.LEFT);
		layout.gridx = 2;
		resultPanel.add(nameLabel, layout);

		layout.gridwidth = 3;
		layout.gridx = 0;
		layout.gridy++;

		resultPanel.add(mUpdateAverage, layout);

		layout.gridy++;
		resultPanel.add(new JSeparator(SwingConstants.HORIZONTAL), layout);

		StringBuffer ratesStr = new StringBuffer();
		for (double rate : model.getUpdateRates()) {
			ratesStr.append(rate + ", ");
		}
		if (ratesStr.length() > 0)
			mUpdateRatesText.setText(ratesStr.substring(0,
					ratesStr.length() - 2).toString());
		mDefaultUpdateRateText.setText("" + model.getDefaultUpdateRate());
		mCurrentUpdateRateText.setText("" + model.getCurrentUpdateRate());
		return resultPanel;
	}

	public JPanel updateSimulationField() {
		JPanel resultPanel = new JPanel();

		// if (!model.isUpdating())
		// return resultPanel;
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Simulation update"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		GridBagConstraints layout = new GridBagConstraints();
		mRefreshEmulatorLabel = new JLabel(EMPTY_LABEL);
		layout.gridx = 0;
		resultPanel.add(mRefreshEmulatorLabel, layout);
		return resultPanel;
	}

	public abstract JPanel fillSensorSettingsPanel();

	public JPanel fillSensorRandomPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Random"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		GridBagConstraints layout = new GridBagConstraints();
		mRandomText = new JTextField(5);
		mRandomText.setText("" + model.getRandom());
		layout.gridx = 0;
		resultPanel.add(mRandomText, layout);

		JLabel label = new JLabel(" " + model.getSI(), JLabel.LEFT);
		layout.gridx = 1;
		resultPanel.add(label, layout);
		return resultPanel;
	}

	public void enableSensor(PrintWriter out, boolean enable) {
		out.println("" + isSensorEnabled());
		setEnabled(enable);
	}

	public void getSensorUpdateRates(PrintWriter out) {
		double[] updatesList = getUpdateRates();
		if (updatesList == null || updatesList.length < 1) {
			out.println("0");
		} else {
			int len = updatesList.length;
			out.println("" + len);
			for (int i = 0; i < len; i++) {
				out.println("" + updatesList[i]);
			}
		}
	}

	public void getSensorUpdateRate(PrintWriter out) {
		if (isSensorEnabled()) {
			double updatesPerSecond = getCurrentUpdateRate();
			out.println("" + updatesPerSecond);
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void unsetSensorUpdateRate(PrintWriter out) {
		if (isSensorEnabled()) {
			out.println("OK");
			mCurrentUpdateRateText.setText("" + model.getDefaultUpdateRate());
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void addEnable(JPanel enabledSensorsPane) {
		enabledSensorsPane.add(mEnabled);
	}

	public JCheckBox getUpdateAvg() {
		return mUpdateAverage;
	}

	public void setRefreshEmulatorTime(String message) {
		mRefreshEmulatorLabel.setText(message);
	}

	public SensorModel getModel() {
		return model;
	}

	public JButton getHelpButton() {
		return helpBtn;
	}

	public HelpWindow getHelpWindow() {
		return helpWindow;
	}

	public JPanel getHelpPanel() {
		JPanel panel = new JPanel();
		JButton button = new JButton("Get me online info");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					if (desktop.isSupported(Desktop.Action.BROWSE)) {
						URI uri;
						try {
							uri = new URI(Global.HELP_ONE_SENSOR_URL
									+ model.getTypeConstant());
							desktop.browse(uri);
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		panel.add(BorderLayout.CENTER, button);
		return panel;
	}
}
