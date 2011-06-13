package org.openintents.tools.simulator.view.sensor.sensors;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.PrintWriter;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public abstract class SensorView extends JPanel {
	private static final long serialVersionUID = 6732292499469735861L;
	private static final String EMPTY_LABEL = "                 -                ";
	private static Random rand = new Random();

	protected JButton mEnabled;

	// Simulation update
	protected JTextField mUpdateRatesText;
	protected JTextField mDefaultUpdateRateText;
	protected JTextField mCurrentUpdateRateText;

	/** Whether to form an average at each update */
	protected JCheckBox mUpdateAverage;

	// Random contribution
	protected JTextField mRandomText;

	protected JLabel mRefreshEmulatorLabel;

	// for measuring updates:
	protected int updateEmulatorCount;
	protected long updateEmulatorTime;

	// Settings
	private JTextField mRefreshCountText;

	protected SensorModel model;
	private JButton expandButton;
	private JPanel insidePanel;

	public SensorView(SensorModel model) {
		this.model = model;

		mEnabled = new JButton(model.getName());
		if (model.isEnabled()) {
			mEnabled.setBackground(Global.ENABLE);
			setVisible(true);
		} else {
			mEnabled.setBackground(Global.DISABLE);
			setVisible(false);
		}
		updateEmulatorCount = 0;
		updateEmulatorTime = System.currentTimeMillis();

		mCurrentUpdateRateText = new JTextField(5);

		mUpdateAverage = new JCheckBox("average");
		mUpdateAverage.setSelected(true);

		fillSensorPanel();
	}

	private void fillSensorPanel() {
		setLayout(new BorderLayout());
		JPanel expandPanel = new JPanel(new BorderLayout());
		expandButton = new JButton();
		expandButton.setBackground(new Color(0, 0, 0, 0));
		expandButton.setIcon(Global.EXPAND_MINUS);
		expandPanel.add(expandButton, BorderLayout.WEST);
		JLabel nameLabel = new JLabel(model.getName());
		nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		expandPanel.add(nameLabel, BorderLayout.CENTER);
		add(expandPanel, BorderLayout.NORTH);
		insidePanel = new JPanel();
		add(insidePanel, BorderLayout.CENTER);
		MatteBorder b = BorderFactory.createMatteBorder(5, 5, 5, 5,
				Global.BORDER_COLOR);
		insidePanel.setBorder(b);
		GridBagConstraints layout = new GridBagConstraints();

		// panel settings
		layout.gridx = 0;
		layout.gridy = 0;
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.anchor = GridBagConstraints.NORTHWEST;
		insidePanel.add(fillSensorSettingsPanel(), layout);

		// update rates
		layout.gridx = 0;
		layout.gridy = 1;
		insidePanel.add(fillSensorUpdatePanel(), layout);

		// random component and update simulation
		layout.gridx = 1;
		layout.gridy = 1;
		JPanel updateRandomPanel = new JPanel(new BorderLayout());
		updateRandomPanel.add(fillSensorRandomPanel(), BorderLayout.NORTH);
		updateRandomPanel.add(updateSimulationField(), BorderLayout.SOUTH);
		insidePanel.add(updateRandomPanel, layout);
	}

	public boolean isEnabled() {
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

	public void updateEmulatorRefresh() {
		updateEmulatorCount++;
		long maxcount = (long) getSafeDouble(mRefreshCountText);
		if (maxcount >= 0 && updateEmulatorCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorTime)
					/ ((double) maxcount);

			mRefreshEmulatorLabel.setText(Global.ONE_DECIMAL_FORMAT.format(ms)
					+ " ms");

			updateEmulatorCount = 0;
			updateEmulatorTime = newtime;
		}
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

//		if (!model.isUpdating())
//			return resultPanel;
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
		out.println("" + isEnabled());
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
		if (isEnabled()) {
			double updatesPerSecond = getCurrentUpdateRate();
			out.println("" + updatesPerSecond);
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void unsetSensorUpdateRate(PrintWriter out) {
		if (isEnabled()) {
			out.println("OK");
			mCurrentUpdateRateText.setText("" + getDefaultUpdateRate());
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

	public JButton getExpandButton() {
		return expandButton;
	}

	public void switchExpand() {
		if (insidePanel.isVisible()) {
			expandButton.setIcon(Global.EXPAND_PLUS);
			insidePanel.setVisible(false);
		} else {
			expandButton.setIcon(Global.EXPAND_MINUS);
			insidePanel.setVisible(true);
		}
	}

}
