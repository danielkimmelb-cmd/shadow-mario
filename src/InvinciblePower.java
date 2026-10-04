import bagel.Image;

import java.util.Properties;

/**
 * A power-up that grants temporary immunity to enemy and fireball damage.
 */
public class InvinciblePower extends Pickup {

    private final int durationFrames;

    /**
     * Creates an invincibility power-up.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public InvinciblePower(double x, double y, Properties gameProps) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.invinciblePower.radius")),
                new Image(gameProps.getProperty("gameObjects.invinciblePower.image")));
        this.durationFrames = Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames"));
    }

    @Override
    protected void applyEffect(Player player) {
        player.activateInvincible(durationFrames);
    }
}
