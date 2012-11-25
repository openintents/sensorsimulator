/*
 * Copyright (C) 2011 OpenIntents.org
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

package org.openintents.tools.simulator.view.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.SensorsScenarioModel;

/**
 * A complex play bar view, build on ScenarioSimulatorModel with
 * start/current/stop positions and drag support for them.
 * 
 * It computes the number of pixels per position, depending on
 * the number of states.
 * 
 * For synchronization it uses a ReentrantReadWriteLock to protect
 * the access to the pixelsPerPosition variable (many read accessed
 * and only a few for write).
 * @author ilarele
 *
 */
public class TimeScrollBar extends JPanel {
	private static final long serialVersionUID = -2606390269441038545L;

	public static final int TIME_SCROLL_HEIGHT = 70;
	public static final int TIME_SCROLL_W_MARGIN = 80;
	public static final int TIME_SCROLL_WIDTH = (int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT);
	public static final int TOLERANCE = 50;
	public static final int W_ARROW = 35;
	public static final int TIME_HEIGHT = 20;

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock rLock = rwl.readLock();
	private final Lock wLock = rwl.writeLock();

	private float mPxPerPos;

	private int wImage;
	private int hImage;

	private BufferedImage mCursorBufferedImage;
	private BufferedImage mStartBufferedImage;
	private BufferedImage mStopBufferedImage;

	private SensorsScenarioModel mModel;

	private int mDragStart = -1;
	private int mDragStop = -1;
	private int mDragCursor = -1;

	public TimeScrollBar(SensorsScenarioModel model) {
		mModel = model;
		setPreferredSize(new Dimension(TIME_SCROLL_WIDTH, TIME_SCROLL_HEIGHT));
		loadImages();
	}

	private void loadImages() {
		mStartBufferedImage = loadImage(Global.IMAGE_START);
		mStopBufferedImage = loadImage(Global.IMAGE_STOP);
		mCursorBufferedImage = loadImage(Global.IMAGE_CURSOR);
	}

	private BufferedImage loadImage(InputStream inputStream) {
		BufferedImage img;
		try {
			img = ImageIO.read(inputStream);
			wImage = img.getWidth(null);
			hImage = img.getHeight(null);
			BufferedImage bi = new BufferedImage(wImage, hImage,
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();
			g.drawImage(img, 0, 0, null);
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		SensorsScenarioModel model = mModel;
		int start = model.getStartPosition();
		int stop = model.getStopPosition();
		int max = model.getMaxPosition();
		int position = model.getCurrentPosition();

		Graphics2D g2d = (Graphics2D) g;

		// begin - start and stop - end intervals
		g2d.setColor(Color.BLACK);
		for (int i = 0; i <= max; i++) {
			int x = scale(i);
			g2d.drawLine(x, TIME_SCROLL_HEIGHT / 3, x, TIME_SCROLL_HEIGHT / 2);
		}

		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(scale(0), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(start), 10);
		g2d.fillRect(scale(stop), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(max - stop), 10);

		// start - position
		g2d.setColor(Global.COLOR_ENABLE_GREEN);
		g2d.fillRect(scale(start), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(position - start), 10);

		// position - stop
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(scale(position), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(stop - position), 10);

		// draw markers at mStart and at mStop
		int startX = mDragStart;
		if (mDragStart == -1) {
			startX = scale(start) - wImage;
		}

		int stopX = mDragStop;
		if (mDragStop == -1) {
			stopX = scale(stop);
		}
		g2d.drawImage(mStartBufferedImage, null, startX, TIME_SCROLL_HEIGHT / 2
				+ TIME_HEIGHT - hImage / 2);

		g2d.drawImage(mStopBufferedImage, null, stopX, TIME_SCROLL_HEIGHT / 2
				- TIME_HEIGHT - hImage / 2 - 5);

		g2d.setColor(Global.TEXT);
		g2d.drawString("Start=" + start, scale(start), TIME_SCROLL_HEIGHT / 2
				+ TIME_HEIGHT);
		g2d.drawString("Stop=" + stop, scale(stop) + W_ARROW,
				TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT);

		// draw cursor
		int cursorX = mDragCursor;
		if (mDragCursor == -1) {
			cursorX = (int) (scale(position) - 0.5f * wImage);
		}
		g2d.drawImage(mCursorBufferedImage, null, cursorX, TIME_SCROLL_HEIGHT
				/ 2 - hImage / 2);
		g2d.drawString(position + "/" + max, scale(position) - 10,
				TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT);

	}

	private int scale(int position) {
		rLock.lock();
		try {
			return TIME_SCROLL_W_MARGIN + (int) (mPxPerPos * position);
		} finally {
			rLock.unlock();
		}
	}

	private int scaleAsDifference(int position) {
		rLock.lock();
		try {
			return (int) (mPxPerPos * position);
		} finally {
			rLock.unlock();
		}
	}

	public void scaleNumberOfPixelsPerPosition() {
		int max = mModel.getStates().size() - 1;
		// System.out.println("scaleNumberOfPixelsPerPosition max=" + max);
		if (max != 0) {
			wLock.lock();
			try {
				mPxPerPos = (float) (TIME_SCROLL_WIDTH - 2 * TIME_SCROLL_W_MARGIN)
						/ max;
				// System.out.println(max + " " + mPxPerPos);
			} finally {
				wLock.unlock();
			}
		}
	}

	public void refreshView() {
		revalidate();
		repaint();
	}

	public float getPxPerPos() {
		rLock.lock();
		try {
			return mPxPerPos;
		} finally {
			rLock.unlock();
		}
	}

	public boolean isDragStop(MouseEvent e) {
		int x = e.getX();
		if (Math.abs(x - scale(mModel.getStopPosition()) - W_ARROW) < TOLERANCE) {
			if (e.getY() < TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT / 2)
				return true;
		}
		return false;
	}

	public boolean isDragStart(MouseEvent e) {
		int x = e.getX();
		if (Math.abs(x - scale(mModel.getStartPosition()) + W_ARROW) < TOLERANCE) {
			if (e.getY() > TIME_SCROLL_HEIGHT / 2 + TIME_HEIGHT / 2)
				return true;
		}
		return false;
	}

	public boolean isDragCursor(MouseEvent e) {
		int x = e.getX();
		if (Math.abs(x - scale(mModel.getCurrentPosition())) < TOLERANCE) {
			int y = e.getY();
			if (y > TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT / 2
					&& y < TIME_SCROLL_HEIGHT / 2 + TIME_HEIGHT / 2)
				return true;
		}
		return false;
	}

	public void setDragStartArrow(int x) {
		if (testInsideBar(x)) {
			mDragStart = x - wImage;
		}
	}

	public void setDragStopArrow(int x) {
		if (testInsideBar(x)) {
			mDragStop = x;
		}
	}

	public void setDragCursor(int x) {
		if (testInsideBar(x)) {
			mDragCursor = x;
		}
	}

	private boolean testInsideBar(int x) {
		return (x > TIME_SCROLL_W_MARGIN && x < TIME_SCROLL_WIDTH
				- TIME_SCROLL_W_MARGIN);
	}

	public void unsetDrag() {
		mDragStart = -1;
		mDragStop = -1;
		mDragCursor = -1;
	}
}