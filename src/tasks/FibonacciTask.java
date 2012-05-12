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

public class FibonacciTask extends AbstractTask implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8322067259082081900L;
	private int n;
	private Space space;
	private final int splitFactor = 2;

	public FibonacciTask(int n, Space space, UUID taskID, UUID parentTaskID) {
		
		super(taskID, parentTaskID);
		this.n = n;
		this.space = space;
	}
	
	@Override
	public void execute(Shared shared, ComputeThread computeThread) {
		
		// Decompose further
		if (this.n >= 2) {
			
			UUID [] childIDs = new UUID[splitFactor];
			
			createChildrenTasks(childIDs);
			createSuccessorTask(childIDs);
		}
		
		// Task decomposed. Compute result.
		else {
			
			Result r = (Result)new FibonacciResult(this.compute(n), this.getTaskId());
			
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
			FibonacciSuccessorTask successorTask = new FibonacciSuccessorTask(this.getTaskId(), this.getParentTaskId(), childIDs, space);
			((Computer2Space)space).putTaskIntoWaitingQueue(successorTask);
			
		} catch (Exception e) {
			System.err.println("Could not put successortask in the space");
		}
	}

	private void createChildrenTasks(UUID[] childIDs) {
		for (int i = 0; i < splitFactor; i++) {
			try {
				Task childTask = new FibonacciTask(n - (i+1), this.space, null, this.getTaskId());
				childIDs[i] = childTask.getTaskId();
				((Computer2Space)space).put(childTask);
				
			} catch (RemoteException e) {
				System.err.println("Could not put childtask in the space");
				e.printStackTrace();
			}
		}
	}
	
	public int compute(int n) {
		if (n == 0) {
			return 0;
		}
		return 1;
		
	}

}
