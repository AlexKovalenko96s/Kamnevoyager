package ua.kas.main;

public class Drivers extends Driver {

	public Drivers(String id, String name, String surname, String numberOfRights, String categoryB, String categoryC) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.numberOfRights = numberOfRights;
		this.categoryB = categoryB;
		this.categoryC = categoryC;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public String getSurname() {
		// TODO Auto-generated method stub
		return super.getSurname();
	}

	@Override
	public String getNumberOfRights() {
		// TODO Auto-generated method stub
		return super.getNumberOfRights();
	}

	@Override
	public String getCategoryB() {
		// TODO Auto-generated method stub
		return super.getCategoryB();
	}

	@Override
	public String getCategoryC() {
		// TODO Auto-generated method stub
		return super.getCategoryC();
	}
}
