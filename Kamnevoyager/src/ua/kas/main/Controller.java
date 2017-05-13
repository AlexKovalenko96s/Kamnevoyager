package ua.kas.main;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class Controller implements Initializable {

	@FXML
	ComboBox<String> comB_city;
	@FXML
	ComboBox<String> comB_location;
	@FXML
	ListView<String> lv_location;

	ObservableList<String> list_selectLocation = FXCollections.observableArrayList();
	ObservableList<String> list_city = FXCollections.observableArrayList();
	ObservableList<String> list_location = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lv_location.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:res\\kamnevoyager.db");
			Statement statement = connect.createStatement();
			String query = "SELECT city FROM location GROUP BY city ORDER BY city";
			ResultSet res = statement.executeQuery(query);
			while (res.next()) {
				list_city.add(res.getString("city"));
			}
			comB_city.setItems(list_city);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectLocation() {
		list_location.clear();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:res\\kamnevoyager.db");
			String query = "SELECT location FROM location WHERE city = ? GROUP BY location ORDER BY location";
			PreparedStatement statement = connect.prepareStatement(query);
			statement.setString(1, comB_city.getSelectionModel().getSelectedItem());
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				list_location.add(res.getString("location"));
			}
			comB_location.setItems(list_location);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void resetLocation() {
		comB_location.getSelectionModel().clearSelection();
	}

	public void deleteLocation() {
		ObservableList<String> select;
		select = lv_location.getSelectionModel().getSelectedItems();
		if (select.size() != 0) {
			do {
				list_selectLocation.remove(select.get(0));
			} while (select.size() == 1);
			lv_location.setItems(list_selectLocation);
		}
	}

	public void addLocation() {
		if (!comB_city.getSelectionModel().isEmpty() && !comB_location.getSelectionModel().isEmpty()) {
			list_selectLocation.add(comB_city.getSelectionModel().getSelectedItem() + " - "
					+ comB_location.getSelectionModel().getSelectedItem());
			lv_location.setItems(list_selectLocation);
		}
	}
}
