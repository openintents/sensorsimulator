/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.util;

import javax.swing.JTextPane;

/**
 * This class is a JTextPane with html content.
 * 
 * @author ilarele
 * 
 */
public class HtmlTextPane extends JTextPane {
	private static final long serialVersionUID = 2272337397818400768L;

	public HtmlTextPane(String text) {
		super();
		setContentType("text/html");
		setEditable(false);
		setText(text);
	}
}
