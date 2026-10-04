import bagel.Image;

import java.util.Properties;

/**
 * The ground platform. A single wide image spans the whole level, so the
 * player is considered supported whenever it is resting at the correct
 * height above the platform's centre line - no horizontal bounds check
 * is required.
 */
public class Platform extends Entity {

    private final double initialX;

    /**
     * Creates a ground platform.
     *
     * @param x         initial x coordinate (also the rightward-scroll boundary)
     * @param y         initial y coordinate (the platform's vertical centre line)
     * @param gameProps game configuration properties
     */
    public Platform(double x, double y, Properties gameProps) {
        this(x, y, new Image(gameProps.getProperty("gameObjects.platform.image")));
    }

    /**
     * Creates a platform with an explicitly supplied image, allowing
     * subclasses (e.g. {@link FlyingPlatform}) to use their own artwork.
     *
     * @param x     initial x coordinate
     * @param y     initial y coordinate
     * @param image the image used to render this platform
     */
    protected Platform(double x, double y, Image image) {
        super(x, y, 0, image);
        this.initialX = x;
    }

    /**
     * The ground cannot be scrolled further right than its starting
     * position - this stops the player from scrolling the world past the
     * very beginning of the level.
     *
     * @param dx the requested horizontal displacement
     */
    @Override
    public void scroll(double dx) {
        if (dx > 0 && getX() >= initialX) {
            return;
        }
        super.scroll(dx);
    }

    /**
     * @return the y coordinate of the walkable surface of this platform
     */
    protected double getSurfaceY() {
        return getY() - getImage().getHeight() / 2.0;
    }

    /**
     * Determines whether the player will land on this platform during a
     * move from {@code previousY} to {@code predictedY}. Uses both
     * endpoints (rather than just the final position) so that a fast fall
     * can't skip straight through the landing zone in one frame.
     *
     * @param player      the player to test
     * @param previousY   the player's y coordinate before this frame's move
     * @param predictedY  the player's y coordinate this frame's move would produce
     * @return true if the player should be treated as landing on this platform
     */
    public boolean willLandOn(Player player, double previousY, double predictedY) {
        double landingY = getLandingY(player);
        return predictedY + player.getRadius() >= getSurfaceY() && previousY <= landingY + 1;
    }

    /**
     * @param player the player landing on this platform
     * @return the y coordinate the player's centre should be set to when landed
     */
    public double getLandingY(Player player) {
        return getSurfaceY() - player.getRadius();
    }
}
