package org.openintents.tools.simulator.view.gui.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

public class DefaultButton extends JButton {
	private static final long serialVersionUID = 6745918488665630583L;
	private BufferedImage buttonImage;
	GradientPaint LIGHT_GRADIENT = new GradientPaint(0, 0, Color.blue, 0, 200,
			Color.WHITE);

	public DefaultButton() {
		super();
		setOpaque(false);
	}

	public DefaultButton(String label) {
		super(label);
		// setOpaque(false);
		// setContentAreaFilled(false);
	}

	@Override
	public void paint(Graphics g) {
		// // Create an image for the button graphics if necessary
		// if (buttonImage == null || buttonImage.getWidth() != getWidth()
		// || buttonImage.getHeight() != getHeight()) {
		// buttonImage = getGraphicsConfiguration().createCompatibleImage(
		// getWidth(), getHeight());
		// }
		// Graphics gButton = buttonImage.getGraphics();
		// gButton.setClip(g.getClip());
		//
		// // Have the superclass render the button for us
		// super.paint(gButton);
		//
		// // Make the graphics object sent to this paint() method translucent
		// Graphics2D g2d = (Graphics2D) g;
		// AlphaComposite newComposite = AlphaComposite.getInstance(
		// AlphaComposite.SRC_OVER, .5f);
		// g2d.setComposite(newComposite);
		//
		// // Copy the button's image to the destination graphics, translucently
		// g2d.drawImage(buttonImage, 0, 0, null);
		//
		// Graphics2D g2 = (Graphics2D) g;
		//
		// GradientPaint p;
		// p = new GradientPaint(0, 0, Color.RED, 0, getHeight(),
		// Color.BLUE);
		//
		// // Paint oldPaint = g2.getPaint();
		// g2.setPaint(p);
		// g2.drawRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
		// g2.setPaint(oldPaint);

		// Graphics2D g2d = (Graphics2D) g;
		//
		// int w = getWidth();
		// int h = getHeight();
		//
		// RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w + 30,
		// h - 1, 20, 20);
		// // intersect this with the existing clip
		// g2d.clip(r2d);
		//
		// // fill the clipped area
		// g2d.setPaint(LIGHT_GRADIENT);
		// g2d.fillRect(0, 0, w, h);
		// // restore original clip
		//
		// // paint outer border
		// g2d.setPaint(OUTER);
		// g2d.drawRoundRect(0, 0, w + 30, h - 1, 20, 20);
		//
		// // paint inner border
		// g2d.setPaint(INNER);
		// g2d.drawRoundRect(1, 1, w + 30, h - 3, 18, 18);
		//
		// // paint right outside border
		// g2d.setPaint(p1);
		// g2d.drawLine(w - 1, 1, w - 1, h);
		//
		// // paint right inside border
		// g2d.setPaint(p2);
		// g2d.drawLine(w - 2, 2, w - 2, h - 1);
		//
		// // make it translucent
		// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// 0.1f));
		super.paintComponent(g);
	}
	// @Override
	// protected void paintBorder(Graphics g) {
	// // super.paintBorder(arg0);
	// g.setColor(Color.BLACK);
	// g.drawRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
	// }

}
