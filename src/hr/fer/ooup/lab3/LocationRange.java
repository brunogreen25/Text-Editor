package hr.fer.ooup.lab3;

public class LocationRange {
	private Location beginLocation;
	private Location endLocation;
	
	public LocationRange(Location beginLocation, Location endLocation) {
		//ovdje se spremaju koodrinate, ne smije pratit referencu
		this.beginLocation = new Location(beginLocation.getX(), beginLocation.getY());
		this.endLocation = new Location(endLocation.getX(), endLocation.getY());
	}
	
	public LocationRange(LocationRange locationRange) {
		Location beginLocation = locationRange.getBeginLocation();
		Location endLocation = locationRange.getEndLocation();
		
		//ovdje se spremaju koodrinate, ne smije pratit referencu
		this.beginLocation = new Location(beginLocation.getX(), beginLocation.getY());
		this.endLocation = new Location(endLocation.getX(), endLocation.getY());
	}
	
	public void setBeginLocation(Location newBeginLocation) {
		this.beginLocation = new Location(newBeginLocation.getX(), newBeginLocation.getY());
	}
	
	public void setEndLocation(Location newEndLocation) {
		this.endLocation = new Location(newEndLocation.getX(), newEndLocation.getY());
	}
	
	public Location getBeginLocation() {
		return this.beginLocation;
	}
	
	public Location getEndLocation() {
		return this.endLocation;
	}
}
