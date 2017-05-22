package ua.kas.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

public class TabuSearch {

	private BufferedReader reader;

	private ArrayList<Double> list_lenght = new ArrayList<>();

	private ArrayList<Integer> list_time = new ArrayList<>();
	private ArrayList<String> list_lenght_String = new ArrayList<>();

	private ArrayList<String> list_time_String = new ArrayList<>();

	@SuppressWarnings("unused")
	private ArrayList<String> list_location = new ArrayList<>();

	private int[][] mass_time;

	@SuppressWarnings("unused")
	private javafx.event.ActionEvent event;

	private String out = "";

	@SuppressWarnings("unused")
	private OpenModalWindow openModalWindow;

	public void start(LinkedList<Location> list_locations, boolean test, javafx.event.ActionEvent event,
			ArrayList<String> list_location) {
		this.list_location = list_location;
		this.event = event;
		list_lenght_String.clear();
		list_time_String.clear();
		list_lenght.clear();
		list_time.clear();
		out = "";

		String line = "";

		int number_of_nodes = list_locations.size();

		TSPEnvironment tspEnvironment = new TSPEnvironment();

		tspEnvironment.distances = new double[number_of_nodes][number_of_nodes];
		mass_time = new int[number_of_nodes][number_of_nodes];

		int[] currSolution = new int[number_of_nodes + 1];

		for (int i = 0; i < currSolution.length - 1; i++) {
			currSolution[i] = i;
		}
		currSolution[number_of_nodes] = 0;

		try {
			for (int i = 0; i < list_locations.size(); i++) {
				for (int j = i + 1; j < list_locations.size(); j++) {
					String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
							+ list_locations.get(i).getX() + "," + list_locations.get(i).getY() + "&destinations="
							+ list_locations.get(j).getX() + "," + list_locations.get(j).getY()
							+ "&key=AIzaSyBD_e4f7DYOB3q9s3MO13O0MZslGK6lD_k";

					URL url2;
					line = "";

					try {
						url2 = new URL(url);
						reader = new BufferedReader(new InputStreamReader(url2.openStream()));
						while (reader.ready()) {
							line += reader.readLine();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					list_lenght_String.add(line.substring(line.indexOf("\"text\" : \"") + 10, line.indexOf("\",")));
					line = line.substring(line.indexOf("\"value\""));
					list_time_String.add(line.substring(line.indexOf("\"text\" : \"") + 10, line.indexOf("\",")));
				}
			}

			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int hours = 0;
			int mins = 0;

			String time = "";

			for (int i = 0; i < list_lenght_String.size(); i++) {
				if (list_lenght_String.get(i).contains("mi"))
					list_lenght.add(Double.parseDouble(
							list_lenght_String.get(i).substring(0, list_lenght_String.get(i).indexOf(" "))) * 1.60934);
				else if (list_lenght_String.get(i).contains("f"))
					list_lenght.add(Double.parseDouble(
							list_lenght_String.get(i).substring(0, list_lenght_String.get(i).indexOf(" "))) * 0.0003);

				if (list_time_String.get(i).contains("hours")) {
					time = list_time_String.get(i);
					hours = Integer.parseInt(time.substring(0, time.indexOf(" ")));
					time = time.substring(time.indexOf(" ") + 1);
					mins = Integer.parseInt(time.substring(time.indexOf(" ") + 1, time.lastIndexOf(" ")));
					list_time.add(hours * 60 + mins);
				} else
					list_time.add(Integer
							.parseInt(list_time_String.get(i).substring(0, list_time_String.get(i).indexOf(" "))));
			}

			out += list_lenght + " - km." + "\n";
			out += list_time + " - min." + "\n";
			out += "-------------------------" + "\n";

			int count = 0;
			double distance = 0;

			for (int i = 0; i < list_locations.size(); i++) {
				for (int j = i + 1; j < list_locations.size(); j++) {
					distance = list_lenght.get(count);

					if (test && list_locations.get(j).getImportance().equals("First degree"))
						distance = distance - 500;
					else if (test && list_locations.get(j).getImportance().equals("Second degree"))
						distance = distance - 250;

					if (distance < 0)
						distance = 0;

					tspEnvironment.distances[i][j] = distance;
					tspEnvironment.distances[j][i] = distance;

					mass_time[i][j] = list_time.get(count);
					mass_time[j][i] = list_time.get(count);

					count++;
				}
			}

			int numberOfIterations = 10000;
			int tabuLength = number_of_nodes;
			TabuList tabuList = new TabuList(tabuLength);

			int[] bestSol = new int[currSolution.length];

			System.arraycopy(currSolution, 0, bestSol, 0, bestSol.length);
			double bestCost = tspEnvironment.getObjectiveFunctionValue(bestSol);

			for (int i = 0; i < numberOfIterations; i++) {
				currSolution = TabuSearch.getBestNeighbour(tabuList, tspEnvironment, currSolution);

				double currCost = tspEnvironment.getObjectiveFunctionValue(currSolution);

				if (currCost < bestCost) {
					System.arraycopy(currSolution, 0, bestSol, 0, bestSol.length);
					bestCost = currCost;
				}
			}

			out += "Search done!" + "\n" + "Best Solution : ";
			printSolution(bestSol);

			for (int i = 0; i < bestSol.length; i++) {
				for (int j = 0; j < i * 20; j++) {
					out += " ";
				}
				out += list_locations.get(bestSol[i]).getCity() + " " + list_locations.get(bestSol[i]).getLocation()
						+ " -> " + "\n";
			}

			out += "\n";

			double allTime = 0;
			double allLenght = 0;
			for (int i = 0; i < bestSol.length - 1; i++) {
				allTime += mass_time[bestSol[i]][bestSol[i + 1]];
				allLenght += tspEnvironment.distances[bestSol[i]][bestSol[i + 1]];
			}

			allTime = allTime / 60.0;

			out += "All lenght = " + allLenght + "km." + "\n";
			out += "All time = " + allTime + "h." + "\n" + "\n";

			double weight = 0d;
			int dimensions = 0;
			String outWeightAndDimension = "";

			for (int i = 0; i < list_locations.size(); i++) {
				weight += list_locations.get(i).getWeight();
				if (list_locations.get(i).getDimensions().equals("Small-sized"))
					dimensions += 1;
				else if (list_locations.get(i).getDimensions().equals("Mid-size"))
					dimensions += 3;
				else if (list_locations.get(i).getDimensions().equals("Large-size"))
					dimensions += 10;
			}

			if ((dimensions <= 5) || (weight <= 300))
				outWeightAndDimension = "Car type - sedan.";
			else if ((dimensions > 5 && dimensions <= 12) || (weight > 300 && weight <= 500))
				outWeightAndDimension = "Car type - minivan.";
			else if ((dimensions > 12 && dimensions <= 25) || (weight > 500 && weight <= 1500))
				outWeightAndDimension = "Car type - small van.";
			else if ((dimensions > 25 && dimensions <= 55) || (weight > 1500 && weight <= 3000))
				outWeightAndDimension = "Car type - van.";
			else if ((dimensions > 55 && dimensions <= 105) || (weight > 3000 && weight <= 6000))
				outWeightAndDimension = "Car type - truck.";
			else
				outWeightAndDimension = "Please, select another settings!";

			out += outWeightAndDimension + "\n";

			if (allTime <= 10) {
				out += "One shift." + "\n";
			} else if (allTime > 10 && allTime <= 20) {
				out += "Two shift." + "\n";
			} else if (allTime > 20) {
				out += "Three or more shift." + "\n";
			}

			openModalWindow = new OpenModalWindow(event);
			WayController.setMass_Time(mass_time);
			WayController.setMass_Way(tspEnvironment.distances);
			WayController.setBestSol(bestSol);
			WayController.setList_location(list_location);

			JOptionPane.showMessageDialog(null, out);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No internet connection!");
		}

	}

	public static int[] getBestNeighbour(TabuList tabuList, TSPEnvironment tspEnviromnet, int[] initSolution) {

		int[] bestSol = new int[initSolution.length];

		System.arraycopy(initSolution, 0, bestSol, 0, bestSol.length);
		double bestCost = tspEnviromnet.getObjectiveFunctionValue(initSolution);
		int city1 = 0;
		int city2 = 0;
		boolean firstNeighbor = true;

		for (int i = 1; i < bestSol.length - 1; i++) {
			for (int j = 2; j < bestSol.length - 1; j++) {
				if (i == j) {
					continue;
				}

				int[] newBestSol = new int[bestSol.length];

				System.arraycopy(bestSol, 0, newBestSol, 0, newBestSol.length);

				newBestSol = swapOperator(i, j, initSolution);

				double newBestCost = tspEnviromnet.getObjectiveFunctionValue(newBestSol);

				if ((newBestCost < bestCost || firstNeighbor) && tabuList.tabuList[i][j] == 0) {
					firstNeighbor = false;
					city1 = i;
					city2 = j;
					System.arraycopy(newBestSol, 0, bestSol, 0, newBestSol.length);
					bestCost = newBestCost;
				}

			}
		}

		if (city1 != 0) {
			tabuList.decrementTabu();
			tabuList.tabuMove(city1, city2);
		}
		return bestSol;
	}

	// swaps two cities
	public static int[] swapOperator(int city1, int city2, int[] solution) {
		int temp = solution[city1];
		solution[city1] = solution[city2];
		solution[city2] = temp;
		return solution;
	}

	public void printSolution(int[] solution) {
		for (int i = 0; i < solution.length; i++) {
			out += solution[i] + " ";
		}
		out += "\n";
	}
}
