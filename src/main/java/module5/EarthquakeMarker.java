package module5;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/**
 * Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public abstract class EarthquakeMarker extends CommonMarker {
	
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker.
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function based on magnitude.
	protected float radius;
	
	
	// Constants for distance
	private static final float kmPerMile = 1.6f;
	
	// Greater than or equal to this threshold is a moderate earthquake
	public static final float THRESHOLD_MODERATE = 5;

	// Greater than or equal to this threshold is a light earthquake
	public static final float THRESHOLD_LIGHT = 4;

	// Greater than or equal to this threshold is an intermediate depth
	private static final float THRESHOLD_INTERMEDIATE = 70;

	// Greater than or equal to this threshold is a deep depth
	private static final float THRESHOLD_DEEP = 300;

	// ADD constants for colors if you want

	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);

	/**
	 * Constructor
	 * @param feature this marker's point feature
	 */
	public EarthquakeMarker (PointFeature feature) {
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2 * magnitude );
		setProperties(properties);
		this.radius = 1.75f * getMagnitude();
	}

	/**
	 * Calls abstract method drawEarthquake,
	 * then checks age and draws X if needed.
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// IMPLEMENT: add X over marker if within past day		
		String age = getStringProperty("age");
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {
			
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x - (radius+buffer),
					y - (radius+buffer),
					x + radius+buffer,
					y + radius+buffer);
			pg.line(x - (radius+buffer),
					y + (radius+buffer),
					x + radius+buffer,
					y - (radius+buffer));
			
		}
		
		// reset to previous styling
		pg.popStyle();
		
	}

	/**
	 * Show the title of the earthquake if this marker is selected
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		// DONE: Implement this method
		pg.pushStyle();

		float offset = 5;
		pg.fill(0);
		pg.text(this.getTitle(), x + 2 * offset, y + offset);

		pg.popStyle();
	}

	/**
	 * Return the "threat circle" radius, or distance up to
	 * which this earthquake can affect things, for this earthquake.
	 * DISCLAIMER: this formula is for illustration purposes only
	 * 			   and is not intended to be used for safety-critical
	 * 			   or predictive applications.
	 * @return the threat cirle for this earthquake marker
	 */
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2 * getMagnitude() - 5);
		double km;
		km = (miles * kmPerMile);
		return km;
	}

	/**
	 * Determine the color of marker from the depth
	 * We use: Deep = red, intermediate = blue, shallow = yellow
	 * @param pg the graphics to draw on
	 */
	private void colorDetermine(PGraphics pg) {
		float depth = getDepth();
		
		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		}
		else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		}
		else {
			pg.fill(255, 0, 0);
		}
	}

	/**
	 * Magnitude getter
	 * @return this earthquake magnitude
	 */
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}

	/**
	 * Depth getter
	 * @return this earthquake depth
	 */
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}

	/**
	 * Title getter
	 * @return this marker's title
	 */
	public String getTitle() {
		return (String) getProperty("title");
	}

	/**
	 * Radius getter
	 * @return this marker's radius
	 */
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}

	/**
	 * OnLand getter
	 * @return true if this earthquake is on land
	 * 		   false otherwise
	 */
	public boolean isOnLand() {
		return isOnLand;
	}

}
