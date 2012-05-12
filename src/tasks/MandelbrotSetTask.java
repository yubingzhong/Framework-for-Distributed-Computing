package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import api.ComputeThread;
import api.Computer;
import api.Computer2Space;
import api.Result;
import api.Shared;
import api.Space;
import api.Task;

public class MandelbrotSetTask extends AbstractTask implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8322067259082081900L;
	private int iterationLimit;
	private int n;
	private double edgeLength;
	private double lowerLeftX;
	private double lowerLeftY;
	
	private int decompositionCounter;
	private Space space;
	private int splitFactor;


	/**
	 * 
	 * This class has two purposes. Either it spawns new children tasks and a successortask waiting for the
	 * aforementioned children. Or, if it has reached the decomposition limit, it computes the subtask and returns the 
	 * result to its parent.
	 * 
	 * @param llx Lower left corner of a square in the complex plane
	 * @param lly Lower left corner of a square in the complex plane
	 * @param edgeLength Edge length of the square 
	 * @param n an int such that the square region is subdivided into n X n squares, each of which visualized by one pixel
	 * @param iterationLimit It defines when the representative point of a region is considered to be in the Mandelbrot set.
	 */
	public MandelbrotSetTask(double llx, double lly, double edgeLength, int n,
			int iterationLimit, int decompositionCounter, Space space, UUID taskID, UUID parentTaskID, int splitFactor) {
		
		super(taskID, parentTaskID);

		this.iterationLimit = iterationLimit;
		this.n = n;
		this.edgeLength = edgeLength;
		this.lowerLeftX = llx;
		this.lowerLeftY = lly;
		this.space = space;
		this.decompositionCounter = decompositionCounter;
		this.splitFactor = splitFactor;
	}
	
	@Override
	public void execute(Shared shared, ComputeThread computeThread) {
		
		// Decompose further
		if (this.decompositionCounter > 0) {
			
//			System.out.println("Decomposing task..");
			UUID [] childIDs = new UUID[splitFactor];
			
			createChildrenTasks(childIDs);
			createSuccessorTask(childIDs);
		}
		
		// Task decomposed. Compute result.
		else {
			
			Result r = (Result)new MandelbrotSetResult(this.compute(), this.getTaskId());
//			System.out.println("Base case, computing...done");
			
			try {
				((Computer2Space)space).putPartialResult(r);
			} catch (RemoteException e) {
				System.err.println("Result could not be returned to its parent");
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void createSuccessorTask(UUID[] childIDs) {
		try {
			MandelbrotSetSuccessorTask successorTask = new MandelbrotSetSuccessorTask(this.getTaskId(), this.getParentTaskId(), childIDs, splitFactor, space);
//			System.out.println("SuccessorTask made..");
			((Computer2Space)space).putTaskIntoWaitingQueue(successorTask);
			
		} catch (Exception e) {
			System.err.println("Could not put successortask in the space");
		}
	}

	private void createChildrenTasks(UUID[] childIDs) {
		for (int i = 0; i < splitFactor; i++) {
			try {
				Task childTask = new MandelbrotSetTask(this.lowerLeftX, this.lowerLeftY, this.edgeLength,
						this.n / (this.splitFactor / 2), this.iterationLimit, this.decompositionCounter - 1, this.space, null, this.getTaskId(), this.splitFactor);
				
				childIDs[i] = childTask.getTaskId();
				
//					System.out.println("Child task made, adding to queue...");
				((Computer2Space)space).put(childTask);
				
			} catch (RemoteException e) {
				System.err.println("Could not put childtask in the space");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Perform the actual computation of 
	 * the mandelbrotset problem.
	 * 
	 * @return a two dimensional array that is a part
	 * of the solution to the mandelbrotset problem.
	 */
	public int[][] compute() {
		
		int [][] count = new int[n][n];
		
		for (int i = 0; i < count.length; i++)
		for (int j = 0; j < count.length; j++) {
			
			double x0 = this.lowerLeftX + ((i * edgeLength) / n);
			double y0 = this.lowerLeftY + ((j * edgeLength) / n);
			
			double x = 0;
			double y = 0;
			double xtemp;
			int iteration = 0;
			
			while (x*x + y*y < 4 && iteration < this.iterationLimit) {
				xtemp = x*x - y*y + x0;
				y = 2*x*y + y0;
				x = xtemp;
				
				iteration++;
			}
			count[i][j] = iteration;
		}
		
		return count;
	}

}
