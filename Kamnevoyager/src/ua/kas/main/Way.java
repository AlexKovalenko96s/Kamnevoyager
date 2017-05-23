package ua.kas.main;

import java.util.ArrayList;

public class Way {

	private ArrayList<String> list_location = new ArrayList<>();

	private int[][] mass_Time;

	private double[][] mass_Way;

	private int[] bestSol;

	private int driver = 0;
	private int car = 0;

	public Way(ArrayList<String> list_location, int[][] mass_Time, double[][] mass_Way, int[] bestSol, int driver,
			int car) {
		this.list_location = list_location;
		this.mass_Time = mass_Time;
		this.mass_Way = mass_Way;
		this.bestSol = bestSol;
		this.driver = driver;
		this.car = car;
	}

	public ArrayList<String> getList_location() {
		return list_location;
	}

	public int[][] getMass_Time() {
		return mass_Time;
	}

	public double[][] getMass_Way() {
		return mass_Way;
	}

	public int[] getBestSol() {
		return bestSol;
	}

	public int getDriver() {
		return driver;
	}

	public int getCar() {
		return car;
	}
}
