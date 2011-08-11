package org.openintents.tools.simulator.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.model.telnet.Vector;

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

	private int mWidth;
	private int mHeight;

	public DevicePanel(int width, int height, StateModel model) {
		mHeight = height;
		mWidth = width;
		mModel = model;

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
				g2.setColor(Global.COLOR_ENABLE_BLUE);
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

	public void updateFromState() {
		// TODO Auto-generated method stub

	}
}
