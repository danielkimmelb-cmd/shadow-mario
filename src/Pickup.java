import bagel.Image;

/**
 * A collectible world object (coin or power-up). Once collected it applies
 * its effect exactly once, then floats upward off the screen before being
 * removed.
 */
public abstract class Pickup extends Entity {

    private static final double RISE_SPEED = 10.0;

    private boolean collected = false;

    /**
     * Creates a pickup.
     *
     * @param x      initial x coordinate
     * @param y      initial y coordinate
     * @param radius collision radius
     * @param image  image used to render the pickup
     */
    protected Pickup(double x, double y, double radius, Image image) {
        super(x, y, radius, image);
    }

    /**
     * Collects this pickup if it hasn't already been collected, applying
     * its effect to the player exactly once.
     *
     * @param player the player collecting this pickup
     */
    public void collect(Player player) {
        if (!collected) {
            collected = true;
            applyEffect(player);
        }
    }

    /**
     * Applies this pickup's effect to the player. Called exactly once,
     * the moment the pickup is collected.
     *
     * @param player the player collecting this pickup
     */
    protected abstract void applyEffect(Player player);

    /**
     * Once collected, rises off the top of the screen and then deactivates.
     */
    public void update() {
        if (collected) {
            setY(getY() - RISE_SPEED);
            if (getY() < -getRadius()) {
                deactivate();
            }
        }
    }
}
