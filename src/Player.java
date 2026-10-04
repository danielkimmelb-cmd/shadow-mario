import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.List;
import java.util.Properties;

/**
 * The player-controlled character.
 *
 * <p>Unlike every other {@link Entity}, the player's on-screen x position
 * never changes: pressing left/right instead scrolls the rest of the world
 * in the opposite direction (see {@link Level}). The player only moves
 * vertically, to support jumping and falling.</p>
 */
public class Player extends Entity implements Damageable {

    private static final double JUMP_VELOCITY = -20.0;
    private static final double GRAVITY = 1.0;
    private static final double DEATH_FALL_SPEED = 2.0;

    private final Image imageLeft;
    private final Image imageRight;
    private final double moveSpeed;

    private double health;
    private int score = 0;
    private double velocityY = 0;
    private boolean facingRight = true;
    private boolean dead = false;

    private int doubleScoreFrames = 0;
    private int invincibleFrames = 0;

    /**
     * Creates the player at the given spawn position.
     *
     * @param x         the spawn x coordinate (this never changes afterwards)
     * @param y         the spawn y coordinate
     * @param gameProps game configuration properties
     */
    public Player(double x, double y, Properties gameProps) {
        super(x, y,
                Double.parseDouble(gameProps.getProperty("gameObjects.player.radius")),
                new Image(gameProps.getProperty("gameObjects.player.imageRight")));
        this.imageRight = getImage();
        this.imageLeft = new Image(gameProps.getProperty("gameObjects.player.imageLeft"));
        this.health = Double.parseDouble(gameProps.getProperty("gameObjects.player.health"));
        this.moveSpeed = Double.parseDouble(gameProps.getProperty("gameObjects.platform.speed"));
    }

    /**
     * The player never scrolls - it is the rest of the world that moves.
     *
     * @param dx ignored
     */
    @Override
    public void scroll(double dx) {
        // Intentionally does nothing: the player is fixed on screen.
    }

    /**
     * @return the amount the rest of the world should scroll this frame,
     *         based on which arrow key (if any) is currently held down
     */
    public double getWorldScrollForInput(Input input) {
        if (dead) {
            return 0;
        }
        if (input.isDown(Keys.RIGHT)) {
            facingRight = true;
            return -moveSpeed;
        } else if (input.isDown(Keys.LEFT)) {
            facingRight = false;
            return moveSpeed;
        }
        return 0;
    }

    /**
     * Updates jumping/falling physics and power-up timers for this frame.
     *
     * @param input     the current frame's input
     * @param platforms every platform (including flying platforms) the player could land on
     */
    public void update(Input input, List<Platform> platforms) {
        if (doubleScoreFrames > 0) {
            doubleScoreFrames--;
        }
        if (invincibleFrames > 0) {
            invincibleFrames--;
        }

        if (dead) {
            setY(getY() + DEATH_FALL_SPEED);
            return;
        }

        double previousY = getY();
        double predictedY = previousY + velocityY;

        // While falling (or resting), check for a platform below to land on.
        // The check uses both the previous and predicted position so that a
        // fast fall can't skip straight through a platform's landing zone
        // in a single frame.
        boolean onGround = false;
        if (velocityY >= 0) {
            for (Platform platform : platforms) {
                if (platform.willLandOn(this, previousY, predictedY)) {
                    onGround = true;
                    setY(platform.getLandingY(this));
                    velocityY = 0;
                    break;
                }
            }
        }

        if (!onGround) {
            setY(predictedY);
        }

        if (onGround && input.wasPressed(Keys.UP)) {
            velocityY = JUMP_VELOCITY;
        } else if (!onGround) {
            velocityY += GRAVITY;
        }

        if (health <= 0) {
            dead = true;
        }
    }

    @Override
    public void draw() {
        (facingRight ? imageRight : imageLeft).draw(getX(), getY());
    }

    @Override
    public void takeDamage(double amount) {
        if (invincibleFrames > 0 || dead) {
            return;
        }
        health -= amount;
        if (health < 0) {
            health = 0;
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

    /**
     * @return true once the player has died and finished falling off screen
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @return true if the player has fallen far enough below the window to end the game
     */
    public boolean hasFallenOffScreen(double windowHeight) {
        return getY() > windowHeight;
    }

    public int getScore() {
        return score;
    }

    /**
     * Awards coin points, doubled while the double-score power is active.
     *
     * @param value the base point value of the coin
     */
    public void addScore(int value) {
        score += doubleScoreFrames > 0 ? value * 2 : value;
    }

    /** Activates the double-score power-up for the given duration. */
    public void activateDoubleScore(int frames) {
        doubleScoreFrames = frames;
    }

    /** Activates the invincibility power-up for the given duration. */
    public void activateInvincible(int frames) {
        invincibleFrames = frames;
    }

    public boolean isInvincible() {
        return invincibleFrames > 0;
    }

    public boolean isFacingRight() {
        return facingRight;
    }
}
