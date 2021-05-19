package com.blockhead7360.timemanager;

import java.awt.Graphics;

import javax.swing.JPanel;

public class CircleIndicator extends JPanel {

	private static final long serialVersionUID = 1L;

	public void paint(Graphics g) {
		setSize(10, 10);
		g.fillOval(0, 0, 10, 10);
	}
	
}
