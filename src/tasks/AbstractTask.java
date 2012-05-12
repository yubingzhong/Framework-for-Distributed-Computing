package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

import api.Space;
import api.Task;

/**
 * This class provides some of the common attributes
 * and methods of task objects
 * @author christian
 *
 */
public abstract class AbstractTask implements Serializable{
	
	private static final long serialVersionUID = 2827792155321836133L;
	private UUID id; 
	private UUID parentTaskId;

	
	public AbstractTask(UUID taskID, UUID parentTaskID){
		
		if (taskID == null) {
			this.id = UUID.randomUUID();
		}
		else {
			this.id = taskID;
		}
		
		this.parentTaskId = parentTaskID;
	}
	
	public UUID getTaskId() {
		return this.id;
	}
	
	public UUID getParentTaskId() {
		return this.parentTaskId;
	}
}
