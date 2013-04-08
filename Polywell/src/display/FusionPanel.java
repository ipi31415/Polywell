package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import magnetic.MagneticField;
import utilities.DoubleVector;
import utilities.Pair;

public class FusionPanel extends JPanel implements MouseInputListener {
	public static final DoubleVector DEFAULT_VIEW_DIRECTION = new DoubleVector(0, 1, 0);
	public static final DoubleVector DEFAULT_UP_DIRECTION = new DoubleVector(0, 0, 1);
	public static final double VECTOR_CUTOFF = 20.0;
	public static final double MAX_COLOR_NORM = 2.0;
	
	private MagneticField field;
	private Pair<Double, Double> xRange;
	private Pair<Double, Double> yRange;
	private Pair<Double, Double> zRange;
	private int gridPoints;
	private DoubleVector gridWidths;
	private DoubleVector centerPoint;
	private DoubleVector viewDirection;
	private DoubleVector upDirection;
	private DoubleVector scaleFactors;

	private DoubleVector clickPoint;
	
	public FusionPanel() {
		this(null, null, 0, new DoubleVector(0, 0, 0), new DoubleVector(0, 1, 0));
	}

	public FusionPanel(MagneticField field, List<Pair<Double, Double>> plotRanges, int gridPoints,
			DoubleVector centerPoint) {
		this(field, plotRanges.get(0), plotRanges.get(1), plotRanges.get(2), gridPoints, centerPoint, 
				DEFAULT_VIEW_DIRECTION);
	}
	
	public FusionPanel(MagneticField field, List<Pair<Double, Double>> plotRanges, int gridPoints,
			DoubleVector centerPoint, DoubleVector viewDirection) {
		this(field, plotRanges.get(0), plotRanges.get(1), plotRanges.get(2), gridPoints, centerPoint, 
				viewDirection);
	}
	
	public FusionPanel(MagneticField field, Pair<Double, Double> xRange, Pair<Double, Double> yRange,
			Pair<Double, Double> zRange, int gridPoints, DoubleVector centerPoint, DoubleVector viewDirection) {
		super();
		this.setPreferredSize(new Dimension(640, 480));
		this.field = field;
		this.xRange = xRange;
		this.yRange = yRange;
		this.zRange = zRange;
		this.gridPoints = gridPoints;
		this.gridWidths = new DoubleVector((xRange.getB() - xRange.getA()) / gridPoints,
				(yRange.getB() - yRange.getA()) / gridPoints,
				(zRange.getB() - zRange.getA()) / gridPoints);
		this.centerPoint = centerPoint;
		this.viewDirection = viewDirection.normalize();
		this.upDirection = DEFAULT_UP_DIRECTION;
		addMouseMotionListener(this);
		addMouseListener(this);
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
		
		drawAxes(g2);
		drawPolywell(g2);
		drawField(g2);
	}
	
