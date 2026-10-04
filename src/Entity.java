import bagel.Image;

/**
 * Abstract base class for every object that lives in the game world
 * (as opposed to screen-level UI text).
 *
 * <p>An Entity has a position, a circular collision radius, an image and
 * an active flag. Circular (centre-distance) collision is used throughout
 * the game because every entity in {@code app.properties} is described by
 * a single radius value.</p>
 */
public abstract class Entity {

    private double x;
    private double y;
    private final double radius;
    private final Image image;
    private boolean active = true;

    /**
     * Creates a new entity.
     *
     * @param x      the initial x coordinate of the entity's centre
     * @param y      the initial y coordinate of the entity's centre
     * @param radius the collision radius of the entity
     * @param image  the image used to render the entity
     */
    protected Entity(double x, double y, double radius, Image image) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.image = image;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    protected void setX(double x) {
        this.x = x;
    }

    protected void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public Image getImage() {
        return image;
    }

    /**
     * @return true while this entity should still be updated and rendered
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Marks this entity as no longer part of the game (removed next frame).
     */
    protected void deactivate() {
        active = false;
    }

    /**
     * Renders the entity at its current position, if active.
     */
    public void draw() {
        if (active) {
            image.draw(x, y);
        }
    }

    /**
     * Applies the world scroll caused by the player's horizontal input.
     * The player itself stays fixed on screen, so every other entity is
     * translated instead to create the illusion of the player moving.
     *
     * @param dx the horizontal displacement to apply this frame
     */
    public void scroll(double dx) {
        setX(getX() + dx);
    }

    /**
     * Circular collision test based on the distance between entity centres.
     *
     * @param other the other entity to test against
     * @return true if the two entities' circles overlap and both are active
     */
    public boolean collidesWith(Entity other) {
        if (!isActive() || !other.isActive()) {
            return false;
        }
        double dx = getX() - other.getX();
        double dy = getY() - other.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (getRadius() + other.getRadius());
    }
}
