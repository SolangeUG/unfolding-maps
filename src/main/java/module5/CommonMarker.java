package module5;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * Implements a common marker for cities and earthquakes on an earthquake map.
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public abstract class CommonMarker extends SimplePointMarker {

	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	

	/**
	 * Clicked property getter
	 * @return true if this marker is clicked
	 * 		   false otherwise
	 */
	public boolean getClicked() {
		return clicked;
	}

	/**
	 * Clicked property setter
	 * @param state the clicked value
	 */
	public void setClicked(boolean state) {
		clicked = state;
	}

	/**
	 * Draw method implementation
	 * @param pg the graphics to draw on
	 * @param x the x position coordinate
	 * @param y the y position coordinate
	 */
	public void draw(PGraphics pg, float x, float y) {
		// Common piece of drawing method for markers.
		// Note that you should implement this by making calls to
		// drawMarker and showTitle, which are abstract methods
		// implemented in subclasses.

		// For starter code just drawMaker(...)
		if (!hidden) {
			drawMarker(pg, x, y);
			if (selected) {
				showTitle(pg, x, y);  // You will implement this in the subclasses
			}
		}
	}

	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
}