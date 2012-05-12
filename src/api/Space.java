package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space extends Remote{
	
	public static String SERVICE_NAME = "Space";
	
	/**
	 * Adds a task to the space's readyqueue to be computed
	 * 
	 * @param task
	 * @throws RemoteException
	 */
	void put(Task task) throws RemoteException;
	
	/**
	 * Used by the client to take a result from the space's resultQueue
	 * @return Returns a result
	 * @throws RemoteException
	 */
	Result<?> takeResult() throws RemoteException;
	
	
	/**
	 * Updates the shared object in the space and notifies all the other registered computers
	 * of the update
	 * @param shared The proposed new Shared object
	 * @throws RemoteException
	 */
	void setShared(Shared<?> shared) throws RemoteException;
	
	
	/**
	 * Stops the space by shutting down all computers
	 * connected to it and exiting itself
	 * @throws RemoteException
	 */
	void stop() throws RemoteException;
}
