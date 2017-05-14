package ua.kas.main;

public class TSPEnvironment { // Tabu Search Environment

	public double[][] distances;

	public double getObjectiveFunctionValue(int solution[]) {
		double cost = 0;

		for (int i = 0; i < solution.length - 1; i++) {
			cost += distances[solution[i]][solution[i + 1]];
		}
		return cost;
	}
}