package api;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface Computer extends Remote{
	
	/**
	 * Sets the computers reference to the shared object
	 *
	 * @param shared
	 * @throws RemoteException
	 */
	void setShared(Shared<?> shared) throws RemoteException;
	
	
	/**
	 * Returns a reference to the computer's shared object
	 * @return Shared object
	 * @throws RemoteException
	 */
	Shared<?> getShared() throws RemoteException;
	
	/**
	 * Shuts down the computer instance
	 * @throws RemoteException
	 */
	void stop() throws RemoteException;
}
