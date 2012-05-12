package client;


import java.rmi.Naming;
import api.Space;
import tasks.FibonacciJob;

public class FibonacciClient {
	
	final static int FIBONACCI_NUMBER = 20;
	
	public static void main(String [] args){
		
	    if (System.getSecurityManager() == null){
	    	System.setSecurityManager(new SecurityManager());
	    }
	    Space space;
	    
	    try{
	    	space = (Space) Naming.lookup("//"+args[0]+":"+args[1]+"/Space");
	    	long jobStartTime = System.nanoTime();
	    	
			FibonacciJob fibonacciJob = new FibonacciJob(FIBONACCI_NUMBER);
			fibonacciJob.generateTasks(space);
			Integer solution = fibonacciJob.collectResults(space);
			
			long jobEndTime = System.nanoTime();
			
			System.out.println("The job took: " + ((jobEndTime - jobStartTime) / 1000000.0) + "ms");
			System.out.println("Fibonacci number for " + FIBONACCI_NUMBER + " is " + solution);
		    
        } catch (Exception e) {
            System.err.println("Client exception!");
            e.printStackTrace();
        }
	    
	}
	
}
