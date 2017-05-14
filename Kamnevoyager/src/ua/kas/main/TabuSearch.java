package ua.kas.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class TabuSearch {

	private BufferedReader reader;

	private ArrayList<Double> list_lenght = new ArrayList<>();
	private ArrayList<Integer> list_time = new ArrayList<>();
	private ArrayList<String> list_lenght_String = new ArrayList<>();
	private ArrayList<String> list_time_String = new ArrayList<>();

	public void start(LinkedList<Location> list_locations) {
		list_lenght_String.clear();
		list_time_String.clear();

		String line = "";

		int number_of_nodes = list_locations.size();

		TSPEnvironment tspEnvironment = new TSPEnvironment();

		tspEnvironment.distances = new double[number_of_nodes][number_of_nodes];

		int[] currSolution = new int[number_of_nodes + 1];

		for (int i = 0; i < currSolution.length - 1; i++) {
			currSolution[i] = i;
		}
		currSolution[number_of_nodes] = 0;

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
				list_time.add(
						Integer.parseInt(list_time_String.get(i).substring(0, list_time_String.get(i).indexOf(" "))));
		}

		System.out.println(list_lenght);
		System.out.println(list_time);

		int count = 0;

		for (int i = 0; i < list_locations.size(); i++) {
			for (int j = i + 1; j < list_locations.size(); j++) {
				tspEnvironment.distances[i][j] = list_lenght.get(count);
				tspEnvironment.distances[j][i] = list_lenght.get(count);
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

		System.out.println("Search done! \nBest Solution cost found = " + bestCost + "\nBest Solution :");
		printSolution(bestSol);

		for (int i = 0; i < bestSol.length; i++) {
			System.out.print(list_locations.get(bestSol[i]).getCity() + " "
					+ list_locations.get(bestSol[i]).getLocation() + " -> ");
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

	public static void printSolution(int[] solution) {
		for (int i = 0; i < solution.length; i++) {
			System.out.print(solution[i] + " ");
		}
		System.out.println();
	}
}