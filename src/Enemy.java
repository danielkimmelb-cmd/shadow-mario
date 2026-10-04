import bagel.Image;

import java.util.Properties;
import java.util.Random;

/**
 * A basic enemy that wanders back and forth around its spawn point while
 * also scrolling with the rest of the world in response to player input.
 * Deals damage to the player once per continuous contact (touching,
 * separating, then touching again deals damage again).
 */
public class Enemy extends Entity {

    private static final Random RANDOM = new Random();

    private final double damageAmount;
    private final double maxDisplacement;
    private final double wanderSpeed;

    private double baseX;
    private double wanderOffset = 0;
    private int direction;
    private boolean currentlyTouching = false;

    /**
     * Creates an enemy, reading its image and stats from {@code gameProps}.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public Enemy(double x, double y, Properties gameProps) {
        this(x, y, gameProps, "enemy");
    }

    /**
     * Protected constructor allowing subclasses (e.g. {@link EnemyBoss}) to
     * reuse this class' construction logic under a different property prefix.
     */
    protected Enemy(double x, double y, Properties gameProps, String propertyPrefix) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects." + propertyPrefix + ".radius")),
                new Image(gameProps.getProperty("gameObjects." + propertyPrefix + ".image")));
        this.damageAmount = propertyPrefix.equals("enemy")
                ? Double.parseDouble(gameProps.getProperty("gameObjects.enemy.damageSize"))
                : 0;
        this.maxDisplacement = propertyPrefix.equals("enemy")
                ? Double.parseDouble(gameProps.getProperty("gameObjects.enemy.maxRandomDisplacementX"))
                : 0;
        this.wanderSpeed = propertyPrefix.equals("enemy")
                ? Double.parseDouble(gameProps.getProperty("gameObjects.enemy.randomSpeed"))
                : 0;
        this.baseX = x;
        this.direction = RANDOM.nextBoolean() ? 1 : -1;
    }

    @Override
    public void scroll(double dx) {
        baseX += dx;
        setX(baseX + wanderOffset);
    }

    /**
     * Advances the enemy's own back-and-forth wandering motion. Subclasses
     * that don't wander (e.g. the boss) may override this to do nothing.
     */
    public void update() {
        if (maxDisplacement <= 0) {
            return;
        }
        wanderOffset += direction * wanderSpeed;
        if (wanderOffset > maxDisplacement || wanderOffset < -maxDisplacement) {
            direction = -direction;
            wanderOffset += direction * wanderSpeed;
        }
        setX(baseX + wanderOffset);
    }

    /**
     * Damages the player once per continuous contact: the first frame the
     * player touches this enemy applies damage, and no more damage is
     * dealt until the player separates and touches it again.
     *
     * @param player the player to check contact against and damage
     */
    public void checkCollision(Player player) {
        boolean touching = collidesWith(player);
        if (touching && !currentlyTouching) {
            player.takeDamage(damageAmount);
        }
        currentlyTouching = touching;
    }
}
