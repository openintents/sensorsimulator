/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 * 
 * Copyright (C) 2008-2011 OpenIntents.org
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Class that is used to limit our text field input.
 * 
 * @author Josip Balic
 */
public class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 1L;

	private int limit;
	// optional uppercase conversion
	private boolean toUppercase = false;

	/**
	 * Class constructor.
	 * 
	 * @param limit
	 *            , integer number of desired text field's input limit
	 */
	public JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	/**
	 * Constructor where we limit text field and generate limited text field
	 * with upper cases.
	 * 
	 * @param limit
	 *            , integer number of desired text field's input limit
	 * @param upper
	 *            , boolean, true if we want only upper cases in text field
	 */
	JTextFieldLimit(int limit, boolean upper) {
		super();
		this.limit = limit;
		toUppercase = upper;
	}

	/**
	 * Method used to insert input to limited text field.
	 */
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit) {
			if (toUppercase)
				str = str.toUpperCase();
			super.insertString(offset, str, attr);
		}
	}
}
