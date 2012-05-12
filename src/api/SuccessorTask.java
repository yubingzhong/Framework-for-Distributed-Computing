package api;

public interface SuccessorTask<T> {
	
	/**
	 * Checks if the successortask is waiting for the result given as an argument.
	 * If it does, and it is ready to be computed,
	 * it moves itself into the readyQueue to be computed.
	 * 
	 * @param result
	 * @return Returns true if the result was needed by the successortask
	 */
	public boolean feedArgument(Result<T> result);
}
