package com.blockhead7360.timemanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class TimeManager {

	private List<TimeTitle> times;

	public static final String VERSION = "1.0";

	private static File folder;

	private static Font bold, plain, italic;

	private boolean running;
	private long start;

	public TimeManager() {

		times = new ArrayList<TimeTitle>();
		bold = new Font("arial", Font.BOLD, 14);
		plain = new Font("arial", Font.PLAIN, 14);
		italic = new Font("arial", Font.ITALIC, 12);
		running = false;
		start  = 0;

		folder = new File(new JFileChooser().getFileSystemView().getDefaultDirectory(), "Documents" + File.separator + "TimeManager");
		if (!folder.exists()) folder.mkdirs();

		for (File f : folder.listFiles()) {

			if (f.getName().endsWith(".timaf")) {

				times.add(new TimeTitle(f.getName().substring(0, f.getName().length() - 6)));

			}

		}

		JFrame frame = new JFrame("TimeManager " + VERSION);
		frame.setSize(new Dimension(515, 500));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowListener() {

			public void windowClosing(WindowEvent e) {
				
				if (running) {
					
					JOptionPane.showMessageDialog(frame, "You're in the middle of a time section. You have to end it before you can exit.", "Wait!", JOptionPane.INFORMATION_MESSAGE);
					return;
					
				}
				
				else {
					
					System.exit(0);
					
				}
				
			}

			public void windowActivated(WindowEvent arg0) { }
			public void windowClosed(WindowEvent arg0) { }
			public void windowDeactivated(WindowEvent arg0) { }
			public void windowDeiconified(WindowEvent arg0) { }
			public void windowIconified(WindowEvent arg0) { }
			public void windowOpened(WindowEvent arg0) { }
			
		});
		frame.setLayout(null);
		Container pane = new Container();

		JScrollPane dataView = new JScrollPane(data(-1, false));
		dataView.setBounds(new Rectangle(5, 100, 490, 310));
		Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Time Sections", TitledBorder.LEFT, TitledBorder.BELOW_TOP);
		dataView.setBorder(border);
		pane.add(dataView);

		JComboBox<String> title = new JComboBox<String>();

		JTextField commands = new JTextField();
		JButton run = new JButton("Run");

		JLabel smallText = new JLabel("", SwingConstants.RIGHT);
		smallText.setFont(new Font("arial", Font.PLAIN, 12));
		smallText.setBounds(new Rectangle(210, 5, 260, 25));
		pane.add(smallText);

		JLabel bigText = new JLabel("Idle", SwingConstants.RIGHT);
		bigText.setFont(new Font("arial", Font.BOLD, 24));
		bigText.setBounds(new Rectangle(20, 25, 460, 50));
		pane.add(bigText);
		
		CircleIndicator statusIndicator = new CircleIndicator();
		statusIndicator.setBounds(new Rectangle(480, 12, 10, 10));
		statusIndicator.setForeground(Color.red);
		pane.add(statusIndicator);


		JButton go = new JButton("Start");
		go.setEnabled(false);
		go.setBounds(new Rectangle(390, 70, 100, 30));
		go.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (running) {

					long end = System.currentTimeMillis();

					String desc = JOptionPane.showInputDialog(frame, "Enter a description for this time section.", "Ending time section", JOptionPane.INFORMATION_MESSAGE);

					if (desc == null) desc = "";

					running = false;
					commands.setEnabled(true);
					run.setEnabled(true);
					title.setEnabled(true);
					statusIndicator.setForeground(Color.red);
					go.setText("Start");

					smallText.setText("");
					bigText.setText("Idle");

					TimeSection section = new TimeSection(start, end, desc);

					boolean success = false;

					for (int i = 0; i < times.size(); i++) {

						if (times.get(i).getName().equals(title.getSelectedItem())) {

							TimeTitle t = times.get(i);
							t.addSection(section);

							int index = title.getSelectedIndex() - 1;
							dataView.getViewport().removeAll();
							dataView.getViewport().add(data(index, false));

							t.save();

							success = true;

						}

					}

					if (!success) {
						CrashReport.message("Failed to save time section (could not find correct time title to save to).");
						return;
					}

				} else {

					running = true;
					commands.setEnabled(false);
					run.setEnabled(false);
					title.setEnabled(false);
					statusIndicator.setForeground(Color.green);
					go.setText("End");

					start = System.currentTimeMillis();

					boolean success = false;

					for (int i = 0; i < times.size(); i++) {

						if (times.get(i).getName().equals(title.getSelectedItem())) {

							TimeTitle t = times.get(i);
							int id = t.getSections().size() + 1;
							smallText.setText(t.getName() + " #" + id);
							success = true;

						}

					}

					if (!success) {
						running = false;
						statusIndicator.setForeground(Color.red);
						bigText.setText("ERROR");
						CrashReport.message("Failed to start time section (could not find correct time title to save to).");
						return;
					}

					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");

					bigText.setText("Started at " + sdf.format(start));

				}

			}

		});
		pane.add(go);

		title.setBounds(new Rectangle(5, 5, 200, 30));
		title.addItem("Select a Time Title");
		for (TimeTitle t : times) {

			title.addItem(t.getName());

		}
		title.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int index = title.getSelectedIndex() - 1;
				dataView.getViewport().removeAll();
				dataView.getViewport().add(data(index, true));

				if (index >= 0) {
					go.setEnabled(true);
				} else {
					go.setEnabled(false);
				}

			}

		});
		pane.add(title);

		JLabel commandText = new JLabel(" ");
		commandText.setFont(new Font("arial", Font.ITALIC, 10));
		commandText.setBounds(new Rectangle(10, 415, 490, 15));
		pane.add(commandText);

		commands.setBounds(new Rectangle(5, 430, 400, 30));
		commands.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					run.getActionListeners()[0].actionPerformed(null);

				}

			}

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

		});
		pane.add(commands);

		run.setBounds(new Rectangle(410, 430, 85, 30));
		run.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String text = commands.getText();

				if (text.isEmpty()) return;

				commands.setText("");

				if (text.startsWith("help")) {

					JOptionPane.showMessageDialog(frame, "help - Show this page\n"
							+ "rate <amount> - See how much money you made per hour for the current time title\n"
							+ "addt <name...> - Add a time title\n"
							+ "delt <name...> - Delete a time title\n"
							+ "dels <id> - Delete a time section from the current time title\n"
							+ "cltxt - Clear the command response text", "TimeManager Command Help", JOptionPane.INFORMATION_MESSAGE);
					return;

				}
				
				else if (text.startsWith("total")) {
					
					if (title.getSelectedIndex() == 0) {

						commandText.setText("Unable to calculate rate: not viewing a valid time title");
						return;

					}
					
					commandText.setText("Total hours in this time title: " + totalHours(title.getSelectedIndex() - 1));
					return;
					
				}
				
				else if (text.startsWith("rate")) {
					
					if (title.getSelectedIndex() == 0) {

						commandText.setText("Unable to calculate rate: not viewing a valid time title");
						return;

					}
					
					text = text.replaceFirst("rate", "").trim();

					if (text.isEmpty()) {

						commandText.setText("Unable to calculate rate: invalid amount entered");
						return;

					}

					double amount = 0;
					try {

						amount = Double.parseDouble(text);

					} catch (NumberFormatException ex) {

						commandText.setText("Unable to calculate rate: " + text + " is not a valid amount");
						return;

					}
					
					double hours = totalHours(title.getSelectedIndex() - 1);
					
					double ans = amount / hours;
					
					commandText.setText("$" + amount + " / " + hours + " hours = $" + ans + "/hour");
					return;
					
				}

				else if (text.startsWith("addt")) {

					text = text.replaceFirst("addt", "").trim();

					if (text.isEmpty()) {

						commandText.setText("Unable to add time title: invalid name entered");
						return;

					}

					for (TimeTitle t : times) {

						if (t.getName().equalsIgnoreCase(text)) {

							commandText.setText("Unable to add time title: " + t.getName() + " already exists");
							return;

						}

					}

					TimeTitle t = new TimeTitle(text);
					times.add(t);
					title.addItem(t.getName());
					commandText.setText("Added time title: " + t.getName());
					return;

				}

				else if (text.startsWith("delt")) {

					text = text.replaceFirst("delt", "").trim();

					if (text.isEmpty()) {

						commandText.setText("Unable to delete time title: invalid name entered");
						return;

					}

					boolean confirm = false;

					if (text.endsWith(" -confirm")) {

						confirm = true;
						text = text.substring(0, text.length() - 9);

					}

					boolean exists = false;

					for (TimeTitle t : times) {

						if (t.getName().equalsIgnoreCase(text)) {

							exists = true;
							text = t.getName();
							break;

						}

					}

					if (!exists) {

						commandText.setText("Unable to delete time title: " + text + " does not exist");
						return;

					}

					if (!confirm) {

						commandText.setText("Are you sure you want to delete " + text + "? If so, run the command again with ' -confirm' at the end.");
						return;

					}

					for (int i = 0; i < times.size(); i++) {

						if (times.get(i).getName().equals(text)) {
							times.get(i).delete();
							times.remove(i);

							if (title.getSelectedItem().equals(text)) {

								title.setSelectedIndex(0);
								dataView.getViewport().removeAll();
								dataView.getViewport().add(data(-1, false));
								go.setEnabled(false);

							}

							title.removeItem(text);

						}

					}

					commandText.setText("Deleted time title: " + text);
					return;


				}

				else if (text.startsWith("dels")) {
					
					if (title.getSelectedIndex() == 0) {

						commandText.setText("Unable to delete time section: not viewing a valid time title");
						return;

					}
					
					text = text.replaceFirst("dels", "").trim();

					if (text.isEmpty()) {

						commandText.setText("Unable to delete time section: invalid ID entered");
						return;

					}

					int ts = 0;
					try {

						ts = Integer.parseInt(text);

					} catch (NumberFormatException ex) {

						commandText.setText("Unable to delete time section: " + text + " is not a valid ID");
						return;

					}

					for (TimeTitle t : times) {

						if (t.getName().equals(title.getSelectedItem())) {

							if (ts <= 0 || ts > t.getSections().size()) {

								commandText.setText("Unable to delete time section: " + text + " is not a valid ID");
								return;

							}

							t.removeSection(ts - 1);
							int index = title.getSelectedIndex() - 1;
							dataView.getViewport().removeAll();
							dataView.getViewport().add(data(index, false));
							t.save();
							
							commandText.setText("Deleted time section for " + t.getName() + ": " + ts);							
							
							return;

						}

					}

				}

				else if (text.startsWith("cltxt")) {

					commandText.setText("");
					return;

				}

				else {

					commandText.setText("Unknown command: " + text);

				}

			}

		});
		pane.add(run);

		frame.setContentPane(pane);

		frame.setVisible(true);

	}
	
	public double totalHours(int titleIndex) {
		
		TimeTitle title = times.get(titleIndex);
		
		long totalTimeMillis = 0;
		
		for (TimeSection ts : title.getSections()) {
			
			totalTimeMillis += ts.getEnd() - ts.getStart();
			
		}
		
		return totalTimeMillis / 3600000.0;
		
	}

	public JPanel data(int titleIndex, boolean load) {
		JPanel parent = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridBagLayout());
		parent.setBackground(Color.white);
		panel.setBackground(Color.white);
		parent.add(panel, BorderLayout.NORTH);


		if (titleIndex < 0) {
			JLabel label = new JLabel("Select a time title to view time sections.", 0);
			label.setFont(bold);
			panel.add(label);
			return panel;
		}

		TimeTitle title = times.get(titleIndex);
		if (load) title.load();
		List<TimeSection> sections = title.getSections();

		if (sections.isEmpty()) {
			JLabel label = new JLabel("No time sections exist for this time title.", 0);
			label.setFont(bold);
			panel.add(label);
			return panel;

		}

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 10;
		c.ipady = 10;

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a");

		for (int i = 0; i < sections.size(); i++) {

			c.gridy = i * 3;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;

			TimeSection t = sections.get(i);

			JLabel id = new JLabel(Integer.toString(i + 1));
			id.setFont(bold);
			c.gridx = 0;
			c.weightx = 1;
			panel.add(id, c);

			JLabel start = new JLabel(sdf.format(new Date(t.getStart())));
			start.setFont(plain);
			c.gridx = 1;
			c.weightx = 10;
			panel.add(start, c);

			JLabel end = new JLabel(sdf.format(new Date(t.getEnd())));
			end.setFont(plain);
			c.gridx = 2;
			panel.add(end, c);

			JLabel desc = new JLabel(t.getDescription());
			desc.setFont(italic);
			c.gridx = 1;
			c.gridy += 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			panel.add(desc, c);

			c.gridx = 0;
			c.gridy += 1;
			c.gridwidth = 3;
			c.weightx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			panel.add(new JSeparator(), c);

		}

		return parent;

	}

	public static File getFolder() {
		return folder;
	}


	public static void main(String[] args) {

		new TimeManager();

	}

}
