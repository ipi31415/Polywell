package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import magnetic.CurrentDensityFunction;
import magnetic.MagneticField;
import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Panel which renders and displays Polywell data
 * @author Ryan Dewey
 */
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

	private HashMap<DoubleVector, DoubleVector> currentMap;
	
	/**
	 * Creates an empty FusionPanel
	 */
	public FusionPanel() {
		this(null, null, 0, new DoubleVector(0, 0, 0), new DoubleVector(0, 1, 0));
	}

	/**
	 * Creates a FusionPanel with the given parameters. Uses default view direction (y-axis)
	 * @param field magnetic field
	 * @param plotRanges range of plot
	 * @param gridPoints number of points to plot field over
	 * @param centerPoint center of plot
	 */
	public FusionPanel(MagneticField field, List<Pair<Double, Double>> plotRanges, int gridPoints,
			DoubleVector centerPoint) {
		this(field, plotRanges.get(0), plotRanges.get(1), plotRanges.get(2), gridPoints, centerPoint, 
				DEFAULT_VIEW_DIRECTION);
	}
	
	/**
	 * Creates a FusionPanel with the given parameters
	 * @param field magnetic field
	 * @param plotRanges range of plot
	 * @param gridPoints number of points to plot field over
	 * @param centerPoint center of plot
	 * @param viewDirection initial viewing direction
	 */
	public FusionPanel(MagneticField field, List<Pair<Double, Double>> plotRanges, int gridPoints,
			DoubleVector centerPoint, DoubleVector viewDirection) {
		this(field, plotRanges.get(0), plotRanges.get(1), plotRanges.get(2), gridPoints, centerPoint, 
				viewDirection);
	}
	
	/**
	 * Creates a FusionPanel with the given parameters
	 * @param field magnetic field
	 * @param xRange range of x axis
	 * @param yRange range of y axis
	 * @param zRange range of z axis
	 * @param gridPoints number of points to plot field over
	 * @param centerPoint center of plot
	 * @param viewDirection initial viewing direction
	 */
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
		this.currentMap = Maps.<DoubleVector, DoubleVector>newHashMap();
		computeCurrent();
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	/**
	 * Sets the x range of the plot
	 * @param xRange the new x-axis range
	 */
	public void setXRange(Pair<Double, Double> xRange) {
		this.xRange = xRange;
	}
	
	/**
	 * Sets the y range of the plot
	 * @param yRange the new y-axis range
	 */
	public void setYRange(Pair<Double, Double> yRange) {
		this.yRange = yRange;
	}
	
	/**
	 * Sets the z range of the plot
	 * @param zRange the new z-axis range
	 */
	public void setZRange(Pair<Double, Double> zRange) {
		this.zRange = zRange;
	}

	/**
	 * Displays the polywell data
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		
		drawAxes(g2);
		drawField(g2);
		drawCurrent(g2);
		drawPath(g2);
	}
	
	/**
	 * Draws the x, y, and z axis
	 * @param g graphics object to draw to
	 */
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
	
	/**
	 * Precomputes the current for rendering.
	 */
	public void computeCurrent() {
		CurrentDensityFunction f = field.getCurrentDensityFunction();
		double step = .5;
		for (double x = -8; x < 8; x += step) {
			for (double y = -8; y < 8; y += step) {
				for (double z = -8; z < 8; z += step) {
					DoubleVector v = new DoubleVector(x, y, z);
					System.out.println(v);
					currentMap.put(v, f.apply(v));
				}
			}
		}
	}
	
	/**
	 * Draws the current flow
	 * @param g the graphics object to draw to
	 */
	public void drawCurrent(Graphics2D g) {
		for (DoubleVector v : currentMap.keySet()) {
			drawVector(g, v, currentMap.get(v), Color.GREEN);
		}
	}
	
	/**
	 * Draws the given path of a particle inside the polywell
	 * @param g the graphics object to draw to
	 */
	public void drawPath(Graphics2D g) {
		List<DoubleVector> path = Lists.<DoubleVector>newArrayList();
		try {
			Scanner in = new Scanner(new File("hundredth2.path"));
			while (in.hasNextLine()) {
				Scanner line = new Scanner(in.nextLine());
				DoubleVector next = new DoubleVector(line.nextDouble(), line.nextDouble(), line.nextDouble());
				path.add(next);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < path.size() - 1; i++) {
			drawVector(g, path.get(i), path.get(i + 1).subtract(path.get(i)), Color.BLACK);
		}
	}
	
	/**
	 * Draws the given magnetic field
	 * @param g the graphics object to draw to
	 */
	public void drawField(Graphics2D g) {
		HashMap<DoubleVector, DoubleVector> vectors = field.getResults();
		
		for (DoubleVector v: vectors.keySet()) {
			drawVector(g, v, vectors.get(v), getColor(vectors.get(v)));
		}
	}
	
	/**
	 * Gets the color for the given vector (scaled from blue for small vectors to red for large vectors)
	 * @param v the given vector
	 * @return the color to draw the given vector
	 */
	public Color getColor(DoubleVector v) {
		double norm = v.norm();
		if (norm > MAX_COLOR_NORM) {
			norm = MAX_COLOR_NORM;
		}
		int blue = (int) ((1 - norm / MAX_COLOR_NORM) * 255);
		int red = (int) ((norm / MAX_COLOR_NORM) * 255);
		return new Color(red, 0, blue);
	}
	
	/**
	 * Draws the given vector from the given point with the given color
	 * @param g the graphics object to draw to
	 * @param o the origin point of the vector
	 * @param x the vector
	 * @param c color of the vector
	 */
	public void drawVector(Graphics2D g, DoubleVector o, DoubleVector x, Color c) {
		if (x.norm() == 0.0) {
			return;
		}
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

	/**
	 * Rotates the screen upon click and drag
	 */
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

	/**
	 * Stores initial position for rotation
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		clickPoint = new DoubleVector(event.getX(), event.getY());
	}

	@Override
	public void mouseReleased(MouseEvent event) {}
}
