package tasks;

import java.util.UUID;
import tasks.TspTask.Tour;
import api.Result;
import api.Space;
import api.Task;

public class TSPSuccessorTask extends AbstractSuccessorTask implements Task {


	private static final long serialVersionUID = -5499561890451175526L;
	
	public TSPSuccessorTask(UUID taskID, UUID parentTaskID, UUID [] taskResultsNeeded, Space space) {
		super(taskID, parentTaskID, taskResultsNeeded, space);
	}
	
	/**
	 * Find the shortest of the paths
	 * @return Returns the resulting shortest tour
	 */
	public Result<Tour> compose() {
		
		@SuppressWarnings("unchecked")
		Result<Tour> shortestTour = partialResults.get(0);
		
		for (Result<Tour> result : partialResults.values()) {
			
			if (result.getTaskReturnValue().getDistance() < shortestTour.getTaskReturnValue().getDistance())
				shortestTour = result;
		}
		
		//System.out.println("Solution composed..");
		return new TspResult(shortestTour.getTaskReturnValue(), this.getTaskId());
	}
	
	public String toString() {
		return "SuccessorTask: " + this.getTaskId().toString();
	}

}
