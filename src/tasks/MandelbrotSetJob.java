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
public class MandelbrotSetJob implements Job{
	
	private final int splitFactor = 4;
	private final int decompositionCounter = 1;
	private int iterationLimit;
	
	public MandelbrotSetJob(int iterationLimit) {
		this.iterationLimit = iterationLimit;
	}
	
	@Override
	public void generateTasks(Space space) {
		try {
			Task task = (Task)new MandelbrotSetTask(-0.7510975859375, 0.1315680625, 0.01611, 2048, iterationLimit, decompositionCounter, space, null, null, splitFactor);
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
	public int[][] collectResults(Space space) {
		try {
			System.out.println("Client is waiting for result");
			Result r = space.takeResult();
			return (int[][])r.getTaskReturnValue();
			
		} catch (RemoteException e) {
			System.err.println("Client failed to get result from space.");
		}
		return null;
	}

}
