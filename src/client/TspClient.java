package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.Naming;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import api.Space;
import tasks.TspJob;
import tasks.TspTask.Tour;


public class TspClient {
	
	final static int N_PIXELS = 400;
	
	public static void main(String [] args){
		
	    if (System.getSecurityManager() == null){
	    	System.setSecurityManager(new SecurityManager());
	    }
	    Space space = null;
	    
	    try{
	    	space = (Space) Naming.lookup("//"+args[0]+":"+args[1]+"/Space");
	    	
	    	double[][] cities = 
	    		{
	    			{ 1, 1 },
	    			{ 2, 1 },
	    			{ 3, 1 },
	    			{ 4, 1 },
	    			{ 1, 2 },	
	    			{ 2, 2 }, // 6
	    			{ 3, 2 },
	    			{ 4, 2 },
	    			{ 1, 3 }, // 9
	    			{ 2, 3 },
	    			{ 3, 3 },
	    			{ 4, 3 }, // 12
	    			{ 1, 4 },
//	    			{ 2, 4 },
//	    			{ 3, 4 }, // 15
//	    			{ 4, 4 },
//	    			{ 1, 5 },
//	    			{ 2, 5 }, // 18
//	    			{ 3, 5 },
//	    			{ 4, 5 },
//	    			{ 1, 6 }, // 21
//	    			{ 2, 6 },
//	    			{ 3, 6 }, 
//	    			{ 4, 6 }  // 24

	    	};
	    	
	    	long jobStartTime = System.nanoTime();
	    	
			TspJob tspJob = new TspJob(cities);
			tspJob.generateTasks(space);
			Tour shortestTour = tspJob.collectResults(space);
			
			long jobEndTime = System.nanoTime();
			
			System.out.println("The job took: " + ((jobEndTime - jobStartTime) / 1000000000.0) + " s");
			System.out.println(shortestTour.toString());
			
			
		    JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue( cities,  tourToArray(shortestTour.getPath()));
		    
		    // display JLabels: graphic images
		    JFrame frame = new JFrame( "Result Visualizations" );
		    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    Container container = frame.getContentPane();
		    container.setLayout( new BorderLayout() );
		    container.add( new JScrollPane( euclideanTspLabel ), BorderLayout.EAST );
		    frame.pack();
		    frame.setVisible( true );
		    
	    }
	    catch (HeadlessException he) {
	    	System.err.println("Environment is headless. Dropping GUI.");
	    }
	    
	    catch (Exception e) {
            System.err.println("Client exception!");
            e.printStackTrace();
        }
	    
	}
	
	private static int [] tourToArray(ArrayList<Integer> tour) {
		int [] tourArray = new int [tour.size()];
		
		for (int i = 0; i < tourArray.length; i++) {
			tourArray[i] = tour.get(i);
		}
		
		return tourArray;
	}
	
	private static JLabel displayEuclideanTspTaskReturnValue( double[][] cities, int[] tour )
	{

	    // display the graph graphically, as it were
	    // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
	    double minX = cities[0][0], maxX = cities[0][0];
	    double minY = cities[0][1], maxY = cities[0][1];
	    for ( int i = 0; i < cities.length; i++ )
	    {
	        if ( cities[i][0] < minX ) minX = cities[i][0];
	        if ( cities[i][0] > maxX ) maxX = cities[i][0];
	        if ( cities[i][1] < minY ) minY = cities[i][1];
	        if ( cities[i][1] > maxY ) maxY = cities[i][1];
	    }
		
	    // scale points to fit in unit square
	    double side = Math.max( maxX - minX, maxY - minY );
	    double[][] scaledCities = new double[cities.length][2];
	    for ( int i = 0; i < cities.length; i++ )
	    {
	        scaledCities[i][0] = ( cities[i][0] - minX ) / side;
	        scaledCities[i][1] = ( cities[i][1] - minY ) / side;
	    }

	    Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
	    Graphics graphics = image.getGraphics();

	    int margin = 10;
	    int field = N_PIXELS - 2*margin;
	    // draw edges
	    graphics.setColor( Color.BLUE );
	    int x1, y1, x2, y2;
	    int city1 = tour[0], city2;
	    x1 = margin + (int) ( scaledCities[city1][0]*field );
	    y1 = margin + (int) ( scaledCities[city1][1]*field );
	    for ( int i = 1; i < cities.length; i++ )
	    {
	        city2 = tour[i];
	        x2 = margin + (int) ( scaledCities[city2][0]*field );
	        y2 = margin + (int) ( scaledCities[city2][1]*field );
	        graphics.drawLine( x1, y1, x2, y2 );
	        x1 = x2;
	        y1 = y2;
	    }
	    city2 = tour[0];
	    x2 = margin + (int) ( scaledCities[city2][0]*field );
	    y2 = margin + (int) ( scaledCities[city2][1]*field );
	    graphics.drawLine( x1, y1, x2, y2 );

	    // draw vertices
	    int VERTEX_DIAMETER = 6;
	    graphics.setColor( Color.RED );
	    for ( int i = 0; i < cities.length; i++ )
	    {
	        int x = margin + (int) ( scaledCities[i][0]*field );
	        int y = margin + (int) ( scaledCities[i][1]*field );
	        graphics.fillOval( x - VERTEX_DIAMETER/2,
	                           y - VERTEX_DIAMETER/2,
	                          VERTEX_DIAMETER, VERTEX_DIAMETER);
	    }
	    ImageIcon imageIcon = new ImageIcon( image );
	    return new JLabel( imageIcon );
	}

		
}
