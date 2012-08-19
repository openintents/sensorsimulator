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

package org.openintents.tools.simulator.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.model.telnet.Vector;

/**
 * Draws the device based on orientation.
 * @author Peli
 *
 */
public class DevicePanel extends JPanel {
	private static final long serialVersionUID = 1641228393704045445L;
	// Mobile size
	private final double sx = 15; // size x
	private final double sy = 40; // size y
	private final double sz = 5; // size z

	// Display size
	private final double dx = 12; // size x
	private final double dy1 = 33; // size y
	private final double dy2 = -15;

	/** Contains the grid model of the phone. */
	private final double[][] phone = {
			// bottom shape
			{ sx, sy, -sz }, { -sx, sy, -sz },
			{ -sx, sy, -sz },
			{ -sx, -sy, -sz },
			{ -sx, -sy, -sz },
			{ sx, -sy, -sz },
			{ sx, -sy, -sz },
			{ sx, sy, -sz },
			// top shape
			{ sx, sy, sz }, { -sx, sy, sz }, { -sx, sy, sz }, { -sx, -sy, sz },
			{ -sx, -sy, sz },
			{ sx, -sy, sz },
			{ sx, -sy, sz },
			{ sx, sy, sz },
			// connectint top and bottom
			{ sx, sy, -sz }, { sx, sy, sz }, { -sx, sy, -sz }, { -sx, sy, sz },
			{ -sx, -sy, -sz }, { -sx, -sy, sz },
			{ sx, -sy, -sz },
			{ sx, -sy, sz },
			// display
			{ dx, dy1, sz }, { -dx, dy1, sz }, { -dx, dy1, sz },
			{ -dx, dy2, sz }, { -dx, dy2, sz }, { dx, dy2, sz },
			{ dx, dy2, sz }, { dx, dy1, sz }, };

	private StateModel mModel;
	private SensorsScenarioView mScenarioView;

	private int mWidth;
	private int mHeight;

	public DevicePanel(int width, int height, StateModel model,
			SensorsScenarioView scenarioView) {
		mHeight = height;
		mWidth = width;
		mModel = model;
		mScenarioView = scenarioView;

	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		float[] orientation = mModel.getOrientation();
		double centerx = mWidth / 2;
		double centery = mHeight / 2;
		double centerz = Global.DEVICE_CENTER_Z;

		Graphics2D g2 = (Graphics2D) graphics;
		// draw Line2D.Double
		for (int i = 0; i < phone.length; i += 2) {
			if (i == 0) {
				// container panel and StateViewSmall
				Container parentView = getParent().getParent();
				if (parentView instanceof StateViewSmall) {
					if (!mScenarioView
							.isCurrentState((StateViewSmall) parentView)) {
						g2.setColor(Global.COLOR_ENABLE_BLUE);
					} else {
						g2.setColor(Color.RED);
					}
				} else {
					g2.setColor(Global.COLOR_ENABLE_BLUE);
				}
			}
			if (i == 24) {
				g2.setColor(Global.COLOR_ENABLE_GREEN);
			}

			Vector v1 = new Vector(phone[i]);
			Vector v2 = new Vector(phone[i + 1]);
			v1.rollpitchyaw(orientation[2], orientation[1], orientation[0]);
			v2.rollpitchyaw(orientation[2], orientation[1], orientation[0]);
			g2.draw(new Line2D.Double(centerx + v1.x * centerz
					/ (centerz - v1.y), centery - v1.z * centerz
					/ (centerz - v1.y), centerx + v2.x * centerz
					/ (centerz - v2.y), centery - v2.z * centerz
					/ (centerz - v2.y)));
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(mWidth, mHeight);
	}

}
