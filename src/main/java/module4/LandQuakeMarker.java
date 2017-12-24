package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/**
 * Implements a visual marker for land earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public class LandQuakeMarker extends EarthquakeMarker {

	/**
	 * Constructor
	 * @param quake point feature to create a marker for
	 */
	LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}


	/**
	 * Draw marker
	 * @param pg the graphics to draw on
	 * @param x the x posiion coordinate
	 * @param y the y position coordinate
	 */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// Draw a centered circle for land quakes
		// DO NOT set the fill color here.
		// That will be set in the EarthquakeMarker class to indicate the depth of the earthquake.
		// Simply draw a centered circle.
		
		// HINT: Notice the radius variable in the EarthquakeMarker class
		// and how it is set in the EarthquakeMarker constructor.
		
		// DONE: Implement this method
		int radius = 20;
		if (getDepth() < THRESHOLD_INTERMEDIATE) {
			radius = 10;
		} else if (getDepth() < THRESHOLD_DEEP) {
			radius = 15;
		}
		pg.ellipse(x, y, radius, radius);
		
	}

	/**
	 * Return the country this earthquake is in
	 * @return country name
	 */
	public String getCountry() {
		return (String) getProperty("country");
	}
		
}