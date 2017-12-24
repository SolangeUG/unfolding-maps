package module4;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * Implements a visual marker for cities on an earthquake map
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public class CityMarker extends SimplePointMarker {
	
	// The size of the triangle marker
	// It's a good idea to use this variable in your draw method
	public static final int TRI_SIZE = 5;  
	
	public CityMarker(Location location) {
		super(location);
	}

	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	}
	
	
	// HINT: pg is the graphics object on which you call the graphics methods.
	// E.g. pg.fill(255, 0, 0) will set the color to red x and y are the center of the object to draw.
	// They will be used to calculate the coordinates to pass into any shape drawing methods.
	// E.g. pg.rect(x, y, 10, 10) will draw a 10x10 square whose upper left corner is at position x, y.

	/**
	 * Implementation of method to draw marker on the map.
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	public void draw(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// DONE: Add code to draw a triangle to represent the CityMarker
		pg.fill(0, 255, 0);
		float a = 8;
		pg.triangle(x, y, x - a, y + a, x + a, y + a);
		
		// Restore previous drawing style
		pg.popStyle();
	}

	/**
	 * City name getter
	 * @return city name
	 */
	public String getCity() {
		return getStringProperty("name");
	}

	/**
	 * Country name getter
	 * @return country name
	 */
	public String getCountry() {
		return getStringProperty("country");
	}

	/**
	 * Population getter
	 * @return population
	 */
	public float getPopulation() {
		return Float.parseFloat(getStringProperty("population"));
	}
	
}
