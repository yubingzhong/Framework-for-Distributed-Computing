package api;

public interface ComputeThread {

	/**
	 * Sets the ComputeThread's shared object,
	 * the object is propagated to the computer and
	 * on to th space and the other computers
	 * @param proposedShared
	 */
	public void setShared(Shared<?> proposedShared);
	
	/**
	 * Gets the shared object held by the computer
	 * @return
	 */
	public Shared<?> getShared();
	
	/**
	 * When generating subtasks this method is used for
	 * retaining one of the tasks and therefore reducing communication
	 * overhead by one RTT
	 * @param t
	 */
	public void cacheTask(Task t);
}