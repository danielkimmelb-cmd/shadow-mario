import bagel.Image;

import java.util.Properties;
import java.util.Random;

/**
 * A platform that floats in mid-air. In addition to scrolling with the
 * rest of the world it wanders back and forth around its spawn point.
 *
 * <p>Its walkable surface and horizontal footprint are derived directly
 * from the actual platform image, exactly like the ground platform - this
 * keeps the collision box lined up with what is actually drawn on screen,
 * rather than relying on separately configured half-length/half-height
 * values that may not exactly match the artwork.</p>
 */
public class FlyingPlatform extends Platform {

    private static final Random RANDOM = new Random();

    private final double halfLength;
    private final double maxDisplacement;
    private final double wanderSpeed;

    private double baseX;
    private double wanderOffset = 0;
    private int direction;

    /**
     * Creates a flying platform.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public FlyingPlatform(double x, double y, Properties gameProps) {
        super(x, y, new Image(gameProps.getProperty("gameObjects.flyingPlatform.image")));
        this.halfLength = getImage().getWidth() / 2.0;
        this.maxDisplacement = Double.parseDouble(gameProps.getProperty("gameObjects.flyingPlatform.maxRandomDisplacementX"));
        this.wanderSpeed = Double.parseDouble(gameProps.getProperty("gameObjects.flyingPlatform.randomSpeed"));
        this.baseX = x;
        this.direction = RANDOM.nextBoolean() ? 1 : -1;
    }

    /**
     * A flying platform is never blocked at a scroll boundary - only the
     * ground platform enforces that. Its base anchor simply tracks the
     * cumulative world scroll.
     */
    @Override
    public void scroll(double dx) {
        baseX += dx;
        setX(baseX + wanderOffset);
    }

    /**
     * Advances the platform's own back-and-forth wandering motion. Should
     * be called once per frame regardless of player input.
     */
    public void update() {
        wanderOffset += direction * wanderSpeed;
        if (wanderOffset > maxDisplacement || wanderOffset < -maxDisplacement) {
            direction = -direction;
            wanderOffset += direction * wanderSpeed;
        }
        setX(baseX + wanderOffset);
    }

    /**
     * Landing requires the player to also be within the platform's width
     * horizontally, on top of the usual vertical crossing check inherited
     * from {@link Platform#willLandOn}.
     */
    @Override
    public boolean willLandOn(Player player, double previousY, double predictedY) {
        double dx = Math.abs(player.getX() - getX());
        return dx < halfLength && super.willLandOn(player, previousY, predictedY);
    }
}
