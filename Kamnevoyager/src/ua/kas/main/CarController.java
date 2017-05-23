package ua.kas.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CarController {

	private static ArrayList<Cars> list_cars = new ArrayList<>();

	private static ArrayList<Boolean> list_works = new ArrayList<>();

	public CarController() {
		addCars();
	}

	private void addCars() {
		try {
			String id, type, mark, model, number;

			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite::resource:ua/kas/main/kamnevoyager.db");
			Statement statement = connect.createStatement();
			String query = "SELECT * FROM autoPark";
			ResultSet res = statement.executeQuery(query);
			while (res.next()) {
				id = res.getString("id");
				type = res.getString("type");
				mark = res.getString("mark");
				model = res.getString("model");
				number = res.getString("number");

				list_cars.add(new Cars(id, type, mark, model, number));
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
		CarController.list_works = list_works;
	}

	public static ArrayList<Cars> getList_cars() {
		return list_cars;
	}
}
