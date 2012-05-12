package tasks;

import java.util.UUID;

import tasks.TspTask.Tour;
import api.Result;

public class TspResult extends AbstractResult implements Result<Tour>{

	private static final long serialVersionUID = -5428435049365804686L;
	private Tour result;
	
	public TspResult(Tour sp, UUID id){
		super(id);
		this.result = sp;
	}
	
	@Override
	public Tour getTaskReturnValue() {
		return this.result;
	}
	
	public String toString() {
		return this.result.toString();
	}
	

}
