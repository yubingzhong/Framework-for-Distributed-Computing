package api;

public interface Shared <T>{
	
	/**
	 * @return Returns the value stored in the shared object
	 */
	T get();
	
	
	/**
	 * Checks to see if this object is newer than the argument
	 * @param shared
	 * @return returns true if the argument is older than this object
	 */
	boolean isNewerThan(Shared<?> shared);
	
}
