package org.openintents.tools.simulator.view.gui.util;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JButton;
import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public class SensorButton extends JButton {
	private static final long serialVersionUID = 5228625229002535576L;
	private boolean mSelected;
	private SensorView mSensor;
	private Dimension mSize;
	private Stroke mSimpleStroke;
	private Stroke mBoldedStroke;

	public SensorButton(String label, SensorView sensor) {
		super(label);
		mSensor = sensor;

		Dimension size = getPreferredSize();
		size.width = Math.max(Global.W_BUTTONS, size.width);
		size.height = Global.H_BUTTONS;
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		mSize = size;
		setBorderPainted(false);
		mBoldedStroke = new BasicStroke(3);
		mSimpleStroke = new BasicStroke(2);
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (mSelected) {
			g2d.setColor(Global.COLOR_ENABLE_BLUE);
			g2d.setStroke(mBoldedStroke);
		} else {
			g2d.setColor(Global.BUTTON);
			g2d.setStroke(mSimpleStroke);
		}
		Dimension size = mSize;
		int i = 2;
		g2d.drawRoundRect(i, i, size.width - 2 * i, size.height - 2 * i, 10, 10);
	}

	public SensorView getSensor() {
		return mSensor;
	}

	public void setSelectedSensor(boolean selected) {
		mSelected = selected;
	}

}
