package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.Naming;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import api.Space;
import tasks.MandelbrotSetJob;


public class MandelbrotSetClient {
	
	final static int N_PIXELS = 1024;
	final static int ITERATION_LIMIT = 4096;
	
	public static void main(String [] args){
		
	    if (System.getSecurityManager() == null){
	    	System.setSecurityManager(new SecurityManager());
	    }
	    Space space;
	    
	    try{
	    	space = (Space) Naming.lookup("//"+args[0]+":"+args[1]+"/Space");
	    	
	    	long jobStartTime = System.nanoTime();
	    	
	    	MandelbrotSetJob mandelJob = new MandelbrotSetJob(ITERATION_LIMIT);
			mandelJob.generateTasks(space);
			int [][] solution = mandelJob.collectResults(space);
			
			long jobEndTime = System.nanoTime();
			
			System.out.println("The job took: " + ((jobEndTime - jobStartTime) / 1000000.0) + "ms");
		    
		    JLabel mandelbrotLabel = displayMandelbrotSetTaskReturnValue( solution );

			
		    // display JLabels: graphic images
		    JFrame frame = new JFrame( "Result Visualizations" );
		    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    Container container = frame.getContentPane();
		    container.setLayout( new BorderLayout() );
		    container.add( new JScrollPane( mandelbrotLabel ), BorderLayout.WEST );
		    frame.pack();
		    frame.setVisible( true );
		    
            
        } catch (Exception e) {
            System.err.println("Client exception!");
        }
	}
	
	private static JLabel displayMandelbrotSetTaskReturnValue( int[][] counts )
	{
	    Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
	    Graphics graphics = image.getGraphics();
	    for ( int i = 0; i < counts.length; i++ )
	    for ( int j = 0; j < counts.length; j++ )
	    {
	        graphics.setColor( getColor( counts[i][j] ) );
	        graphics.fillRect(i, j, 1, 1);
	    }
	    ImageIcon imageIcon = new ImageIcon( image );
	    return new JLabel( imageIcon );
	}
	
	private static Color getColor( int i )
	{
		return new Color((i * 255) / ITERATION_LIMIT);
	}
	
}
