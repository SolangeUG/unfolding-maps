package module5;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */

// DONE: Change SimplePointMarker to CommonMarker as the very first thing you do in module 5.
// I.E: CityMarker extends CommonMarker. It will cause an error. That's what's expected.
public class CityMarker extends CommonMarker {

	// The size of the triangle marker
	public static int TRI_SIZE = 5;

	/**
	 * Constructor
	 * @param location this city's location
	 */
	public CityMarker(Location location) {
		super(location);
	}

	/**
	 * Constructor
	 * @param city this city's point feature
	 */
	CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 30, 30);
		pg.triangle(x, y-TRI_SIZE, x - TRI_SIZE, y + TRI_SIZE, x + TRI_SIZE, y + TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	}

	/**
	 * Show the title of the city if this marker is selected
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	public void showTitle(PGraphics pg, float x, float y) {
		// DONE: Implement this method
		pg.pushStyle();

		int offset = 5;
		String title = this.getCountry()
				+ " | " + this.getCity()
				+ " | " + this.getPopulation();
		pg.fill(0);
		pg.text(title, x + offset, y + offset);

		pg.popStyle();

	}

	/**
	 * City name getter
	 * @return the name of the city
	 */
	public String getCity() {
		return getStringProperty("name");
	}

	/**
	 * Country name getter
	 * @return the name of this city's country
	 */
	public String getCountry() {
		return getStringProperty("country");
	}

	/**
	 * Population getter
	 * @return this city's population count
	 */
	public float getPopulation() {
		return Float.parseFloat(getStringProperty("population"));
	}
}
