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

import com.google.common.collect.Lists;

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
		DoubleVector direction = new DoubleVector(0, 1, 0);
		double radius = 5.0;
		double thickness = 1.0;
		double magnitude = 0.7;
        Pair<CurrentDensityFunction, List<Pair<Double, Double>>> temp = 
        		CurrentDensityFunctionFactory.getCubicPolywellFunction(radius, thickness, magnitude);
		CurrentDensityFunction j = temp.getA();
		List<Pair<Double, Double>> ranges = temp.getB();
		List<Pair<Double, Double>> newRanges = Lists.<Pair<Double, Double>>newArrayList();
		for (int i = 0; i < 3; i++) {
			newRanges.add(Pair.of(ranges.get(i).getA() * 2, ranges.get(i).getB() * 2));
		}
		MagneticField field = new MagneticField(j, ranges, "Cubic Polywell30");
		try {
			field.readResults();
		} catch (Exception e) {
			e.printStackTrace();
		}
        JPanel fusionPanel = new FusionPanel(field, newRanges, 20, new DoubleVector(0, 0, 0),
        		new DoubleVector(-1, 1, -1));
        frame.add(fusionPanel);
        frame.pack();
        frame.setVisible(true);
	}
}
