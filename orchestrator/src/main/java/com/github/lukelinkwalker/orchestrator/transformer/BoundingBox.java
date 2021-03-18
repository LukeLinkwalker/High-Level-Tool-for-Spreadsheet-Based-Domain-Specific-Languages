package com.github.lukelinkwalker.orchestrator.transformer;

public class BoundingBox {
	private int x;
	private int y;
	private int width;
	private int height;
	
	public BoundingBox() {
		width = 1;
		height = 1;
	}
	
	public BoundingBox(int column, int row) {
		x = column;
		y = row;
		width = 1;
		height = 1;
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
	
	@Override
	public String toString() {
		return "BoundingBox [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}
}
