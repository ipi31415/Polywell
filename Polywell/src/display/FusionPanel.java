package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import magnetic.MagneticField;
import utilities.Pair;

public class FusionPanel extends JPanel{
	private MagneticField field;
	private Pair<Double, Double> xRange;
	private Pair<Double, Double> yRange;
	private Pair<Double, Double> zRange;
	private int gridPoints;
	
	public FusionPanel() {
		this(null, null, 0);
	}
	
	public FusionPanel(MagneticField field, List<Pair<Double, Double>> plotRanges, int gridPoints) {
		super();
		this.setPreferredSize(new Dimension(640, 480));
		this.field = field;
		this.xRange = plotRanges.get(0);
		this.yRange = plotRanges.get(1);
		this.zRange = plotRanges.get(2);
		this.gridPoints = gridPoints;
	}
	
	public FusionPanel(MagneticField field, Pair<Double, Double> xRange, Pair<Double, Double> yRange,
			Pair<Double, Double> zRange, int gridPoints) {
		super();
		this.setPreferredSize(new Dimension(640, 480));
		this.field = field;
		this.xRange = xRange;
		this.yRange = yRange;
		this.zRange = zRange;
		this.gridPoints = gridPoints;
	}
	
	public void setXRange(Pair<Double, Double> xRange) {
		this.xRange = xRange;
	}
	
	public void setYRange(Pair<Double, Double> yRange) {
		this.yRange = yRange;
	}
	
	public void setZRange(Pair<Double, Double> zRange) {
		this.zRange = zRange;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(Color.BLUE);
		g2.drawLine(0, 0, 640, 480);
		
		drawField();
	}
	
	public void drawField() {
		
	}
}