	public void drawAxes(Graphics2D g) {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		DoubleVector center = new DoubleVector(centerX, centerY);
		
		DoubleVector xMax = new DoubleVector(xRange.getB(), 0, 0);
		DoubleVector xMin = new DoubleVector(xRange.getA(), 0, 0);
		xMax = xMax.getProjection3Dto2D(viewDirection, upDirection);
		xMin = xMin.getProjection3Dto2D(viewDirection, upDirection);
		DoubleVector yMax = new DoubleVector(0, yRange.getB(), 0);
		DoubleVector yMin = new DoubleVector(0, yRange.getA(), 0);
		yMax = yMax.getProjection3Dto2D(viewDirection, upDirection);
		yMin = yMin.getProjection3Dto2D(viewDirection, upDirection);
		DoubleVector zMax = new DoubleVector(0, 0, zRange.getB());
		DoubleVector zMin = new DoubleVector(0, 0, zRange.getA());
		zMax = zMax.getProjection3Dto2D(viewDirection, upDirection);
		zMin = zMin.getProjection3Dto2D(viewDirection, upDirection);
		
		double xDrawMin = new DoubleVector(xMin.getValue(0), xMax.getValue(0),
				yMin.getValue(0), yMax.getValue(0),
				zMin.getValue(0), zMax.getValue(0)).min();
		double xDrawMax = new DoubleVector(xMin.getValue(0), xMax.getValue(0),
				yMin.getValue(0), yMax.getValue(0),
				zMin.getValue(0), zMax.getValue(0)).max();
		double yDrawMin = new DoubleVector(xMin.getValue(1), xMax.getValue(1),
				yMin.getValue(1), yMax.getValue(1),
				zMin.getValue(1), zMax.getValue(1)).min();
		double yDrawMax = new DoubleVector(xMin.getValue(1), xMax.getValue(1),
				yMin.getValue(1), yMax.getValue(1),
				zMin.getValue(1), zMax.getValue(1)).max();
		
		double maxDistX = Math.max(xDrawMax, xDrawMin);
		double maxDistY = Math.max(yDrawMax, yDrawMin);
		
		double xScaleFactor = getWidth() / (2 * maxDistX);
		double yScaleFactor = -1 * getHeight() / (2 * maxDistY);
		scaleFactors = new DoubleVector(xScaleFactor, yScaleFactor).multiply(2.0 / 3.0);
		
		xMin = xMin.elementMultiply(scaleFactors).add(center);
		xMax = xMax.elementMultiply(scaleFactors).add(center);
		yMin = yMin.elementMultiply(scaleFactors).add(center);
		yMax = yMax.elementMultiply(scaleFactors).add(center);
		zMin = zMin.elementMultiply(scaleFactors).add(center);
		zMax = zMax.elementMultiply(scaleFactors).add(center);
		
		g.setColor(Color.BLACK);
		g.drawLine((int) xMin.getValue(0).doubleValue(), (int) xMin.getValue(1).doubleValue(), 
				(int) xMax.getValue(0).doubleValue(), (int) xMax.getValue(1).doubleValue());
		g.drawLine((int) yMin.getValue(0).doubleValue(), (int) yMin.getValue(1).doubleValue(), 
				(int) yMax.getValue(0).doubleValue(), (int) yMax.getValue(1).doubleValue());
		g.drawLine((int) zMin.getValue(0).doubleValue(), (int) zMin.getValue(1).doubleValue(), 
				(int) zMax.getValue(0).doubleValue(), (int) zMax.getValue(1).doubleValue());
	}
	
	public void drawPolywell(Graphics2D g) {
		double radius = 4;
		double thickness = .5;
		double centerDistance = radius + thickness * Math.sqrt(2);
		
	}
	
	public void drawField(Graphics2D g) {
		HashMap<DoubleVector, DoubleVector> vectors = field.getResults();
		
		for (DoubleVector v: vectors.keySet()) {
			drawVector(g, v, vectors.get(v), getColor(vectors.get(v)));
		}
 		try {
			field.storeResults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Color getColor(DoubleVector v) {
		double norm = v.norm();
		if (norm > MAX_COLOR_NORM) {
			norm = MAX_COLOR_NORM;
		}
		int blue = (int) ((1 - norm / MAX_COLOR_NORM) * 255);
		int red = (int) ((norm / MAX_COLOR_NORM) * 255);
		return new Color(red, 0, blue);
	}
	
	public void drawVector(Graphics2D g, DoubleVector o, DoubleVector x, Color c) {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		DoubleVector center = new DoubleVector(centerX, centerY);
		
		x = x.add(o);
		x = x.getProjection3Dto2D(viewDirection, upDirection);
		o = o.getProjection3Dto2D(viewDirection, upDirection);
		
		o = o.elementMultiply(scaleFactors).add(center);
		x = x.elementMultiply(scaleFactors).add(center);
		
		DoubleVector cutoff = x.subtract(o);
		if (cutoff.norm() > VECTOR_CUTOFF) {
			cutoff = cutoff.normalize().multiply(VECTOR_CUTOFF);
			x = o.add(cutoff);
		}

		g.setColor(c);
		g.drawLine((int) o.getValue(0).doubleValue(), (int) o.getValue(1).doubleValue(), 
				(int) x.getValue(0).doubleValue(), (int) x.getValue(1).doubleValue());
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		DoubleVector currentPoint = new DoubleVector(event.getX(), event.getY());
		DoubleVector change = currentPoint.subtract(clickPoint);
		change = change.elementDivide(scaleFactors);
		DoubleVector up = upDirection.subtract(
				upDirection.multiply(upDirection.dotProduct(viewDirection))).normalize();
		DoubleVector right = viewDirection.crossProduct(up).normalize();
		viewDirection = viewDirection.subtract(right.multiply(change.getValue(0))).normalize();
		viewDirection = viewDirection.subtract(up.multiply(change.getValue(1)));
		clickPoint = currentPoint;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent event) {}

	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event) {
		clickPoint = new DoubleVector(event.getX(), event.getY());
	}

	@Override
	public void mouseReleased(MouseEvent event) {}
}
