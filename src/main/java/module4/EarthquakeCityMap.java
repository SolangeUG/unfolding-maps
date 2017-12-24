package module4;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
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
 * @author Solange U. Gasengayire
 *
 */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods).
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings.
	private static final long serialVersionUID = 1L;

	// If you are working offline, change the value of this variable to true
	private static final boolean offline = false;

	// Feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL =
			"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	// The map
	private UnfoldingMap map;

	// The earthquakes
	private List<PointFeature> earthquakes;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		// size(900, 700, OPENGL);

		size(1360, 900, OPENGL);
		if (offline) {
			String mbTilesString = "blankLight-1-3.mbtiles";
			map = new UnfoldingMap(this, 200, 50, 650, 600,
					new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		} else {
			AbstractMapProvider provider = new Microsoft.RoadProvider();
			// GOOGLE provider doesn't always work!
			// provider = new Google.GoogleMapProvider();

			map = new UnfoldingMap(this, 50, 50, 1480, 800, provider);
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}

		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		earthquakesURL = "quiz1.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		String countryFile = "countries.geo.json";
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		String cityFile = "city-data.json";
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		List<Marker> cityMarkers = new ArrayList<>();
		for (Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		List<Marker> quakeMarkers = new ArrayList<>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  } else { // OceanQuakes
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.
	    //           They are used for their geometric properties.
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}

	/**
	 * Helper method to draw key in GUI
	 * DONE: Update this method as appropriate
	 */
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		// DONE: Update this method as appropriate
		fill(255, 250, 240);
		rect(25, 50, 150, 300);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);

		// City Marker
		fill(color(0, 255, 0));
		triangle(55, 125, 62, 115, 70, 125);

		// LandQuake Marker
		fill(color(255, 255, 255));
		ellipse(63, 145, 15, 15);

		// OceanQuake Marker
		fill(color(255, 255, 255));
		rect(55, 165, 15, 15);

		// Low Magnitude
		fill(color(255, 255, 0));
		ellipse(63, 240, 15, 15);

		// Average Magnitude
		fill(color(0, 0, 255));
		ellipse(63, 270, 15, 15);

		// High Magnitude
		fill(color(255, 0, 0));
		ellipse(63, 300, 15, 15);

		fill(0, 0, 0);
		textSize(12);
		text("City Marker", 85, 120);
		text("Land Quake", 85, 145);
		text("Ocean Quake", 85, 170);
		text("Size ~ Magnitude", 50, 210);

		text("Shallow", 85, 240);
		text("Intermediate", 85, 270);
		text("Deep", 85, 300);
	}

	/**
	 * Determine whether the earthquake occurred on land.
	 * @param earthquake the feature to check
	 * @return true if it is on lonad
	 * 		   false otherwise
	 */
	private boolean isLand(PointFeature earthquake) {
		// If this quake occurred on land, set the "country" property of its
		// PointFeature to the country where it occurred and return true.
		// Notice that the helper method isInCountry will set this "country" property already.
		// Otherwise, return false.

		// DONE: implement this method using the helper method isInCountry
		// DONE: loop over all countries to check if location is in any of them
		for (Marker country: countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}

		// Not inside any country
		return false;
	}

	/**
	 * Print countries with number of earthquakes
	 */
	private void printQuakes() {
		// You will want to loop through the country markers or country features
		// (either will work) and then for each country, loop through
		// the quakes to count how many occurred in that country.
		// Recall that the country markers have a "name" property,
		// and LandQuakeMarkers have a "country" property set.

		// DONE: Implement this method
		int earthquakesCount = 0;
		System.out.println();

		for (Marker country: countryMarkers) {
			String name = (String) country.getProperty("name");
			for (PointFeature quake: earthquakes) {
				String quakeCountry = (String) quake.getProperty("country");
				if (name.equals(quakeCountry)) {
					earthquakesCount++;
				}
			}
			if (earthquakesCount > 0) {
				System.out.println(name + ": " + earthquakesCount);
			}
			earthquakesCount = 0;
		}

		int quakesInOcean = 0;
		for (PointFeature quake: earthquakes) {
			if (! isLand(quake)) {
				quakesInOcean++;
			}
		}
		System.out.println("Ocean quakes: " + quakesInOcean);
	}

	/**
	 * Determine whether a given earthquake is in a given country
	 * @param earthquake the earthquake feature to check
	 * @param country the coutnry marker
	 * @return true if the feature is in the country
	 * 		   false otherwise
	 */
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// This will add the country property to the properties of the earthquake
		// feature if it's in one of the countries.
		// You should not have to modify this code.

		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
