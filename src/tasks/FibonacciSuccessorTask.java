package tasks;

import java.util.UUID;
import api.Result;
import api.Space;



public class FibonacciSuccessorTask extends AbstractSuccessorTask {

	private static final long serialVersionUID = 2208682373572709999L;

	public FibonacciSuccessorTask(UUID taskID, UUID parentTaskID, UUID [] taskResultsNeeded, Space space) {
		super(taskID, parentTaskID, taskResultsNeeded, space);
	}

	public Result<Integer> compose() {
		
		int result = (Integer)partialResults.get(0).getTaskReturnValue() + (Integer)partialResults.get(1).getTaskReturnValue();
		return new FibonacciResult(result, this.getTaskId());
	}
}
