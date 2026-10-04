import bagel.Image;

import java.util.Properties;

/**
 * Marks the goal position of a level. Scrolls with the rest of the world
 * like any other entity; reaching it (together with defeating the boss, on
 * level 3) wins the level.
 */
public class EndFlag extends Entity {

    /**
     * Creates the end flag.
     *
     * @param x         initial x coordinate
     * @param y         initial y coordinate
     * @param gameProps game configuration properties
     */
    public EndFlag(double x, double y, Properties gameProps) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.endFlag.radius")),
                new Image(gameProps.getProperty("gameObjects.endFlag.image")));
    }
}
