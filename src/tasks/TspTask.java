package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import api.ComputeThread;
import api.Computer2Space;
import api.Shared;
import api.Space;
import api.SuccessorTask;
import api.Task;

public class TspTask extends AbstractTask implements Task {

	private static final long serialVersionUID = -3801238715490874867L;
	private double[][] cities;
	private ArrayList<Integer> partialTour;
	private ArrayList<Integer> citiesToVisit;
	private Space space;
	
	/**
	 * This task takes a set of permutations that represent an order in which to visit cities, and 
	 * computes the permutation that gives the shortest tour visiting all cities once.
	 * 
	 * @param cities Array that contains cities and their locations
	 */
	public TspTask(double [][] cities, UUID taskID, UUID parentID, ArrayList<Integer> partialTour, Space space){
		super(taskID, parentID);
		
		this.cities = cities;
		this.space = space;
		this.partialTour = partialTour;
		this.citiesToVisit = calculateCitiesToVisit(cities, partialTour);
		
	}
	
	private boolean canPrune(double estimatedCost, TSPShared shared) {
		if (estimatedCost >= shared.get())
			return true;
		
		return false;
	}
	
	private void prune() {
		ArrayList<Integer> dummyTour = new ArrayList<Integer>();
		for (int i = 0; i < cities.length; i++) {dummyTour.add(i);}
		putPartialResult(new TspResult(new Tour(dummyTour, Double.MAX_VALUE), getTaskId()));
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute(Shared s, ComputeThread computeThread) {
		
		double estimatedCost = calculateTourCost(partialTour, cities, true) + computeMST(citiesToVisit, cities, this.partialTour);
		
		// Prune branch if possible
		if(canPrune(estimatedCost, (TSPShared)s)) {
			prune();
			return;
		}
		
		// Further decompose task
		if (citiesToVisit.size() > cities.length - 3) {
			
			//System.out.println("Decomposing task..");
			UUID [] argumentsNeeded = new UUID[citiesToVisit.size()];
			ArrayList<Task> childrenTasks = new ArrayList<Task>();
			ArrayList<Integer> childTasksPartialTour = null;
			int i = 0;
			Task childTask = null;
			
			// Generate children tasks
			for (Integer nextCity : citiesToVisit) {
				
				childTasksPartialTour = new ArrayList<Integer>(partialTour);
				childTasksPartialTour.add(nextCity);
				
				childTask = new TspTask(cities, null, getTaskId(), childTasksPartialTour, space);
				argumentsNeeded[i] = childTask.getTaskId();
				childrenTasks.add(childTask);
				i++;
			}
			
			// Cache task
			computeThread.cacheTask(childrenTasks.get(0));
			
			for (int j = 1; j < childrenTasks.size(); j++) {
				try {((Computer2Space)space).put(childrenTasks.get(j));}
				catch (RemoteException e) { e.printStackTrace();}
			}
			
			SuccessorTask<Tour> successorTask = new TSPSuccessorTask(this.getTaskId(), this.getParentTaskId(), argumentsNeeded, this.space);
			
			try {
				((Computer2Space)this.space).putTaskIntoWaitingQueue(successorTask);
			} catch (RemoteException e) {
				System.err.println("Could not put SuccessorTask back into the space");
				e.printStackTrace();
			}
		}
		
		// Compute task (because it is atomic, at leaf level of the tree) 
		else {
			
			ArrayList<Integer> aTour;
			PermutationGenerator pg = new PermutationGenerator(citiesToVisit.size());
			double minimalTourDistance = Double.MAX_VALUE;
			ArrayList<Integer> minimalTour = new ArrayList<Integer>();

			for (int i = 0; i < cities.length; i++) { minimalTour.add(i);}
			
			double tourDistance;
			int[] permutation;
			
//			System.out.println("Number of permutations in subtree: " + pg.getTotal());
			
			while (pg.hasMore()) {
				
				if(canPrune(estimatedCost, (TSPShared)computeThread.getShared())) {
					prune();
					return;
				}
				
				aTour = new ArrayList<Integer>(partialTour);
				permutation = pg.getNext();
				
				for (Integer nextCity : permutation) { aTour.add(citiesToVisit.get(nextCity)); }
				tourDistance = calculateTourCost(aTour, cities, false);
				
//				System.out.println(new Tour(aTour, tourDistance));
				
				if (tourDistance < minimalTourDistance) {
					minimalTour = (ArrayList<Integer>) aTour.clone();
					minimalTourDistance = tourDistance;
					this.setShared(minimalTourDistance, computeThread);
				}
			}
			
//			System.out.println("Shortest: " + new Tour(minimalTour, minimalTourDistance));
//			System.out.println("");
//			System.out.println(minimalTour.toString());
			
			putPartialResult(new TspResult(new Tour(minimalTour, minimalTourDistance), this.getTaskId()));
		}
	}
	
	private void putPartialResult(TspResult result) {
		try {
			((Computer2Space)this.space).putPartialResult(result);
		} catch (RemoteException e) {
			System.err.println("Could not put partial result into space");
			e.printStackTrace();
		}
	}
	
	private void setShared(double costOfPartialTour, ComputeThread computeThread) {
		computeThread.setShared(new TSPShared(costOfPartialTour));
	}

	/**
	 * Helper method for figuring out which cities to visit next
	 */
	private ArrayList<Integer> calculateCitiesToVisit(double [][] cities, ArrayList<Integer> partialTour) {
		
		ArrayList<Integer> citiesToVisit = new ArrayList<Integer>();
		
		for (int i = 0; i < cities.length; i++) {
			if (partialTour.indexOf((Integer)i) < 0 ) {
				citiesToVisit.add(i);
			}
		}
		return citiesToVisit;
	}
	
	/**
	 * Helper method to calculate the cost of the 
	 * partial tour represented by this task
	 * @param tour List of integers representing a tour of nodes
	 * @param cities The coordinates of all cities
	 * @param path Flag that indicates wether to compute a tour or a path
	 * @return Returns the distance of the tour (or path)
	 */
	private double calculateTourCost(ArrayList<Integer> tour, double [][] cities, boolean path) {
		double cost = 0;

		if (tour.size() > 1) {
			for (int i = 1; i < tour.size(); i++) {
				cost += euclidianDistance(cities[tour.get(i-1)][0], cities[tour.get(i-1)][1],
										  cities[tour.get(i)][0], cities[tour.get(i)][1]);
			}
			if (!path)
				cost += euclidianDistance(cities[tour.get(tour.size()-1)][0], cities[tour.get(tour.size()-1)][1], cities[tour.get(0)][0], cities[tour.get(0)][1]);
		}
		return cost;
	}
	
	/**
	 * Computes euclidian distance between two points
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return distance between the two points
	 */
	private double euclidianDistance(double x0, double y0, double x1, double y1) {
		return Math.sqrt(Math.pow(x1 - x0, 2.0) +Math.pow(y1 - y0, 2.0));
	}
	

	/**
	 * Computes the cost of a minimum spanning tree using the Euclidian distance as
	 * the cost of the edges between the cities
	 * 
	 * @param citiesToVisit The nodes in the spanning tree
	 * @param cities the coordinates of the cities
	 * @return Cost of the minimum spanning tree
	 */
	private double computeMST(ArrayList<Integer> citiesToGo, double[][]cities, ArrayList<Integer> partialTour) {
		
		ArrayList<Integer> citiesToVisit = new ArrayList<Integer>(citiesToGo);
		ArrayList<Integer> mst = new ArrayList<Integer>(partialTour);
		
		double mstCost = 0;
		int lastAddedNode = 0;
		
		while (citiesToVisit.size() > 0) {
			
			int closestCity = 0;
			double distanceToClosestCity = Double.MAX_VALUE;
			double calculatedDistance;
			
			for (Integer cityInMST : mst) {
				for (Integer cityToVisit : citiesToVisit) {
					calculatedDistance = euclidianDistance(cities[cityInMST][0], cities[cityInMST][1], cities[cityToVisit][0], cities[cityToVisit][1]);
					
					if (calculatedDistance <= distanceToClosestCity) {
						distanceToClosestCity = calculatedDistance;
						closestCity = cityToVisit;

					}
				}
			}
			
			mst.add(citiesToVisit.remove(citiesToVisit.indexOf(closestCity)));
			mstCost += distanceToClosestCity;
			lastAddedNode = closestCity;
		}
		
		
		//
		// Find the least cost edge of the last node and add it
		// to the mstCost to get a better lower bound
		// TODO: Fix this. It is WRONG
		
		double leastCostEdge = Double.MAX_VALUE;
		double distance = 0;
		
		for (Integer node : mst) {
			if (node != lastAddedNode) {
				distance = euclidianDistance(cities[lastAddedNode][0], cities[lastAddedNode][1], cities[node][0], cities[node][1]);
				if (distance < leastCostEdge) leastCostEdge = distance;
			}
		}

		return mstCost;
	}
	
	public String toString() {
		return "Task: " + this.getTaskId().toString();
	}
	
	
	/**
	 * Data structure to hold a tour and
	 * its distance.
	 */
	public class Tour implements Serializable {

		private static final long serialVersionUID = -7447428998511485462L;
		private double distance;
		private ArrayList<Integer> shortestPath;
		
		public Tour(ArrayList<Integer> shortestPath, double distance) {
			this.shortestPath = shortestPath;
			this.distance = distance;
		}
		
		public double getDistance() {
			return this.distance;
		}
		
		public ArrayList<Integer> getPath() {
			return this.shortestPath;
		}
		
		public String toString() {
			return "Tour: " + this.shortestPath.toString() + "\nDistance: " + this.distance;
		}
	}
}



