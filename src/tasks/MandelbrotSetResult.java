package tasks;

import java.util.UUID;
import api.Result;


/**
 * Result that stores a two dimensional array that contains an integer saying how
 * many iterations where used to find out if the pixel is in the mandelbrotset or not.
 * This value is then used to set the color of the pixel according to some chosen coloring
 * scheme.
 * @author christian
 *
 */
public class MandelbrotSetResult extends AbstractResult implements Result<int[][]>{
	
	private static final long serialVersionUID = -6082197216283751652L;
	private int [][] result;
	
	public MandelbrotSetResult(int [][] pixelMap, UUID id) {
		super(id);
		this.result = pixelMap;
	}

	@Override
	public int[][] getTaskReturnValue() {
		return this.result;
	}
	
}
