package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer2Space extends Remote{
	
	/**
	 * Register a computer with the space so that it can 
	 * be used to compute tasks
	 * @param computer
	 * @throws java.rmi.RemoteException
	 */
    void register( Computer computer ) throws RemoteException;

    /**
     * Puts a final result in the resultqueue
     * @param result
     * @throws RemoteException
     */
    void putResultIntoResultQueue(Result<?> result) throws RemoteException;
    
    
    /**
     * Puts a partial result into the space to be used by its successortask
     * @param result
     * @throws RemoteException
     */
    void putPartialResult(Result<?> result) throws RemoteException;
    
    
    /**
     * Removes a partial result from the partialResult queue
     * @param result
     * @throws RemoteException
     */
    void removePartialResult(Result<?> result) throws RemoteException;
    
    
    /**
     * Puts a successor task into the space's waiting queue.
     * Tasks in the waiting queue will not be executed until
     * all their arguments are present, at which time they will
     * be moved into the readyQueue.
     * @param task
     * @throws RemoteException
     */
    void putTaskIntoWaitingQueue(SuccessorTask<?> task) throws RemoteException;
    
    
    /**
     * Removes the successorTask fromt the waitingqueue and returns it
     * @param task
     * @return Returns the removed successortask
     * @throws RemoteException
     */
    SuccessorTask<?> removeTaskFromWaitingQueue(SuccessorTask<?> task) throws RemoteException;
    
    
	/**
	 * Updates the shared object in the space and notifies all the other registered computers
	 * of the update
	 * @param shared The proposed new Shared object
	 * @throws RemoteException
	 */
	void setShared(Shared<?> shared) throws RemoteException;
    
    /**
     * Puts a task into the readyQueue. 
     * @param task
     * @throws RemoteException
     */
    void put(Task task) throws RemoteException;
    
    /**
     * Takes a task from the space's readyQueue
     * @return A Task
     * @throws RemoteException
     */
    Task take() throws RemoteException;
   

}
