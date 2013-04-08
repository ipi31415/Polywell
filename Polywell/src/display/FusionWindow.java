package display;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
        
        JPanel fusionPanel = new FusionPanel();
        frame.add(fusionPanel);
        frame.pack();
        frame.setVisible(true);
	}
}
