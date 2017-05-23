package ua.kas.main;

public abstract class Driver {

	protected String id;
	protected String name;
	protected String surname;
	protected String numberOfRights;
	protected String categoryB;
	protected String categoryC;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getNumberOfRights() {
		return numberOfRights;
	}

	public String getCategoryB() {
		return categoryB;
	}

	public String getCategoryC() {
		return categoryC;
	}
}
