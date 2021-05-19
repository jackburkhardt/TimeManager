package com.blockhead7360.timemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TimeTitle {
	
	private String name;
	private List<TimeSection> sections;
	
	public static final String SPLITTER = "_____";
	
	public TimeTitle(String name) {
		
		this.name = name;
		sections = new ArrayList<TimeSection>();
		
	}
	
	public void load() {
		
		File file = new File(TimeManager.getFolder(), name + ".timaf");
		if (!file.exists()) return;
		
		List<String> lines = new LinkedList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String str;
			while ((str = br.readLine()) != null) {
				
				lines.add(str);
				
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			CrashReport.error(e);
		}
		
		sections.clear();
		
		for (String line : lines) {
			
			String[] data = line.split("_____");
			long start = Long.parseLong(data[0]);
			long end = Long.parseLong(data[1]);
			String desc = data[2];
			
			sections.add(new TimeSection(start, end, desc));
			
		}
		
	}
	
	public void delete() {
		
		File file = new File(TimeManager.getFolder(), name + ".timaf");
		if (file.exists()) file.delete();
		
	}
	
	public void save() {
		
		File file = new File(TimeManager.getFolder(), name + ".timaf");
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			for (TimeSection section : sections) {
				
				bw.write(section.getStart() + SPLITTER
						+ section.getEnd() + SPLITTER
						+ section.getDescription() + "\n");
				
			}
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			CrashReport.error(e);
		}
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public void addSection(TimeSection t) {
		
		sections.add(t);
		
	}
	
	public void removeSection(int i) {
		
		sections.remove(i);
		
	}
	
	public List<TimeSection> getSections() {
		
		return sections;
		
	}
	
}
