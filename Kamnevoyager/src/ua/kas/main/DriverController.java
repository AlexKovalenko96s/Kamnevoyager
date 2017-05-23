package ua.kas.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DriverController {

	private static ArrayList<Drivers> list_drivers = new ArrayList<>();

	private static ArrayList<Boolean> list_works = new ArrayList<>();

	public DriverController() {
		addDrivers();
	}

	private void addDrivers() {
		try {
			String id, name, surname, numberOfRights, categoryB, categoryC;

			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite::resource:ua/kas/main/kamnevoyager.db");
			Statement statement = connect.createStatement();
			String query = "SELECT * FROM drivers";
			ResultSet res = statement.executeQuery(query);
			while (res.next()) {
				id = res.getString("id");
				name = res.getString("name");
				surname = res.getString("surname");
				numberOfRights = res.getString("numberOfRights");
				categoryB = res.getString("categoryB");
				categoryC = res.getString("categoryC");

				list_drivers.add(new Drivers(id, name, surname, numberOfRights, categoryB, categoryC));
				list_works.add(false);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Boolean> getList_works() {
		return list_works;
	}

	public static void setList_works(ArrayList<Boolean> list_works) {
		DriverController.list_works = list_works;
	}

	public static ArrayList<Drivers> getList_drivers() {
		return list_drivers;
	}
}
