package module6;

/**
 * Class that represents a region on the world map.
 *
 * @author Solange U. Gasengayire
 *
 */
public class Region {

    private float xPos;
    private float yPos;
    private float xSize;
    private float ySize;

    private float red = 255;
    private float green = 255;
    private float blue = 255;

    private String name;

    /**
     * Constructor
     * @param x the x position coordinate
     * @param y the y position coordinate
     * @param sizeX the x size of this region
     * @param sizeY the y size of this region
     * @param rName the name of this region
     */
    Region(float x, float y, float sizeX, float sizeY, String rName) {
        this.xPos = x;
        this.yPos = y;
        this.xSize = sizeX;
        this.ySize = sizeY;
        this.name = rName;
    }

    /**
     * Return this region's x position coordinate
     * @return the x position
     */
    public float getXPosition() {
        return xPos;
    }

    /**
     * Set this region's x position coordinate
     * @param xPos the x position
     */
    public void setXPosition(float xPos) {
        this.xPos = xPos;
    }

    /**
     * return this region's y position coordinate
     * @return the y position
     */
    public float getYPosition() {
        return yPos;
    }

    /**
     * Set this region's y position coordinate
     * @param yPos the y position
     */
    public void setYPosition(float yPos) {
        this.yPos = yPos;
    }

    /**
     * Return this region's x size
     * @return the x size
     */
    public float getXSize() {
        return xSize;
    }

    /**
     * Set this region's x size
     * @param xSize the x size
     */
    public void setXSize(float xSize) {
        this.xSize = xSize;
    }

    /**
     * Return this region's y size
     * @return the y size
     */
    public float getYSize() {
        return ySize;
    }

    /**
     * Set this region's y size
     * @param ySize the y size
     */
    public void setYSize(float ySize) {
        this.ySize = ySize;
    }

    /**
     * Return this region's red component of its color
     * @return the red value
     */
    public float getRed() {
        return red;
    }

    /**
     * Set this region's red component of its color
     * @param red the red value
     */
    public void setRed(float red) {
        this.red = red;
    }

    /**
     * Return this region's green component of its color
     * @return the green value
     */
    public float getGreen() {
        return green;
    }

    /**
     * Set this region's green component of its color
     * @param green the green value
     */
    public void setGreen(float green) {
        this.green = green;
    }

    /**
     * Return this region's blue component of its color
     * @return the blue value
     */
    public float getBlue() {
        return blue;
    }

    /**
     * Set this region's blue component of its color
     * @param blue the blue value
     */
    public void setBlue(float blue) {
        this.blue = blue;
    }

    /**
     * Return this region's name
     * @return the name of this region
     */
    public String getName() {
        return name;
    }

    /**
     * Set this region's name value
     * @param rName the name value
     */
    public void setName(String rName) {
        this.name = rName;
    }

    /**
     * Return this region's initials
     * @return the initials
     */
    public String getInitials() {
        return this.name.substring(0, 2).toUpperCase();
    }
}
