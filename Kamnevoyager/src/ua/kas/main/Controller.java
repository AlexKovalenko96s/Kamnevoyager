package ua.kas.main;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class Controller implements Initializable {

	@FXML
	ComboBox<String> comB_city;
	@FXML
	ComboBox<String> comB_location;
	@FXML
	ComboBox<String> comB_dimensions;
	@FXML
	ComboBox<String> comB_importance;

	@FXML
	ListView<String> lv_location;

	@FXML
	CheckBox cb_test;

	@FXML
	TextField tf_weight;

	private LinkedList<Location> list_locations = new LinkedList<>();

	private ObservableList<String> list_selectLocation = FXCollections.observableArrayList();
	private ObservableList<String> list_city = FXCollections.observableArrayList();
	private ObservableList<String> list_location = FXCollections.observableArrayList();
	private ObservableList<String> list_dimensions = FXCollections.observableArrayList("Small-sized", "Mid-size",
			"Large-size");
	private ObservableList<String> list_importance = FXCollections.observableArrayList("First degree", "Second degree",
			"Third degree");

	private TabuSearch tabuSerch = new TabuSearch();

	private boolean first = true;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (first) {
			tf_weight.setDisable(true);
			comB_dimensions.setDisable(true);
			comB_importance.setDisable(true);
		}

		lv_location.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite::resource:ua/kas/main/kamnevoyager.db");
			Statement statement = connect.createStatement();
			String query = "SELECT city FROM location GROUP BY city ORDER BY city";
			ResultSet res = statement.executeQuery(query);
			while (res.next()) {
				list_city.add(res.getString("city"));
			}
			comB_city.setItems(list_city);
			comB_dimensions.setItems(list_dimensions);
			comB_importance.setItems(list_importance);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectLocation() {
		list_location.clear();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite::resource:ua/kas/main/kamnevoyager.db");
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

	public void addLocation() throws ClassNotFoundException, SQLException {
		double weight = 0, x = 0, y = 0;

		if (!comB_city.getSelectionModel().isEmpty() && !comB_location.getSelectionModel().isEmpty()) {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite::resource:ua/kas/main/kamnevoyager.db");
			String query = "SELECT x, y FROM location WHERE city = ? and location = ?";
			PreparedStatement statement = connect.prepareStatement(query);
			statement.setString(1, comB_city.getSelectionModel().getSelectedItem());
			statement.setString(2, comB_location.getSelectionModel().getSelectedItem());
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				x = Double.parseDouble(res.getString("x"));
				y = Double.parseDouble(res.getString("y"));
			}

			if (first) {
				list_locations.add(new SetLocation(comB_city.getSelectionModel().getSelectedItem(),
						comB_location.getSelectionModel().getSelectedItem(), "null", "null", 0, x, y));

				list_selectLocation.add(comB_city.getSelectionModel().getSelectedItem() + " - "
						+ comB_location.getSelectionModel().getSelectedItem());

				tf_weight.setDisable(false);
				comB_dimensions.setDisable(false);
				comB_importance.setDisable(false);

				comB_location.getSelectionModel().clearSelection();
				comB_city.getSelectionModel().clearSelection();

				lv_location.setItems(list_selectLocation);

				first = false;
			} else if (!comB_dimensions.getSelectionModel().isEmpty() && !comB_importance.getSelectionModel().isEmpty()
					&& tf_weight.getText() != null) {
				try {
					weight = Double.parseDouble(tf_weight.getText());
				} catch (Exception e) {
				}

				if (weight > 0) {
					list_locations.add(new SetLocation(comB_city.getSelectionModel().getSelectedItem(),
							comB_location.getSelectionModel().getSelectedItem(),
							comB_dimensions.getSelectionModel().getSelectedItem(),
							comB_importance.getSelectionModel().getSelectedItem(), weight, x, y));

					list_selectLocation.add(comB_city.getSelectionModel().getSelectedItem() + " - "
							+ comB_location.getSelectionModel().getSelectedItem());

					comB_location.getSelectionModel().clearSelection();
					comB_city.getSelectionModel().clearSelection();
					comB_dimensions.getSelectionModel().clearSelection();
					comB_importance.getSelectionModel().clearSelection();
					tf_weight.setText(null);
					lv_location.setItems(list_selectLocation);
				} else
					JOptionPane.showMessageDialog(null, "Please, enter correct weight!");
			} else
				JOptionPane.showMessageDialog(null, "Please, fill in all fields!");
		}
	}

	public void works(ActionEvent event) {
		if (list_locations.size() > 1) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					tabuSerch.start(list_locations, cb_test.isSelected(), event,
							new ArrayList<String>(list_selectLocation));
				}
			});
			thread.start();
		} else {
			JOptionPane.showMessageDialog(null, "Please, enter more then one location!");
		}
	}
}
