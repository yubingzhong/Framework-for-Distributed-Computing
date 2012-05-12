package system;

import java.util.ArrayList;
import api.Computer;

public class LeaseManager implements Runnable {
	
	private Computer computer;
	private ArrayList<Computer> registeredComputers;
	
	public LeaseManager() {
		this.registeredComputers = new ArrayList<Computer>();
	}
	
	public void register(Computer computer) {
		registeredComputers.add(computer);
		System.out.println("Registered computer #" + registeredComputers.size() );
	}
	
	public ArrayList<Computer> getRegisteredComputers() {
		return this.registeredComputers;
	}

	@Override
	public void run() {
		
		//while (true) {
			//TODO: Check that computers are alive
				
	//	}
	}
	
	
}
