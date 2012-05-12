package api;

import java.io.Serializable;
import java.util.UUID;

public interface Result<T> extends Serializable {

	/**
	 * Returns the result value in whatever format is 
	 * suitable for the task at hand.
	 */
	T getTaskReturnValue();
	
	/**
	 * Returns the id of the task that the 
	 * result solves.
	 */
	UUID getResultID();
	
}
