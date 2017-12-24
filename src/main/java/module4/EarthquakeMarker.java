package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public abstract class EarthquakeMarker extends SimplePointMarker {
	
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// SimplePointMarker has a field "radius" which is inherited by Earthquake marker:
	// protected float radius;
	//
	// You will want to set this in the constructor, either using the thresholds below,
	// or a continuous function based on magnitude.

	// Greater than or equal to this threshold is a moderate earthquake
	static final float THRESHOLD_MODERATE = 5;

	// Greater than or equal to this threshold is a light earthquake
	static final float THRESHOLD_LIGHT = 4;

	// Greater than or equal to this threshold is an intermediate depth
	static final float THRESHOLD_INTERMEDIATE = 70;

	// Greater than or equal to this threshold is a deep depth
	static final float THRESHOLD_DEEP = 300;

	// abstract method implemented in derived classes

	/**
	 * Draw marker abstract method
	 * It is implemented in derived classes
	 * @param pg the graphics to draw on
	 * @param x the x posiion coordinate
	 * @param y the y position coordinate
	 */
	public abstract void drawEarthquake(PGraphics pg, float x, float y);


	/**
	 * Constructor
	 * @param feature point feature to create a marker for
	 */
	public EarthquakeMarker (PointFeature feature) {
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2 * magnitude);
		setProperties(properties);
		this.radius = 1.75f * getMagnitude();
	}

	/**
	 * Calls abstract method drawEarthquake, checks age and draws X if needed
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	public void draw(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// OPTIONAL DONE: draw X over marker if within past day
		ageDetermine(pg, x, y);

		// reset to previous styling
		pg.popStyle();
		
	}

	/**
	 * Determine the color of marker from depth
	 * Suggestion: Deep = red, intermediate = blue, shallow = yellow
	 * @param pg the graphics to draw on
	 */
	private void colorDetermine(PGraphics pg) {
		// DONE: Implement this method
		float depth = getDepth();
		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		} else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		} else {
			pg.fill(255, 0, 0);
		}
	}

	/**
	 * Determine age of marker
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	private void ageDetermine(PGraphics pg, float x, float y) {
		String age = (String) this.getProperty("age");
		if ("Past Hour".equals(age)) {
			pg.pushStyle();

			pg.fill(0);
			pg.textSize(18);
			pg.text("X", x - 4, y + 8);

			pg.popStyle();
		}
	}
	

	/**
	 * Magnitude getter
	 * @return the magnitude
	 */
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}

	/**
	 * Depth getter
	 * @return the depth
	 */
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}

	/**
	 * Title getter
	 * @return the title
	 */
	public String getTitle() {
		return (String) getProperty("title");	
		
	}

	/**
	 * Radius getter
	 * @return the radius
	 */
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}

	/**
	 * OnLand getter
	 * @return true if the marker is on land
	 * 		   false otherwise
	 */
	public boolean isOnLand() {
		return isOnLand;
	}
	
}
