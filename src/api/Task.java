package api;

import java.util.UUID;

public interface Task  {
	
	
	/**
	 * Executes a task by either decomposing the task into subtasks or
	 * computing the result of the task
	 * @param shared 
	 * @param computeThread The ComputeThread executing the task
	 */
	public void execute(Shared<?> shared, ComputeThread computeThread);
	
	/**
	 * Returns the ID of the task. The ID is chosen at random from a
	 * large space so the probability of IDs colliding is negligible.
	 * Therefore it is safe to assume that these IDs are unique
	 * @return The unique ID of the task
	 */
	public UUID getTaskId();
	
}
