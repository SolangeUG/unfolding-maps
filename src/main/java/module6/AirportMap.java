package module6;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An applet that shows airports (and routes) on a world map.
 *
 * @author Adam Setters
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public class AirportMap extends PApplet {

	// The world map
	private UnfoldingMap map;

	// List for airport markers
	private List<Marker> airportList = new LinkedList<>();

	// List for route markers
	private List<Marker> routeList = new LinkedList<>();

	// Hashmap for quicker access when matching airports with routes
	// Airports will be put in this hashmap with OpenFlights unique id for key
	private HashMap<Integer, Location> airports = new HashMap<>();

	// List of all the world regions
	private List<Region> regions = new LinkedList<>();

	// Map of minimum distance per region
	private HashMap<String, Integer> limits = new HashMap<>();

	// Selected, highlighted region
	private Region selectedRegion = null;

	// Selected, highlighted airport marker
	private AirportMarker selectedMarker = null;
	
	public void setup() {

		// setting up the applet
		int canvasWidth = 1360, canvasHeight = 920;
		size(canvasWidth, canvasHeight, OPENGL);

		// setting up map and default events
		int mapX = 50, mapY = 55;
		int mapWidth = 1480, mapHeight = 800;
		AbstractMapProvider provider = new Microsoft.RoadProvider();

		map = new UnfoldingMap(this, mapX, mapY, mapWidth, mapHeight, provider);
		MapUtils.createDefaultEventDispatcher(this, map);

		// initialize the minimum distance per region
		this.initLimitsPerRegion();

		// parse airport data
		this.loadAirportData();

		// parse route data
		this.loadRouteData();

		// limit/filter the number of airports per region
		List<Marker> filtered = new LinkedList<>();
		for (Region region : regions) {
			List<Marker> markers = filterAirports(region.getName());
			filtered.addAll(markers);
		}
		airportList = filtered;

		// Uncomment if you want to see all routes displayed on the map
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}

	/**
	 * The draw() method is called directly after setup(), and it is
	 * continuously executed until the program is stopped.
	 * It is called automatically, so it should never be called explicitly.
	 */
	public void draw() {
        int bkgColor = color(66, 73, 73); //color(220, 220, 220);
        this.background(bkgColor);
        this.map.draw();
        this.drawRegions();
        this.addKey();
	}

	/**
	 * Working with mouse events
	 * Highlight marker when applicable
	 */
	public void mouseMoved() {
		if (selectedMarker != null) {
			selectedMarker.setSelected(false);
			selectedMarker = null;
			for (Marker marker : airportList) {
				marker.setHidden(false);
			}
		}

		for (Marker marker : airportList) {
			AirportMarker airport = (AirportMarker) marker;
			if (airport.isInside(map, mouseX, mouseY)) {
				selectedMarker = airport;
				selectedMarker.setSelected(true);
				hideAirports();
			} else {
				airport.setSelected(false);
			}
		}
	}

	/**
	 * When the user hovers on an airport marker,
	 * hide all the airports that are not in this airport's destination list.
	 */
	private void hideAirports() {
		for (Marker marker : airportList) {
			marker.setHidden(true);
		}
		if (selectedMarker.getDestinations().size() > 0) {
			for (AirportMarker airport : selectedMarker.getDestinations()) {
				airport.setHidden(false);
			}
			selectedMarker.setHidden(false);
		}
	}

	/**
	 * Working with mouse events
	 * Highlight map and airports features by region
	 */
	public void mouseReleased() {
		for (Region region: regions) {
			if (mouseX > region.getXPosition()
					&& mouseX < region.getXPosition() + region.getXSize()
					&& mouseY > region.getYPosition()
					&& mouseY < region.getYPosition() + region.getYSize()) {
				hideMarkers(region);
				break;
			}
		}

	}

	/**
	 * Hide markers that are not in the provided region
	 * @param region The requested region
	 */
	private void hideMarkers(Region region) {
		if (region.equals(selectedRegion)) {
			for (Marker marker : airportList) {
				AirportMarker airport = (AirportMarker) marker;
				airport.setHidden(false);
			}
			selectedRegion = null;
		} else {
			for (Marker marker : airportList) {
				AirportMarker airport = (AirportMarker) marker;
				if (! airport.getRegion().equals(region)) {
					airport.setHidden(true);
				} else {
					airport.setHidden(false);
				}
			}
			selectedRegion = region;
		}
	}


	/**
	 * Load airport data from the airports.dat file,
	 * and initialize airportList as well as the airports hashmap variables.
	 */
	private void loadAirportData() {

		// Get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");

		// Retrieve all the regions from the parsed data
		List<String> regionNames = ParseFeed.parseRegions(features);
		Collections.sort(regionNames);
		this.initRegions(regionNames);

		// Create airport markers from (filtered) features
		for(PointFeature feature : features) {
			AirportMarker marker = new AirportMarker(feature);
			// We're only adding airports that have a valid region property
			if (marker.getRegionName() != null) {
				for (Region region: regions) {
					if (marker.getRegionName().equals(region.getName())) {
						marker.setRegion(region);
						break;
					}
				}
				airportList.add(marker);
				airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			}
		}
	}

	/**
	 * Load route data from the routes.dat file
	 * and initialize the routeList instance variable.
	 */
	private void loadRouteData() {

		// Get features from route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");

		// Create route markers from features
		for(ShapeFeature route : routes) {

			// Get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int destination = Integer.parseInt((String)route.getProperty("destination"));

			// Get locations for airports on route
			if (airports.containsKey(source) && airports.containsKey(destination)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(destination));
			}

			// Create the corresponding marker (hidden by default)
			SimpleLinesMarker routeMarker = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			routeMarker.setColor(color(52, 73, 94));
			routeMarker.setHidden(true);
			routeList.add(routeMarker);

			this.addRouteToAirport(routeMarker);
		}

	}

	/**
	 * Add a given route to an airport list of routes
	 * @param route The route to be added
	 */
	private void addRouteToAirport(SimpleLinesMarker route) {
		String source = route.getStringProperty("source");
		for (Marker marker : airportList) {
			AirportMarker airport = (AirportMarker) marker;
			if (source.equals(airport.getId())) {
				airport.addRoute(route);
				AirportMarker dest = getDestination(route);
				if (dest != null) {
					airport.addDestination(dest);
				}
				break;
			}
		}
	}

	/**
	 * Init regions from a list of region names
	 * @param regionList The list of region names
	 */
	private void initRegions(List<String> regionList) {
		float xPos = 280, yPos = 15;
		float gap = 15, xSize = 28, ySize = 28;

		for (String name: regionList) {
			Region region = new Region(xPos, yPos, xSize, ySize, name);
			float[] colors = getColorsForRegion(name);
			region.setRed(colors[0]);
			region.setGreen(colors[1]);
			region.setBlue(colors[2]);

			regions.add(region);
			xPos += xSize + gap;
		}
	}

	/**
	 * Add regions' "buttons" to the map
	 * This will help us highlight
	 */
	private void drawRegions() {
		for (Region region: regions) {

			float red = region.getRed();
			float blue = region.getBlue();
			float green = region.getGreen();

			float xSize = region.getXSize();
			float ySize = region.getYSize();
			float xPos = region.getXPosition();
			float yPos = region.getYPosition();
			String code = region.getInitials();

			fill(red, green, blue);
			rect(xPos, yPos, xSize, ySize);

			fill(0, 0, 0);
			textSize(12);
			text(code, xPos + 5, yPos + 10);
		}
	}

	/**
	 * Helper method to add key
	 */
	private void addKey() {

		fill(255, 250, 240);
		rect(30, 100, 215, 440);
		//rect(30, 100, 215, 530);

		fill(0, 0, 0);
		textSize(18);
		textAlign(LEFT, CENTER);
		text("AIRPORT DATA KEY", 50, 125);

		fill(0, 0, 0);
		textSize(12);
		text("Regions", 50, 165);

		float x = 50;
		float y = 185;
		float gap = 35;

		for (Region region : regions) {
			float red = region.getRed();
			float blue = region.getBlue();
			float green = region.getGreen();
			float xSize = region.getXSize();
			float ySize = region.getYSize();

			String name = region.getName();
			String code = region.getInitials();

			fill(red, green, blue);
			rect(x, y, xSize, ySize);

			fill(0, 0, 0);
			textSize(12);
			text(code, x + 5, y + 12);
			text(name, x + xSize + 5, y+ 12);

			y += gap;
		}

	}

	/**
	 * Filter the list of airport markers based on their region location
	 * And limit the resulting list to airport markers that are at least minDistance distant
	 * @param region The requested region
	 * @return The filtered list
	 */
	private List<Marker> filterAirports(String region) {
		List<Marker> airports
				= airportList
                    .stream()
                    .filter(f -> region.equals(f.getStringProperty("region")))
                    .collect(Collectors.toList());

		int count = 0;
		int limit = limits.get(region);
		List<Marker> markers = new LinkedList<>();

		for (Marker marker : airports) {
			AirportMarker airport = (AirportMarker) marker;
			if (count < limit) {
				if (! markers.contains(airport)) {
					markers.add(airport);
					count++;

					for (AirportMarker dest : airport.getDestinations()) {
						if (count >= limit) {
							break;
						} else {
							if (! markers.contains(dest)) {
								markers.add(dest);
								count++;
							}
						}
					}
				}
			}
		}
		return markers;
	}

	/**
	 * Return the destination airport from a given route
	 * @param route The requested route
	 * @return The destination airport
	 */
	private AirportMarker getDestination(SimpleLinesMarker route) {
		AirportMarker destination = null;
		String dest = route.getStringProperty("destination");
		for (Marker marker : airportList) {
			AirportMarker airport = (AirportMarker) marker;
			if (dest.equals(airport.getId())) {
				destination = airport;
				break;
			}
		}
		return destination;
	}

	/**
	 * Initialize the minimum distances map
	 */
	private void initLimitsPerRegion() {
		limits.put("Africa", 500);		// Total: 702
		limits.put("America", 2000);	// Total: 3163
		limits.put("Antarctica", 10);	// Total: 19
		limits.put("Arctic", 7);		// Total: 7
		limits.put("Asia", 1000);		// Total: 1544
		limits.put("Atlantic", 40);		// Total: 63
		limits.put("Australia", 100);	// Total: 252
		limits.put("Europe", 1000);		// Total: 1813
		limits.put("Indian", 50);		// Total: 113
		limits.put("Pacific", 200);		// Total: 339
	}

	/**
	 * Return a color per region
	 * @param name The region name
	 * @return an array of colors for the input region
	 */
	private float[] getColorsForRegion(String name) {

		float[] colors = new float[3];
		float red, green, blue;

		switch (name) {
			case "Africa":
				red = 69; green = 179; blue = 157;
				break;

			case "America":
				red = 139; green = 196; blue = 74;
				break;

			case "Antarctica":
				red = 102; green = 255; blue = 0;
				break;

			case "Arctic":
				red = 102; green = 255; blue = 0;
				break;

			case "Asia":
				red = 46; green = 134; blue = 193;
				break;

			case "Atlantic":
				red = 255; green = 255; blue = 0;
				break;

			case "Australia":
				red = 0; green = 0; blue = 255;
				break;

			case "Europe":
				red = 231; green = 76; blue = 60;
				break;

			case "Indian":
				red = 255; green = 0; blue = 255;
				break;

			case "Pacific":
				red = 241; green = 196; blue = 15;
				break;

			default:
				red = 255; green = 255; blue = 255;
				break;
		}

		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;
		return colors;
	}
	

}
