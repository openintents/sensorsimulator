package org.openintents.tools.simulator.view.telnet.addons;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openintents.tools.simulator.model.telnet.addons.ReplayAddonModel;

public class ReplayAddonView extends JPanel {
	private static final long serialVersionUID = -1031252587774757353L;
	// Replay
	private JButton replayRecord;
	private JButton replayPlayback;

	public ReplayAddonView(final ReplayAddonModel replayAddonModel) {
		replayRecord = new JButton("Record");
		replayPlayback = new JButton("Playback");
	}

	public void fillPane(JPanel replayFieldPane) {
		replayFieldPane.add(replayRecord);
		replayFieldPane.add(replayPlayback);
	}

	public void setPlaybackText(String message) {
		replayPlayback.setText(message);
	}

	public JButton getRecordButton() {
		return replayRecord;
	}

	public JButton getPlaybackButton() {
		return replayPlayback;
	}
}
