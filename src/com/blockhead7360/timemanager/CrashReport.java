package com.blockhead7360.timemanager;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class CrashReport {
	
	public static void message(String message) {
		
		JOptionPane.showMessageDialog(null, message, "An error occurred", JOptionPane.ERROR_MESSAGE);
		
	}
	
	public static void error(Exception ex) {
		
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		
		JOptionPane.showMessageDialog(null, pw, "An error occurred", JOptionPane.ERROR_MESSAGE);
		
	}
	
}
