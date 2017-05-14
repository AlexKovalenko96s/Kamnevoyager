package ua.kas.main;

public abstract class Location {

	protected String city;
	protected String location;
	protected String dimensions;
	protected String importance;

	protected double weight;
	protected double x;
	protected double y;

	public String getCity() {
		return city;
	}

	public String getLocation() {
		return location;
	}

	public String getDimensions() {
		return dimensions;
	}

	public String getImportance() {
		return importance;
	}

	public double getWeight() {
		return weight;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
