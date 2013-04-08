package display;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import magnetic.CurrentDensityFunction;
import magnetic.CurrentDensityFunctionFactory;
import magnetic.MagneticField;
import utilities.DoubleVector;
import utilities.Pair;

public class FusionWindow {
	public static final int DEFAULT_WINDOW_WIDTH = 640;
	public static final int DEFAULT_WINDOW_HEIGHT = 480;
	
	private JFrame frame;
	
	public FusionWindow() {
		frame = new JFrame("Polywell Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(((int) screenSize.getWidth() - DEFAULT_WINDOW_WIDTH) / 2, 
        		((int) screenSize.getHeight() - DEFAULT_WINDOW_HEIGHT) / 2);
        
        DoubleVector center = new DoubleVector(0, 0, 0);
		DoubleVector direction = new DoubleVector(0, 0, 1);
		double radius = 5.0;
		double thickness = 1.0;
		double magnitude = 2.0;
        Pair<CurrentDensityFunction, List<Pair<Double, Double>>> temp = 
        		CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, center, 
        				direction, magnitude);
		CurrentDensityFunction j = temp.getA();
		List<Pair<Double, Double>> ranges = temp.getB();
		System.out.println(ranges);
		MagneticField field = new MagneticField(j, ranges);
        JPanel fusionPanel = new FusionPanel(field, ranges, 10, new DoubleVector(0, 0, 0),
        		new DoubleVector(-1, 1, -1));
        frame.add(fusionPanel);
        frame.pack();
        frame.setVisible(true);
	}
}
