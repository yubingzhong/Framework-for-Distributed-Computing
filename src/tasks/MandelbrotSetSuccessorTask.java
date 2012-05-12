package tasks;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

import api.Computer2Space;
import api.Result;
import api.Space;
import api.SuccessorTask;
import api.Task;



public class MandelbrotSetSuccessorTask extends AbstractSuccessorTask {

	private static final long serialVersionUID = -5499561890451175526L;
	private int splitFactor;
	
	public MandelbrotSetSuccessorTask(UUID taskID, UUID parentTaskID, UUID [] taskResultsNeeded, int splitFactor, Space space) {
		super(taskID, parentTaskID, taskResultsNeeded, space);
		
		this.splitFactor = splitFactor;
	}
	
	/**
	 * Composes the subarrays into a larger array
	 * @return A composed result
	 */
	public Result<int[][]> compose() {
		
			int [][] firstSubresult = (int [][])this.partialResults.get(0).getTaskReturnValue();
			int subresultSize = firstSubresult[0].length;
			int [][] result = new int [2 * subresultSize][2 * subresultSize];
			int [][] arrayToCopy;
			
//			System.out.println("" + splitFactor + " " + subresultSize+"x"+subresultSize+"px big array");
			
			int subArrayCounter = 0;
			
			for (int i = 0; i < (splitFactor / 2); i++) {
				for (int j = 0; j < (splitFactor / 2); j++) {
					
					arrayToCopy = (int[][])this.partialResults.get(subArrayCounter).getTaskReturnValue();
					result = copy2DArray(i, j, arrayToCopy, result);
					subArrayCounter++;
				}
			}
		return new MandelbrotSetResult(result, this.getTaskId());
	}
	
	private int [][] copy2DArray(int dstx, int dsty, int[][]src, int[][]dst) {
		for (int j = 0; j < src[0].length; j++) {
			for (int k = 0; k < src[0].length; k++) {
				dst[dstx + j][dsty + k] = src[j][k];
			}
		}
		
		return dst;
	}


}
