package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.io.Serializable;

public class ScanPosition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	public ScanPosition(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}
