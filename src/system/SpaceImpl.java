package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.swing.DebugGraphics;

import tasks.TSPShared;
import tasks.TspResult;
import tasks.TspTask.Tour;

import api.Computer;
import api.Computer2Space;
import api.Result;
import api.Shared;
import api.Space;
import api.SuccessorTask;
import api.Task;

public class SpaceImpl extends UnicastRemoteObject implements Space, Computer2Space {

	
	public static SpaceImpl SINGLETON;
	
	private BlockingDeque<Task> readyTaskStack;
	private BlockingDeque<SuccessorTask> waitingTaskStack;
	private BlockingQueue<Result> resultQueue;
	private BlockingQueue<Result> partialResults;
	
	private SpaceWorker spaceWorker;
	private LeaseManager leaseManager;
	
	private Shared shared;
	private Runtime runTime; 
	
	private int taskCount;
	private int successorTaskCount;
	
	
	private SpaceImpl() throws RemoteException {
		super();
		
		waitingTaskStack = new LinkedBlockingDeque<SuccessorTask>();
		readyTaskStack = new LinkedBlockingDeque<Task>();
		resultQueue = new LinkedBlockingQueue<Result>();
		partialResults = new LinkedBlockingQueue<Result>();
		
		spaceWorker = new SpaceWorker();
		spaceWorker.start();
		
		runTime = Runtime.getRuntime();
		runTime.addShutdownHook(new ShutdownProcedure(this));
		
		shared = new TSPShared(Double.MAX_VALUE);
		taskCount = 0;
		successorTaskCount = 0;
		
		leaseManager = new LeaseManager();

	}
	
	public static SpaceImpl getInstance() throws RemoteException{
		if (SINGLETON == null) {
			 SINGLETON = new SpaceImpl();
		}
		return SINGLETON;
	}
	

	/**
	 * Registers a computer with the space
	 */
	@Override
	public void register(Computer computer) throws RemoteException {
		leaseManager.register(computer);
		computer.setShared(shared);
		
	}

	@Override
	public void put(Task task) throws RemoteException {
		
		try {
			this.taskCount++;
			this.readyTaskStack.add(task);
			//System.out.println("Task " + task.getTaskId() + " added to readyqueue. Queuesize: " + readyTaskStack.size());
			
		} catch (Exception e) {
			System.err.println("Could not put task in the readyqueue");
			e.printStackTrace();
		}
	}
	
	public Task take() throws RemoteException {
		Task t = null;
		try {
			t = readyTaskStack.takeLast();
		} catch (InterruptedException e) {}
		
		return t;
	}

	public void putResultIntoResultQueue(Result result) throws RemoteException {
		try {
			System.out.println("Task count: " + this.taskCount);
			System.out.println("SuccessorTask count: " + this.successorTaskCount);
			
			if (result == null) {
				System.err.println("Result is null");
			}
			
			this.resultQueue.add(result);
			System.out.println("Final result was added to the resultqueue. Queuesize: " + resultQueue.size());
			
		} catch (Exception e) {
			System.err.println("Could not add the result to the resultQueue");
			e.printStackTrace();
		}
	}

	@Override
	public Result takeResult() throws RemoteException {
		Result r;
		
		while (true) {
			r = this.resultQueue.poll();
			
			if (r != null){
				return r;
			}
		}
	}
	
	public void putTaskIntoWaitingQueue(SuccessorTask task) {
		
		try {
			this.successorTaskCount++;
			this.waitingTaskStack.add(task);
			//System.out.println("SuccessorTask " + ((Task) task).getTaskId() + " was put in waitingqueue. Queuesize: " + waitingTaskStack.size());
		} catch (Exception e) {
			System.err.println("Could not put successortask in waitingQueue");
			e.printStackTrace();
		}
	}

	public SuccessorTask removeTaskFromWaitingQueue(SuccessorTask successorTask) throws RemoteException{
		try {
			this.waitingTaskStack.remove(successorTask);
			//System.out.println("SuccessorTask " + ((Task) successorTask).getTaskId() + " removed from waitingQueue. Queuesize: " + waitingTaskStack.size());
			
		} catch (Exception e) {
			System.err.println("Could not remove successorTask from waitingQueue");
			e.printStackTrace();
		}
		
		return successorTask;
	}

	public void putPartialResult(Result result) throws RemoteException {
		try {
			partialResults.add(result);
			spaceWorker.interrupt();
			
		} catch (Exception e) {
			System.err.println("Something bad happened while putting partial result into space");
			e.printStackTrace();
			
		}
	}
	
	public void removePartialResult(Result result) throws RemoteException {
		try {
			this.partialResults.remove(result);
		} catch (Exception e) {
			System.err.println("Could not remove partial result from queue");
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void setShared(Shared proposedShared) throws RemoteException {
		
		if (proposedShared.isNewerThan(this.shared)) {
			System.out.println("Space set new upper bound: " + proposedShared.get());
			this.shared = proposedShared;
			
			for (Computer computer : leaseManager.getRegisteredComputers()) {
				computer.setShared(this.shared);
			}
		}
	}
	
	
	@Override
	public void stop() throws RemoteException {
		
		System.out.println("Shutting down space..");
		for (Computer computer : leaseManager.getRegisteredComputers()) {
			computer.stop();
		}
		System.exit(0);
		
	}

	public static void main(String [] args){
		try {
			
			int serverPort = Integer.parseInt(args[0]);
			
		    if (System.getSecurityManager() == null){
		    	System.setSecurityManager(new SecurityManager());
		    }
			
			String name = "Space";
			Space space = SpaceImpl.getInstance();
			
			// Create RMI Registry
			Registry registry = LocateRegistry.createRegistry(serverPort);
         	registry.rebind(name, space);
          
         	System.out.println("Server (space) running on port: "+ serverPort);
			
		} catch (RemoteException e) {
			System.err.println("Space gave a RemoteException");
			e.printStackTrace();
		}
	}

	
	/**
	 * Thread that takes care of a graceful shutdown.
	 * Invoked when the program is killed.
	 * @author christian
	 *
	 */
	private class ShutdownProcedure extends Thread{
		
		private Space space;
		
		public ShutdownProcedure(Space space) {
			this.space = space;
		}
		
		@Override
		public void run() {
			
			try {
				this.space.stop();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return;
			
		}
		
	}
	
	/**
	 * Thread that continuously tries to match partial results with SuccessorTasks
	 * that are waiting.
	 * @author christian
	 *
	 */
	private class SpaceWorker extends Thread {
		
		public void run() {
			while (true) {
				
//				System.out.println("Worker running..");
//				System.out.println("PartialResultQeueue: " + partialResults.size());
//				System.out.println("WaitingTaskQueue: " + waitingTaskStack.size());
				
				if (partialResults.size() > 0 && waitingTaskStack.size() > 0) {
					for (SuccessorTask successorTask : waitingTaskStack) {
						for (Result result : partialResults) {
							if (successorTask.feedArgument(result)) { continue; }
						}
					}
				}
				else {
					try {
						sleep(10000);
					} catch (InterruptedException e) {}
				}
			}
		}
		
	}



	
	
}
