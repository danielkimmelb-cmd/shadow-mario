import bagel.Image;

import java.util.Properties;

/**
 * A fireball projectile fired either by the player or by {@link EnemyBoss}.
 * Travels horizontally at a fixed speed, in whichever direction it was
 * launched, while also scrolling with the rest of the world.
 */
public class Fireball extends Entity {

    private final double speed;
    private final double damage;
    private final boolean movingRight;

    /**
     * Creates a fireball, using the default damage configured in
     * {@code app.properties}.
     *
     * @param x           the spawn x coordinate (the shooter's position)
     * @param y           the spawn y coordinate (the shooter's position)
     * @param movingRight true if the fireball should travel rightwards, false for leftwards
     * @param gameProps   game configuration properties
     */
    public Fireball(double x, double y, boolean movingRight, Properties gameProps) {
        this(x, y, movingRight, gameProps,
                Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damageSize")));
    }

    /**
     * Creates a fireball with an explicitly chosen damage amount, allowing
     * the boss's fireballs to hit harder than the player's.
     *
     * @param x           the spawn x coordinate (the shooter's position)
     * @param y           the spawn y coordinate (the shooter's position)
     * @param movingRight true if the fireball should travel rightwards, false for leftwards
     * @param gameProps   game configuration properties
     * @param damage      the amount of damage this fireball deals on impact
     */
    public Fireball(double x, double y, boolean movingRight, Properties gameProps, double damage) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius")),
                new Image(gameProps.getProperty("gameObjects.fireball.image")));
        this.speed = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.speed"));
        this.damage = damage;
        this.movingRight = movingRight;
    }

    /**
     * Advances the fireball's own projectile motion (independent of any
     * world scroll, which is still applied separately via {@link #scroll}).
     *
     * @param windowWidth the width of the game window, used to detect when
     *                    the fireball has left the screen
     */
    public void update(double windowWidth) {
        setX(getX() + (movingRight ? speed : -speed));
        if (getX() < 0 || getX() > windowWidth) {
            deactivate();
        }
    }

    public double getDamage() {
        return damage;
    }

    /**
     * Applies this fireball's damage to the given target and deactivates it.
     *
     * @param target the entity struck by this fireball
     */
    public void hit(Damageable target) {
        target.takeDamage(damage);
        deactivate();
    }
}
