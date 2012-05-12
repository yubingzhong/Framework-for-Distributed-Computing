package tasks;

import java.io.Serializable;

import api.Shared;

public class TSPShared implements Shared<Double>, Serializable{
	
	private double upperBound;
	
	public TSPShared(double upperBound) {
		this.upperBound = upperBound;
	}
	
	@Override
	public Double get() {
		return this.upperBound;
	}

	@Override
	public boolean isNewerThan(Shared shared) {
		
		if ((Double)shared.get() > this.get()) {
			return true;
		}
		return false;
	}

}
