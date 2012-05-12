package api;


public interface Job {

		/**
		 * Starts off the job by creating a root task
		 * and moving it to the space's readyQueue
		 * @param space
		 */
		public void generateTasks(Space space);
		
		/**
		 * Collects the result of the job as soon as it becomes
		 * available at the space
		 * @param <T>
		 * @param space
		 */
		public <T> T collectResults(Space space);
}
