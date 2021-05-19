package com.blockhead7360.timemanager;

public class TimeSection {
	
	private long start;
	private long end;
	private String description;
	
	public TimeSection(long start, long end, String description) {
		this.start = start;
		this.end = end;
		this.description = description;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	} 
	
}
