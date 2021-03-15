package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;

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

	public static BoundingBox merge(BoundingBox a, BoundingBox b) {
		ArrayList<Intersect> intersections = mergeCheck(a, b);
		
		// Constants
		int minX = Math.min(a.getX(), b.getX());
		int maxX = Math.max(a.getX() + a.getWidth(), b.getX() + b.getWidth());
		int minY = Math.min(a.getY(), b.getY());
		int maxY = Math.max(a.getY() + a.getHeight(), b.getY() + b.getHeight());
		
		if(intersections.size() == 1) {
			BoundingBox result = new BoundingBox();
			
			result.setX(Math.min(a.getX(), b.getX())); 
			result.setY(Math.min(a.getY(), b.getY())); 
			
			if(intersections.get(0).equals(Intersect.Horizontal)) {
				result.setWidth(a.getWidth() + b.getWidth());
				result.setHeight(a.getHeight() > b.getHeight() ? a.getHeight() : b.getHeight());
			} else if(intersections.get(0).equals(Intersect.Vertical)) {
				result.setHeight(a.getHeight() + b.getHeight());
				result.setWidth(a.getWidth() > b.getWidth() ? a.getWidth() : b.getWidth());
			} else if(intersections.get(0).equals(Intersect.ContainedA)) {
				result.setHeight(b.getHeight());
				result.setWidth(b.getWidth());
			} else if(intersections.get(0).equals(Intersect.ContainedB)) {
				result.setHeight(a.getHeight());
				result.setWidth(a.getWidth());
			}
			
			return result;
		}
		
		return null;
	}
	
	public static ArrayList<Intersect> mergeCheck(BoundingBox a, BoundingBox b) {
		ArrayList<Intersect> result = new ArrayList<>();
		
		// Constants
		int minX = Math.min(a.getX(), b.getX());
		int maxX = Math.max(a.getX() + a.getWidth(), b.getX() + b.getWidth());
		int minY = Math.min(a.getY(), b.getY());
		int maxY = Math.max(a.getY() + a.getHeight(), b.getY() + b.getHeight());
		
		// Checking Touching
		if(a.getX() == (b.getX() + b.getWidth()) || b.getX() == (a.getX() + a.getWidth())) {
			if(a.getY() <= b.getY() && (a.getY() + a.getHeight()) > b.getY() ||
			   a.getY() >= b.getY() && a.getY() < (b.getY() + b.getHeight())) 
			{
				result.add(Intersect.Horizontal);
			}
		}
		
		if(a.getY() == (b.getY() + b.getHeight()) || b.getY() == (a.getY() + a.getHeight())) {
			if(a.getX() <= b.getX() && (a.getX() + a.getWidth()) > b.getX() ||
			   a.getX() >= b.getX() && a.getX() < (b.getX() + b.getWidth())) 
			{
				result.add(Intersect.Vertical);
			}
		}
		
		// Checking Containing
		if(a.getX() >= b.getX() && (a.getX() + a.getWidth()) <= (b.getX() + b.getWidth()) &&
		   a.getY() >= b.getY() && (a.getY() + a.getHeight()) <= (b.getY() + b.getHeight())) 
		{
			result.add(Intersect.ContainedA);
		}
		
		if(b.getX() >= a.getX() && (b.getX() + b.getWidth()) <= (a.getX() + a.getWidth()) &&
		   b.getY() >= a.getY() && (b.getY() + b.getHeight()) <= (a.getY() + a.getHeight())) 
		{
			result.add(Intersect.ContainedB);
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "BoundingBox [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}
	
	public enum Intersect {
		Vertical, Horizontal, ContainedA, ContainedB, Contained
	}
}
