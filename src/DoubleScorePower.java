import bagel.Image;

import java.util.Properties;

/**
 * A power-up that doubles coin score for a limited number of frames.
 */
public class DoubleScorePower extends Pickup {

    private final int durationFrames;

    /**
     * Creates a double-score power-up.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public DoubleScorePower(double x, double y, Properties gameProps) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.doubleScore.radius")),
                new Image(gameProps.getProperty("gameObjects.doubleScore.image")));
        this.durationFrames = Integer.parseInt(gameProps.getProperty("gameObjects.doubleScore.maxFrames"));
    }

    @Override
    protected void applyEffect(Player player) {
        player.activateDoubleScore(durationFrames);
    }
}
