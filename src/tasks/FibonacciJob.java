package tasks;

import java.rmi.RemoteException;
import java.util.UUID;

import api.Job;
import api.Result;
import api.Space;
import api.Task;

/**
 * Solves a mandelbrotSet problem by splitting the problem up
 * into pieces. (How many is given by the splitFactor and decompositionCounter parameters)
 * When the smallest pieces are solved they are composed together again into the original
 * picture.
 * 
 * @author christian
 *
 */
public class FibonacciJob implements Job{
	
	private final int splitFactor = 2;
	private final int decompositionCounter = 1;
	private int n;
	
	public FibonacciJob(int n) {
		this.n = n;
	}
	
	@Override
	public void generateTasks(Space space) {
		try {
			Task task = (Task)new FibonacciTask(n, space, null, null);
			space.put(task);
			System.out.println("Client is generating task..");
		} catch (Exception e) {
			System.err.println("Generating tasks failed for the MandelbrotSetJob");
			e.printStackTrace();
		}
		
		System.out.println("Generating task done..");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer collectResults(Space space) {
		try {
			System.out.println("Client is waiting for result");
			Result r = space.takeResult();
			return (Integer)r.getTaskReturnValue();
			
		} catch (RemoteException e) {
			System.err.println("Client failed to get result from space.");
		}
		return null;
	}

}
