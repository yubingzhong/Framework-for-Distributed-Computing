package tasks;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

import api.ComputeThread;
import api.Computer;
import api.Computer2Space;
import api.Result;
import api.Shared;
import api.Space;
import api.SuccessorTask;
import api.Task;


/**
 * This class represents a successortask that waits for its child tasks to complete before it can execute.
 * The execution of a successorobject is to compose the pieces (children) into a larger picture
 * and either return a result (if it is the root task) or become a new piece of an even larger picture (if it is a subtask).
 * @author christian
 *
 */
public abstract class AbstractSuccessorTask extends AbstractTask implements SuccessorTask, Task{

	private static final long serialVersionUID = -4694903848478097142L;
	protected Space space;
	protected UUID [] resultIDsNeeded;
	protected int noOfArgumentsNeeded;
	protected HashMap<Integer, Result> partialResults;


	
	public AbstractSuccessorTask(UUID taskID, UUID parentTaskID, UUID [] resultIDsNeeded, Space space) {
		super(taskID, parentTaskID);

		this.partialResults = new HashMap<Integer, Result>();
		this.resultIDsNeeded = resultIDsNeeded;
		this.noOfArgumentsNeeded = resultIDsNeeded.length;
		this.space = space;
	}
	
	public void execute(Shared shared, ComputeThread computeThread) {
		
		// Task does not have a parent, compose the task and return result to client
		if (this.getParentTaskId() == null){
			
			try {
				Result r = compose();
				((Computer2Space)space).putResultIntoResultQueue(r);
				System.out.println("Job done!");
				
			} catch (Exception e) {
				System.err.println("Could not add the result to the resultqueue");
				e.printStackTrace();
			}
		}
		
		// Task has a parent which means it is a subresult
		else {
			try {
				((Computer2Space)space).putPartialResult(compose());
			} catch (Exception e) {
				System.err.println("Could not send the result to the space.");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized boolean feedArgument(Result result) {
		for (int i = 0; i < resultIDsNeeded.length; i++) {
			
			// If the task is waiting for this result..
			if (resultIDsNeeded[i].equals(result.getResultID())) {
				partialResults.put(i, result);
				noOfArgumentsNeeded--;
				
				try {((Computer2Space)this.space).removePartialResult(result);}
				catch (RemoteException e1) { e1.printStackTrace();}

				// If the task has all the arguments needed..
				if (noOfArgumentsNeeded == 0) {
					try {
						this.space.put(this);
					} catch (RemoteException e) {
						System.err.println("Could not put task into space's readyQueue");
						e.printStackTrace();
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object o) {
		
		if (o instanceof SuccessorTask) {
			Task successorTask = ((Task)o);
			
			if (successorTask.getTaskId().equals(this.getTaskId())) {
				return true;
			}
		}
		
		return false;
	}
	
	public abstract Result compose();
}
