	package system;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import api.ComputeThread;
import api.Computer;
import api.Computer2Space;
import api.Shared;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer {

	private static final long serialVersionUID = 6384594488269807165L;
	private Shared shared;
	private Computer2Space computer2Space;
	
	private ArrayList<Thread> threads;
	private Runtime runTime;
	private int noOfThreads;
	
	protected ComputerImpl(Computer2Space computer2Space, boolean useMultipleCores) throws RemoteException {
		super();
		this.computer2Space = computer2Space;
		this.runTime = Runtime.getRuntime();
		threads = new ArrayList<Thread>();
		
		if (useMultipleCores)
			noOfThreads = runTime.availableProcessors();
		else
			noOfThreads = 1;
		
		for (int i = 0; i < noOfThreads; i++) {
			threads.add(new ComputeThreadImpl(this.computer2Space, this));
		}
		
		System.out.println("Available cores: " + availableCores());
		System.out.println("Starting " + threads.size() + " threads");
		
		for (Thread thread : threads) {
			thread.start();
		}
	}
	
	public Shared getShared() {
		return this.shared;
	}
	
	private int availableCores() {
		return runTime.availableProcessors();
	}

	public void setShared(Shared proposedShared) throws RemoteException {
		
		if (this.shared == null) {
			this.shared = proposedShared;
		} 
		else {
			if (proposedShared.isNewerThan(this.shared)) {
//				System.out.println("New upper bound: " + proposedShared.get());
				this.shared = proposedShared;
			}
		}
		return;
	}

	@Override
	public void stop() throws RemoteException {
		System.out.println("Computer stopped");
		System.exit(0);
	}
	
public static void main(String [] args){
		
	    try{
	    	String name = "Computer";
            Computer2Space computer2Space = (Computer2Space) Naming.lookup("//"+args[0]+":"+args[1]+"/Space");
            Computer computer = new ComputerImpl(computer2Space, true);
            
            computer2Space.register(computer);
            System.out.println("Computer is registered and running");
            
        }
	    catch (RemoteException e) {
            System.err.println("Computer remote exception");
            e.printStackTrace();
        }
	    catch (Exception e2) {
	    	System.err.println("Computer exception");
	    	e2.printStackTrace();
	    }
	}
}
