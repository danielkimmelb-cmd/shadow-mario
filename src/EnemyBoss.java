import java.util.Properties;
import java.util.Random;

/**
 * The level 3 boss. Does not wander, has its own health pool, and
 * periodically fires fireballs at the player when they are far enough away.
 */
public class EnemyBoss extends Enemy implements Damageable {

    private static final int ATTACK_INTERVAL_FRAMES = 60;
    private static final double ATTACK_PROBABILITY = 0.7;
    private static final double DEATH_FALL_SPEED = 2.0;
    private static final double DAMAGE_RESISTANCE = 0.5;

    private static final Random RANDOM = new Random();

    private final double activationRadius;
    private double health;
    private int framesSinceLastAttackCheck = 0;
    private boolean dead = false;

    /**
     * Creates the boss.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public EnemyBoss(double x, double y, Properties gameProps) {
        super(x, y, gameProps, "enemyBoss");
        this.health = Double.parseDouble(gameProps.getProperty("gameObjects.enemyBoss.health"));
        this.activationRadius = Double.parseDouble(gameProps.getProperty("gameObjects.enemyBoss.activationRadius"));
    }

    /**
     * Advances the boss's death fall, if applicable.
     */
    @Override
    public void update() {
        if (dead) {
            setY(getY() + DEATH_FALL_SPEED);
        }
        // The boss does not wander, so no other per-frame motion is needed.
    }

    /**
     * Decides whether the boss should fire a fireball at the player this
     * frame: every {@value #ATTACK_INTERVAL_FRAMES} frames, while the
     * player is at or beyond the activation radius, there is a
     * {@value #ATTACK_PROBABILITY} chance of an attack.
     *
     * @param playerX the player's current x coordinate
     * @return true if the boss should fire a fireball this frame
     */
    public boolean shouldAttack(double playerX) {
        if (dead) {
            return false;
        }
        framesSinceLastAttackCheck++;
        if (framesSinceLastAttackCheck < ATTACK_INTERVAL_FRAMES) {
            return false;
        }
        framesSinceLastAttackCheck = 0;
        double distance = Math.abs(playerX - getX());
        return distance >= activationRadius && RANDOM.nextDouble() < ATTACK_PROBABILITY;
    }

    /**
     * Reduces health by only half the incoming damage, making the boss
     * noticeably tougher than a fireball's raw damage would suggest.
     */
    @Override
    public void takeDamage(double amount) {
        if (dead) {
            return;
        }
        health -= amount * DAMAGE_RESISTANCE;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    public boolean isDead() {
        return dead;
    }
}
