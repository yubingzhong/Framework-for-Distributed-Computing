package tasks;

import java.io.Serializable;
import java.util.UUID;

import api.Result;

/**
 * This class provides some of the common attributes
 * and methods of result objects
 * @author christian
 *
 */
public abstract class AbstractResult implements Serializable {
		
	private static final long serialVersionUID = -6927185076522409858L;
	private UUID id;
	
	public AbstractResult(UUID id) {
		this.id = id;
	}
	
	public UUID getResultID() {
		return this.id;
	}
	
	public boolean equals(Object o) {
		Result r = (Result)o;
		if (r.getResultID().equals(this.id)) {
			return true;
		}
		return false;
	}
}
