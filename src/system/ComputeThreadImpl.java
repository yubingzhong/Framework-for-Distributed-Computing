package system;

import java.rmi.RemoteException;

import api.ComputeThread;
import api.Computer;
import api.Computer2Space;
import api.Shared;
import api.Task;

public class ComputeThreadImpl extends Thread implements ComputeThread {

	private Computer2Space space;
	private Computer computer;
	
	private Task cachedTask = null;
	
	public ComputeThreadImpl(Computer2Space space, Computer computer) {
		this.space = space;
		this.computer = computer;
	}
	
	public void run() {
		while (true) {
			Task t = takeTask();
			execute(t);
		}
	}

	private Task takeTask() {
		Task t = null;
		
		if (cachedTask != null) {
			t = cachedTask;
			cachedTask = null;
			return t;
		}
		
		try {
			t = space.take();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public void cacheTask(Task task) {
		this.cachedTask = task;
	}

	public void execute(Task task) {
		System.out.println(this.toString() + " executing task " + task.getTaskId());
		
		try {
			task.execute(computer.getShared(), this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public Shared getShared() {
		try {
			return computer.getShared();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setShared(Shared shared) {
		
		ComputeThreadWorker computeThreadWorker = new ComputeThreadWorker(shared);
		computeThreadWorker.start();
		return;
	}
	
	class ComputeThreadWorker extends Thread{
		
		private Shared shared;
		
		public ComputeThreadWorker(Shared shared) {
			this.shared = shared;
		}
		
		public void run() {
			try {
				space.setShared(this.shared);
			} catch (RemoteException e) {
				e.printStackTrace();
				System.err.println("Worker thread could not set shared object with space");
			}
			
			return;
		}
	}
	
}
