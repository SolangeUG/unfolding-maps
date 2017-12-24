package module3;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;


/**
 * EarthquakeCityMap
 *
 * An application with an interactive map displaying earthquake data.
 * Co-author: UC San Diego Intermediate Software Development MOOC team
 *
 * @author Solange U. Gasengayire
 *
 */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// If you are working offline, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	private static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	private static final float THRESHOLD_LIGHT = 4;

	// The map
	private UnfoldingMap map;


	public void setup() {

		int canvasWidth = 1520;
		int canvasHeight = 900;
		size(canvasWidth, canvasHeight, OPENGL);

		String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

		if (offline) {
			String mbTilesString = "blankLight-1-3.mbtiles";
			map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		} else {
			int mapX = 120, mapY = 50;
			int mapWidth = 1680, mapHeight = 800;
			AbstractMapProvider provider;
			provider = new Microsoft.RoadProvider();
			//provider = new OpenStreetMap.OpenStreetMapProvider();

			map = new UnfoldingMap(this, mapX, mapY, mapWidth, mapHeight, provider);
			// If you want to test with a local file, uncomment the next line
			// earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<>();

	    // Use provided parser to collect properties for each earthquake
	    // PointFeatures have a getLocation method
		List<PointFeature> earthquakes;
		try {
			earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		} catch (Exception e) {
			earthquakesURL = "2.5_week.atom";
			earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		}

	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
			System.out.println(f.getLocation() + " | " + mag);
	    }
	    
	    // DONE: Add code here as appropriate
		for (PointFeature feature: earthquakes) {
			Marker marker = createMarker(feature);
			markers.add(marker);
		}
		map.addMarkers(markers);

	}

	/**
	 * Helper method that returns a SimplePointMaker
	 * for an earthquake feature received as parameter
	 * @param feature The earthquake feature to build a marker for
	 * @return the point marker for the feature parameter
	 */
	private SimplePointMarker createMarker(PointFeature feature) {
		// DONE: finish implementing and use this method, if it helps.

		int color = color(255, 0, 0);
		int radius = 15;
		float magnitude = getMagnitude(feature);
		if (magnitude < THRESHOLD_LIGHT) {
			color = color(0, 0, 255);
			radius = 5;
		} else if (magnitude < THRESHOLD_MODERATE) {
			color = color(255, 255, 0);
			radius = 10;
		}

		SimplePointMarker marker = new SimplePointMarker(
				feature.getLocation(), feature.getProperties()
		);
		marker.setRadius(radius);
		marker.setColor(color);
		return marker;
	}

	/**
	 * Draw a map and its markers
	 */
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	/**
	 * Helper method to draw the key in the GUI
	 * DONE: Implement this method to draw the key
	 */
	private void addKey() {
		// Remember you can use Processing's graphics methods here

		// Key canvas
		fill(color(255, 253, 231));
		float keyX = 100, keyY = 220;
		float keyWidth = 250, keyHeight = 500;
		rect(keyX, keyY, keyWidth, keyHeight);

		// Key title
		fill(0, 102, 153, 204);
		textSize(24);
		text("Earthquake Key", 130, 260);

		// Legend for red markers
		fill(255, 0, 0, 204);
		ellipse(145, 320, 20, 20);
		fill(0, 0, 0, 204);
		textSize(16);
		text("5.0+ Magnitude", 170, 328);

		// Legend for yellow markers
		fill(255, 255, 0, 204);
		ellipse(145, 400, 15, 15);
		fill(0, 0, 0, 204);
		textSize(16);
		text("4.0+ Magnitude", 170, 408);

		// Legend for blue markers
		fill(0, 0, 255, 204);
		ellipse(145, 480, 10, 10);
		fill(0, 0, 0, 204);
		textSize(16);
		text("< 4.0 Magnitude", 170, 488);

	}

	/**
	 * Return the magnitude from a point feature properties
	 * @param feature The point feature to search from
	 * @return the magnitude from its properties
	 */
	private float getMagnitude(PointFeature feature) {
		Object magObj = feature.getProperty("magnitude");
		float magnitude;
		magnitude = Float.parseFloat(magObj.toString());
		return magnitude;
	}
}
