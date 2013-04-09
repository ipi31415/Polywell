package display;

import javax.swing.SwingUtilities;

/**
 * Starts display of data
 * @author Ryan Dewey
 */
public class FusionMain {

	/**
	 * Begins data display
	 * @param args not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
                new FusionWindow();
            }
        });
	}
}
