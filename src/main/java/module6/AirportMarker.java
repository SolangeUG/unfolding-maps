package module6;

import java.util.LinkedList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Solange U. Gasengayire
 *
 */
public class AirportMarker extends CommonMarker {

	// Routes connecting to this airportmarker
	private List<SimpleLinesMarker> routes = new LinkedList<>();
	private List<AirportMarker> destinations = new LinkedList<>();
	private Region region = null;

	/**
	 * Constructor
	 * @param city This aiport marker's city
	 */
	AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		this.setId(city.getId());
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		float red = region.getRed();
		float blue = region.getBlue();
		float green = region.getGreen();
		pg.fill(red, green, blue);
		pg.ellipse(x, y, 10, 10);
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		// show rectangle with title
		String name = getCity();
		String details;
		if (getCode() != null && ! getCode().isEmpty()) {
			details = getCode() + " | " + getCountry();
		} else {
			details = getCountry();
		}

		pg.pushStyle();

		pg.fill(254, 249, 231);
		pg.rect(-30, 500,
				Math.max(225, Math.max(pg.textWidth(name + 20), pg.textWidth(details + 48))), 50);

		pg.fill(255, 250, 240);
		pg.ellipse(23, 525, 35, 35);

		pg.fill(0, 0, 0);
		pg.textSize(12);
		pg.text(name, 48, 522);
		pg.text(details, 48, 534);

		pg.fill(0, 0, 0);
		pg.textSize(16);
		pg.text("info", 8, 530);

		pg.popStyle();

		// show routes
		for (SimpleLinesMarker route: routes) {
			route.setHidden(false);
		}
	}

	/**
	 * Return a list of this airport's routes
	 * @return the routes list
	 */
	public List<SimpleLinesMarker> getRoutes() {
		/* We could simply return our routes list, but that would be a security issue.
		 * That would mean exposing our internal, private implementation.
		 * So, instead, we return a copy of our routes list.
		 */
		return new LinkedList<>(routes);
	}

	/**
	 * Return a list of destinations from this airport
	 * @return the destinations list
	 */
	public List<AirportMarker> getDestinations() {
		/* Same thing here, returning our destinations list would be a security flaw.
		 * Because, that would expose our internal, private implementation.
		 * So, instead, we'll return a copy of our destinations list.
		 */
		return new LinkedList<>(destinations);
	}

	/**
	 * Add a route for this airport
	 * @param route The route to be added
	 * @return true if the operation succeeded
	 * 		   false otherwise
	 */
	public boolean addRoute(SimpleLinesMarker route) {
		boolean result = false;
		if (route != null) {
			result = routes.add(route);
		}
		return result;
	}

	/**
	 * Add a destination for this airport
	 * @param destination The destination to be added
	 * @return true if the operation succeeded
	 * 		   false otherwise
	 */
	public boolean addDestination(AirportMarker destination) {
		boolean result = false;
		if (destination != null) {
			result = destinations.add(destination);
		}
		return result;
	}

	@Override
	public void setSelected(boolean state) {
		super.setSelected(state);
		if (! this.isSelected()) {
			for (SimpleLinesMarker route: routes) {
				route.setHidden(true);
			}
		}
	}

	/**
	 * Return the airport code of this marker
	 * @return the airport code marker if it exists
	 * 		   or an empty string otherwise
	 */
	public String getCode() {
		String code = this.getStringProperty("code");
		if (code != null && ! code.isEmpty()) {
			code = code.substring(1, code.length() - 1);
		}
		return code;
	}

	/**
	 * Return the aiport's altitude
	 * @return the altitude
	 */
	public float getAltitude() {
		float altitude = 0;
		String altProp = this.getStringProperty("altitude");
		if (altProp != null && ! altProp.isEmpty()) {
			altitude = Float.valueOf(altProp);
		}
		return altitude;
	}

	/**
	 * Return the airport's city
	 * @return the city
	 */
	public String getCity() {
		String city = this.getStringProperty("city");
		if (city != null && ! city.isEmpty()) {
			city = city.substring(1, city.length() - 1);
		}
		return city;
	}

	/**
	 * Return the airport's country
	 * @return the country
	 */
	public String getCountry() {
		String country = this.getStringProperty("country");
		if (country != null && ! country.isEmpty()) {
			country = country.substring(1, country.length() - 1);
		}
		return country;
	}

	/**
	 * Return the airport region property
	 * @return the value of the mystery property if it exists
	 * 			or an empty string otherwise
	 */
	public String getRegionName() {
		return this.getStringProperty("region");
	}

	/**
	 * Return the distance between this object and a given location
	 * @param other The given location
	 * @return the distance to the input location
	 */
	public double distanceTo(Location other) {
		return this.location.getDistance(other);
	}

	/**
	 * Update this marker's region property
	 * @param region this marker's region
	 */
	public void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * Return this marker's region property
	 * @return the region
	 */
	public Region getRegion() {
		return this.region;
	}

	@Override
	public String toString() {
		return "{" + getCity() + "|" + getCountry() + "}";
	}
	
}
