package ua.kas.main;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;

public class WayController {

	@FXML
	Label lb_name;
	@FXML
	Label lb_estimatedTime;
	@FXML
	Label lb_arrivalTime;
	@FXML
	Label lb_timeDifference;

	@FXML
	Button btn_broke;
	@FXML
	Button btn_repaired;
	@FXML
	Button btn_start;

	@FXML
	ListView<String> lv_way;

	private ObservableList<String> list_running = FXCollections.observableArrayList();

	private ArrayList<String> list_location = new ArrayList<>();

	private int[][] mass_Time;

	private double[][] mass_Way;

	private int[] bestSol;

	private double allTime = 0;
	private double allLenght = 0;
	private double lenght = 0;
	private double speed = 0;

	private int count = 0;

	private Timeline timeLine;
	private Timeline timeLineBroke;

	private boolean works = false;

	private int driver = 0;
	private int car = 0;

	public static Way way;

	public void initialize() {
		Way tempWay = way;

		list_location = tempWay.getList_location();
		mass_Time = tempWay.getMass_Time();
		mass_Way = tempWay.getMass_Way();
		bestSol = tempWay.getBestSol();
		driver = tempWay.getDriver();
		car = tempWay.getCar();

		btn_start.setDisable(false);
		btn_repaired.setDisable(true);
		btn_broke.setDisable(true);

		for (int i = 0; i < bestSol.length - 1; i++) {
			allTime += mass_Time[bestSol[i]][bestSol[i + 1]];
			allLenght += mass_Way[bestSol[i]][bestSol[i + 1]];
		}

		speed = allLenght / (allTime / 60);

		lb_estimatedTime.setText("Estimated time: " + new DecimalFormat("#0.00").format(allTime / 60.0) + " h.");
		lb_arrivalTime.setText("Arrival time: " + new DecimalFormat("#0.00").format(allTime / 60.0) + " h.");
	}

	public void works() {
		works = true;
		btn_start.setDisable(true);
		btn_repaired.setDisable(true);
		btn_broke.setDisable(false);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				timeLine = new Timeline(new KeyFrame(Duration.seconds(1), ae -> add()));
				timeLine.setCycleCount(Animation.INDEFINITE);
				timeLine.play();
			}
		});
		thread.start();
	}

	private void add() {
		if (lenght <= 0 && count + 1 < bestSol.length) {
			if (count == 0) {
				list_running.add(0, "START!!!");
			}
			list_running.add(0,
					"FROM: " + list_location.get(bestSol[count]) + " TO: " + list_location.get(bestSol[count + 1]));
			lenght = mass_Way[bestSol[count]][bestSol[count + 1]];
			count++;
		}

		if (lenght <= 0 && count + 1 == bestSol.length) {
			list_running.add(0, "FINISH!!!");
			ArrayList<Boolean> list_works = DriverController.getList_works();
			ArrayList<Boolean> list_worksCars = CarController.getList_works();

			list_works.set(driver, false);
			DriverController.setList_works(list_works);
			list_worksCars.set(car, false);
			DriverController.setList_works(list_worksCars);

			btn_broke.setDisable(true);
			timeLine.stop();
			works = false;
			return;
		} else {
			lenght -= 50.0;
			if (lenght <= 0)
				list_running.add(0, "ARRIVED TO: " + list_location.get(bestSol[count]));
			else
				list_running.add(0, new DecimalFormat("#0.0000").format(lenght));
		}
		lv_way.setItems(list_running);
	}

	public void broke() {
		if (works) {
			works = false;
			timeLine.pause();
			btn_repaired.setDisable(false);
			btn_broke.setDisable(true);
			list_running.add(0, "THE MACHINE IS BROKEN!!!");
			lv_way.setItems(list_running);

			timeLineBroke = new Timeline(new KeyFrame(Duration.millis(100), ae -> waitTime()));
			timeLineBroke.setCycleCount(Animation.INDEFINITE);
			timeLineBroke.play();
		}
	}

	public void repaired() {
		if (!works) {
			timeLineBroke.stop();
			works = true;
			timeLine.play();
			btn_repaired.setDisable(true);
			btn_broke.setDisable(false);
		}
	}

	public void waitTime() {
		allLenght += 0.1 * 50;
		allTime = allLenght / speed;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lb_arrivalTime.setText("Arrival time: " + new DecimalFormat("#0.00").format(allTime) + " h.");
				;
			}
		});
	}

	public static void setWay(Way way) {
		WayController.way = way;
	}
}
