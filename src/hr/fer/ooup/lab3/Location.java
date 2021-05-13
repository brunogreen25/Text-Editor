package hr.fer.ooup.lab3;

import java.awt.*;

public class Location {
	
	//Point je super za lokaciju jer ima vec dosta gotovih metoda
	private Point location;
	
	public Location(int x, int y) {
		this.location = new Point(x, y);
	}
	
	public Location(Location location) {
		this.location = new Point(location.getX(), location.getY());
	}
	
	public Point getLocation() {
		return this.location;
	}
	
	public void setLocation(Point newLocation) {
		this.location = newLocation;
	}
	public void setLocation(int x, int y) {
		this.location.x = x;
		this.location.y = y;
	}
	
	public void setX(int x) {
		this.location.x = x;
	}
	
	public void setY(int y) {
		this.location.y = y;
	}
	
	public int getX() {
		return this.location.x;
	}
	
	public int getY() {
		return this.location.y;
	}
}
