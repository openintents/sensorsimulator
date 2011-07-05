package org.openintents.tools.simulator;

import javax.swing.JTextPane;


public class JTextAreaWrap extends JTextPane {
	private static final long serialVersionUID = 2272337397818400768L;
	public JTextAreaWrap(String text) {
		super();
		setContentType("text/html");
		setEditable(false);
		setText(text);
	}
}
