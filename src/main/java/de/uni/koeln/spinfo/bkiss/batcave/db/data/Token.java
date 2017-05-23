package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.HashSet;
import java.util.Set;


public class Token implements Comparable<Token> {
	
	private String form;
	private int index;
	private Set<String> tags;
	private ScanPosition scanPosition;
	private boolean newLine;

	
	public Token(){
		this.tags = new HashSet<String>();
		this.newLine = false;
	}
	
	public Token(String form, int index, ScanPosition pos, Set<String> tags, boolean newLine){
		this();
		this.form = form;
		this.index = index;
		this.scanPosition = pos;
		this.tags = tags;
		this.newLine = newLine;
	}


	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}
	
	public void setNewLine(boolean newLine){
		this.newLine = newLine;
	}
	
	public boolean isNewLine(){
		return newLine;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}

	public ScanPosition getScanPosition() {
		return scanPosition;
	}

	public void setScanPosition(ScanPosition pos) {
		this.scanPosition = pos;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void addTag(String tag){
		tags.add(tag);
	}
	
	@Override
	public String toString() {
		return form + " " + index + " tags=" + tags + "";
	}
	
	/**
	 * Only compares index positions!
	 */
	@Override
	public int compareTo(Token o) {
		return index - o.getIndex();
	}

}
