import bagel.Image;

import java.util.Properties;

/**
 * A coin that increases the player's score when collected.
 */
public class Coin extends Pickup {

    private final int value;

    /**
     * Creates a coin.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public Coin(double x, double y, Properties gameProps) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius")),
                new Image(gameProps.getProperty("gameObjects.coin.image")));
        this.value = Integer.parseInt(gameProps.getProperty("gameObjects.coin.value"));
    }

    @Override
    protected void applyEffect(Player player) {
        player.addScore(value);
    }
}
