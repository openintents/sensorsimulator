package org.openintents.tools.simulator.view.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;

public class TimeScrollBar extends JPanel {
	private static final long serialVersionUID = -2606390269441038545L;

	public static final int TIME_SCROLL_HEIGHT = 70;
	public static final int TIME_SCROLL_W_MARGIN = 80;
	public static final int TIME_SCROLL_WIDTH = (int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT);

	private static final int TOLERANCE = 50;

	public static final int W_ARROW = 25;

	private static final int TIME_HEIGHT = 20;

	private Object mLock = new Object();

	private int mEnd; // maximum for stop position
	private int mPosition;

	private int mStart = 0;
	private int mStop = 0;

	private float mPxPerPos;

	public TimeScrollBar() {
		setPreferredSize(new Dimension(TIME_SCROLL_WIDTH, TIME_SCROLL_HEIGHT));
	}

	private void scaleNumberOfPixelsPerPosition() {
		synchronized (mLock) {
			if (mEnd != 0) {
				mPxPerPos = (float) (TIME_SCROLL_WIDTH - 2 * TIME_SCROLL_W_MARGIN)
						/ mEnd;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// begin - start and stop - end intervals
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(scale(0), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(mStart), 10);
		g2d.fillRect(scale(mStop), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(mEnd - mStop), 10);

		// start - position
		g2d.setColor(Global.COLOR_ENABLE_GREEN);
		g2d.fillRect(scale(mStart), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(mPosition - mStart), 10);

		// position - stop
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(scale(mPosition), TIME_SCROLL_HEIGHT / 2 - 5,
				scaleAsDifference(mStop - mPosition), 10);

		// draw markers at mStart and at mStop
		drawImage(g2d, Global.IMAGE_START, mStart, TIME_SCROLL_HEIGHT / 2
				+ TIME_HEIGHT, -1);
		drawImage(g2d, Global.IMAGE_STOP, mStop, TIME_SCROLL_HEIGHT / 2
				- TIME_HEIGHT, 0);
		g2d.setColor(Global.TEXT);
		g2d.drawString("Start=" + mStart, scale(mStart), TIME_SCROLL_HEIGHT / 2
				+ TIME_HEIGHT);
		g2d.drawString("Stop=" + mStop, scale(mStop) - W_ARROW,
				TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT);

		// draw cursor
		drawImage(g2d, Global.IMAGE_CURSOR, mPosition, TIME_SCROLL_HEIGHT / 2,
				-0.5f);
		g2d.drawString("Position=" + mPosition, scale(mPosition) - 30,
				TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT);

	}

	private int scale(int position) {
		synchronized (mLock) {
			return TIME_SCROLL_W_MARGIN + (int) (mPxPerPos * position);
		}
	}

	private int scaleAsDifference(int position) {
		synchronized (mLock) {
			return (int) (mPxPerPos * position);
		}
	}

	private void drawImage(Graphics2D g2d, File imageFile, int posX, int posY,
			float dir) {
		BufferedImage img;
		try {
			img = ImageIO.read(imageFile);
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			BufferedImage bi = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();
			g.drawImage(img, 0, 0, null);
			g2d.drawImage(bi, null, (int) (scale(posX) + dir * w), posY - h / 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setEndPosition(int position) {
		synchronized (mLock) {
			if (position != mEnd && -1 < position) {
				mEnd = position;
				setStopState(position, false);
				scaleNumberOfPixelsPerPosition();
				refreshView();
			}
		}
	}

	public void setCurrentPosition(int position) {
		synchronized (mLock) {
			if (position != mPosition && position <= mEnd && position <= mStop) {
				mPosition = position;
				if (mStart > position) {
					mStart = position;
				}
				if (mStop < position) {
					mStop = position;
				}
			}
			refreshView();
		}
	}

	public void setStartState(int value) {
		synchronized (mLock) {
			if (mStart != value) {
				mStart = value;
				if (mPosition < mStart) {
					mPosition = mStart;
				}
				if (mStart > mStop) {
					mStart = mStop;
				}
				refreshView();
			}
		}
	}

	public void setStopState(int value, boolean refresh) {
		synchronized (mLock) {
			if (mStop != value && value <= mEnd) {
				mStop = value;
				if (mPosition > mStop) {
					mPosition = mStop;
				}
				if (mStart > mStop) {
					mStart = mStop;
				}
				if (refresh) {
					refreshView();
				}
			}
		}
	}

	public boolean isDragStop(MouseEvent e) {
		int x = e.getX();
		synchronized (mLock) {
			if (Math.abs(x - scale(mStop) - W_ARROW) < TOLERANCE) {
				if (e.getY() < TIME_SCROLL_HEIGHT / 2 - TIME_HEIGHT / 2)
					return true;
			}
			return false;
		}
	}

	public boolean isDragStart(MouseEvent e) {
		int x = e.getX();
		synchronized (mLock) {
			if (Math.abs(x - scale(mStart) + W_ARROW) < TOLERANCE) {
				if (e.getY() > TIME_SCROLL_HEIGHT / 2 + TIME_HEIGHT / 2)
					return true;
			}
			return false;
		}
	}

	public void setStopStateAbsolute(int px) {
		synchronized (mLock) {
			setStopState((int) ((px - TIME_SCROLL_W_MARGIN) / mPxPerPos), true);
		}
	}

	public void setStartStateAbsolute(int px) {
		synchronized (mLock) {
			setStartState((int) ((px - TIME_SCROLL_W_MARGIN) / mPxPerPos));
		}
	}

	private void refreshView() {
		revalidate();
		repaint();
	}

	public void removePosition(int removedPosition) {
		synchronized (mLock) {
			setEndPosition(mEnd - 1);
		}
	}

	public void incrementNoStates() {
		synchronized (mLock) {
			setEndPosition(mEnd + 1);
		}
	}

	public float getPxPerPos() {
		synchronized (mLock) {
			return mPxPerPos;
		}
	}

	public int getStartState() {
		synchronized (mLock) {
			return mStart;
		}
	}

	public int getStopState() {
		synchronized (mLock) {
			return mStop;
		}
	}

	public void reset() {
		synchronized (mLock) {
			mStart = mStop = mPosition = mEnd = 0;
		}
	}
}
