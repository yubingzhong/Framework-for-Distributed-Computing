package tasks;

import java.util.UUID;
import api.Result;

public class FibonacciResult extends AbstractResult implements Result<Integer>{
	
	private static final long serialVersionUID = -6082197216283751652L;
	private int result;
	
	public FibonacciResult(int n, UUID id) {
		super(id);
		this.result = n;
	}

	@Override
	public Integer getTaskReturnValue() {
		return this.result;
	}
	
}
