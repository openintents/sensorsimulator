package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class SensorDocumentListener implements DocumentListener {
	
	// timer
	Timer mTimer;
	
	public SensorDocumentListener (ActionListener actionListener) {
		mTimer = new Timer(1000, actionListener);
		mTimer.setRepeats(false);
		mTimer.setCoalesce(true);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		mTimer.restart();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		mTimer.restart();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		mTimer.restart();
	}
}