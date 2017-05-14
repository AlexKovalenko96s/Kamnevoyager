package ua.kas.main;

public class SetLocation extends Location {

	public SetLocation(String city, String location, String dimensions, String importance, double weight, double x,
			double y) {
		this.city = city;
		this.location = location;
		this.dimensions = dimensions;
		this.importance = importance;
		this.weight = weight;
		this.x = x;
		this.y = y;
	}

	@Override
	public String getCity() {
		// TODO Auto-generated method stub
		return super.getCity();
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return super.getLocation();
	}

	@Override
	public String getDimensions() {
		// TODO Auto-generated method stub
		return super.getDimensions();
	}

	@Override
	public String getImportance() {
		// TODO Auto-generated method stub
		return super.getImportance();
	}

	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return super.getWeight();
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return super.getX();
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return super.getY();
	}
}
