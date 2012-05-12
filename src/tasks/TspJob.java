package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import tasks.TspTask.Tour;
import api.Job;
import api.Result;
import api.Space;

public class TspJob implements Job{

	private double [][] cities;
	
	public TspJob(double [][] cities){
		this.cities = cities;
	}
	
	@Override
	public void generateTasks(Space space) {
		ArrayList<Integer> partialTour = new ArrayList<Integer>();
		partialTour.add(0);
		TspTask rootTask = new TspTask(cities, null, null, partialTour, space);

		System.out.println("Calculating shortest tour for " + cities.length + " cities");
		setSharedObject(space, Double.MAX_VALUE);

		try {
			space.put(rootTask);
		} catch (RemoteException e) {
			System.err.println("Could not add task to space");
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tour collectResults(Space space) {
		try {
			System.out.println("Client is waiting for result");
			Result<Tour> r = (Result<Tour>) space.takeResult();
			return (Tour)r.getTaskReturnValue();
			
		} catch (RemoteException e) {
			System.err.println("Client failed to get result from space.");
			System.exit(0);
		}
		return null;
	
	}

	private void setSharedObject(Space space, double upperBound) {
		try {
			space.setShared(new TSPShared(upperBound));
		} catch (RemoteException e) {
			System.err.println("Could not set shared object");
			e.printStackTrace();
		}
	}
	
}
