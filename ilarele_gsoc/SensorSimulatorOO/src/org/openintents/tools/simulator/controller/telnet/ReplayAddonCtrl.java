package org.openintents.tools.simulator.controller.telnet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.openintents.tools.simulator.model.telnet.addons.ReplayAddonModel;
import org.openintents.tools.simulator.view.telnet.addons.ReplayAddonView;

public class ReplayAddonCtrl {
	// Replay
	private ReplayAddonModel model;
	private ReplayAddonView view;

	public ReplayAddonCtrl(final ReplayAddonModel model, ReplayAddonView view,
			final JTextArea messagePanel) {
		this.model = model;
		this.view = view;

		final JButton replayRecord = view.getRecordButton();
		replayRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model.createFile()) {
					messagePanel.append("Recording Started\n");
					replayRecord.setText("Stop");

				} else {
					messagePanel.append("Recording Stopped\n");
					replayRecord.setText("Record");
				}
			}
		});

		final JButton replayPlayback = view.getPlaybackButton();
		replayPlayback.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model.openFile()) {
					if (model.isPlaying()) {
						messagePanel.append("Playing back\n");
						replayPlayback.setText("Stop");
					} else {
						messagePanel.append("Playback Stopped\n");
					}
				} else {
					messagePanel.append("Finish recording first\n");
				}
			}
		});
	}

	public void recordData(double readYaw, double readRoll, double readPitch) {
		model.recordData(readYaw, readRoll, readPitch);
	}

	public boolean playData() {
		return model.playData();
	}

	public void setPlaybackText(String message) {
		view.setPlaybackText(message);
	}

	public int getYaw() {
		return model.getYaw();
	}

	public int getRoll() {
		return model.getRoll();
	}

	public int getPitch() {
		return model.getPitch();
	}
}
