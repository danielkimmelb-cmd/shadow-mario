import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Colour;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Represents one playable level: loads its layout from a CSV file, then
 * owns and updates every entity in it for as long as the level is being
 * played.
 */
public class Level {

    /** The outcome of a level at any point in time. */
    public enum Status {
        /** The level is still being played. */
        ONGOING,
        /** The player has met the level's win condition. */
        WON,
        /** The player has died. */
        LOST
    }

    private static final double BOSS_FIREBALL_DAMAGE_MULTIPLIER = 2.0;

    private final Properties gameProps;
    private final Properties messageProps;
    private final Image background;
    private final int windowWidth;
    private final int windowHeight;

    private final Font scoreFont;
    private final Font healthFont;
    private final Font bossHealthFont;
    private final Font instructionFont;

    private Player player;
    private final List<Platform> platforms = new ArrayList<>();
    private final List<FlyingPlatform> flyingPlatforms = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private EnemyBoss boss;
    private final List<Pickup> pickups = new ArrayList<>();
    private EndFlag endFlag;
    private final List<Fireball> playerFireballs = new ArrayList<>();
    private final List<Fireball> bossFireballs = new ArrayList<>();

    private boolean flagReached = false;
    private Status status = Status.ONGOING;

    /**
     * Loads a level from the given CSV file.
     *
     * @param csvFile      path to the level's CSV layout file
     * @param gameProps    game configuration properties
     * @param messageProps display text properties
     */
    public Level(String csvFile, Properties gameProps, Properties messageProps) {
        this.gameProps = gameProps;
        this.messageProps = messageProps;
        this.background = new Image(gameProps.getProperty("backgroundImage"));
        this.windowWidth = Integer.parseInt(gameProps.getProperty("windowWidth"));
        this.windowHeight = Integer.parseInt(gameProps.getProperty("windowHeight"));

        String font = gameProps.getProperty("font");
        this.scoreFont = new Font(font, Integer.parseInt(gameProps.getProperty("score.fontSize")));
        this.healthFont = new Font(font, Integer.parseInt(gameProps.getProperty("playerHealth.fontSize")));
        this.bossHealthFont = new Font(font, Integer.parseInt(gameProps.getProperty("enemyBossHealth.fontSize")));
        this.instructionFont = new Font(font, Integer.parseInt(gameProps.getProperty("instruction.fontSize")));

        loadCsv(csvFile);
    }

    private void loadCsv(String csvFile) {
        for (String[] row : IOUtils.readCsv(csvFile)) {
            String type = row[0];
            double x = Double.parseDouble(row[1]);
            double y = Double.parseDouble(row[2]);

            switch (type) {
                case "PLAYER":
                    player = new Player(x, y, gameProps);
                    break;
                case "PLATFORM":
                    platforms.add(new Platform(x, y, gameProps));
                    break;
                case "FLYING_PLATFORM":
                    FlyingPlatform flying = new FlyingPlatform(x, y, gameProps);
                    flyingPlatforms.add(flying);
                    break;
                case "ENEMY":
                    enemies.add(new Enemy(x, y, gameProps));
                    break;
                case "ENEMY_BOSS":
                    boss = new EnemyBoss(x, y, gameProps);
                    break;
                case "COIN":
                    pickups.add(new Coin(x, y, gameProps));
                    break;
                case "DOUBLE_SCORE":
                    pickups.add(new DoubleScorePower(x, y, gameProps));
                    break;
                case "INVINCIBLE_POWER":
                    pickups.add(new InvinciblePower(x, y, gameProps));
                    break;
                case "END_FLAG":
                    endFlag = new EndFlag(x, y, gameProps);
                    break;
                default:
                    // Unknown entity types are ignored.
                    break;
            }
        }
    }

    /**
     * Advances the level's simulation by one frame.
     *
     * @param input the current frame's input
     * @return the level's status after this frame has been processed
     */
    public Status update(Input input) {
        if (status != Status.ONGOING) {
            return status;
        }

        double dx = player.getWorldScrollForInput(input);

        // Move every world entity to its position for this frame first, so
        // that the player's landing check (and all collision checks below)
        // are made against up-to-date positions rather than last frame's -
        // otherwise the player ends up reacting one frame late to moving
        // platforms, which is visible as a sync/position mismatch.
        for (Platform platform : platforms) {
            platform.scroll(dx);
        }
        for (FlyingPlatform flying : flyingPlatforms) {
            flying.scroll(dx);
            flying.update();
        }
        for (Enemy enemy : enemies) {
            enemy.scroll(dx);
            enemy.update();
        }
        if (boss != null) {
            boss.scroll(dx);
            boss.update();
        }
        for (Pickup pickup : pickups) {
            pickup.scroll(dx);
            pickup.update();
        }
        if (endFlag != null) {
            endFlag.scroll(dx);
        }

        List<Platform> allPlatforms = new ArrayList<>(platforms);
        allPlatforms.addAll(flyingPlatforms);
        player.update(input, allPlatforms);

        // Now that everyone is in their final position for this frame,
        // resolve collisions between the (just-updated) player and everyone else.
        for (Enemy enemy : enemies) {
            enemy.checkCollision(player);
        }
        for (Pickup pickup : pickups) {
            if (pickup.collidesWith(player)) {
                pickup.collect(player);
            }
        }
        if (endFlag != null && endFlag.collidesWith(player)) {
            flagReached = true;
        }

        updateFireballs(input, dx);

        updateStatus();
        return status;
    }

    private void updateFireballs(Input input, double dx) {
        double activationRadius = boss == null ? 0
                : Double.parseDouble(gameProps.getProperty("gameObjects.enemyBoss.activationRadius"));

        if (boss != null && boss.isAlive() && input.wasPressed(Keys.S)) {
            double distance = Math.abs(player.getX() - boss.getX());
            if (distance < activationRadius) {
                boolean towardsRight = boss.getX() > player.getX();
                playerFireballs.add(new Fireball(player.getX(), player.getY(), towardsRight, gameProps));
            }
        }

        if (boss != null && boss.shouldAttack(player.getX())) {
            boolean towardsRight = player.getX() > boss.getX();
            double bossDamage = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damageSize"))
                    * BOSS_FIREBALL_DAMAGE_MULTIPLIER;
            bossFireballs.add(new Fireball(boss.getX(), boss.getY(), towardsRight, gameProps, bossDamage));
        }

        for (Fireball fireball : playerFireballs) {
            fireball.scroll(dx);
            fireball.update(windowWidth);
            if (boss != null && fireball.isActive() && boss.isAlive() && fireball.collidesWith(boss)) {
                fireball.hit(boss);
            }
        }

        for (Fireball fireball : bossFireballs) {
            fireball.scroll(dx);
            fireball.update(windowWidth);
            if (fireball.isActive() && fireball.collidesWith(player)) {
                fireball.hit(player);
            }
        }
    }

    private void updateStatus() {
        if (player.isDead() && player.hasFallenOffScreen(windowHeight)) {
            status = Status.LOST;
            return;
        }
        boolean bossDefeated = boss == null || boss.isDead();
        if (flagReached && bossDefeated) {
            status = Status.WON;
        }
    }

    /**
     * Renders the level's background, every active entity, and the HUD.
     */
    public void draw() {
        background.draw(windowWidth / 2.0, windowHeight / 2.0);

        for (Platform platform : platforms) {
            platform.draw();
        }
        for (FlyingPlatform flying : flyingPlatforms) {
            flying.draw();
        }
        for (Pickup pickup : pickups) {
            pickup.draw();
        }
        for (Enemy enemy : enemies) {
            enemy.draw();
        }
        if (boss != null) {
            boss.draw();
        }
        if (endFlag != null) {
            endFlag.draw();
        }
        for (Fireball fireball : playerFireballs) {
            fireball.draw();
        }
        for (Fireball fireball : bossFireballs) {
            fireball.draw();
        }
        player.draw();

        drawHud();
    }

    private void drawHud() {
        double scoreX = Double.parseDouble(gameProps.getProperty("score.x"));
        double scoreY = Double.parseDouble(gameProps.getProperty("score.y"));
        scoreFont.drawString(messageProps.getProperty("score") + player.getScore(), scoreX, scoreY);

        double healthX = Double.parseDouble(gameProps.getProperty("playerHealth.x"));
        double healthY = Double.parseDouble(gameProps.getProperty("playerHealth.y"));
        int healthPercent = (int) Math.round(player.getHealth() * 100);
        healthFont.drawString(messageProps.getProperty("health") + healthPercent, healthX, healthY);

        if (boss != null) {
            double bossX = Double.parseDouble(gameProps.getProperty("enemyBossHealth.x"));
            double bossY = Double.parseDouble(gameProps.getProperty("enemyBossHealth.y"));
            int bossPercent = (int) Math.round(boss.getHealth() * 100);
            bossHealthFont.drawString(messageProps.getProperty("health") + bossPercent, bossX, bossY,
                    new DrawOptions().setBlendColour(Colour.RED));

            double activationRadius = Double.parseDouble(gameProps.getProperty("gameObjects.enemyBoss.activationRadius"));
            if (boss.isAlive() && Math.abs(player.getX() - boss.getX()) < activationRadius) {
                String prompt = messageProps.getProperty("bossFireInstruction");
                double promptY = Double.parseDouble(gameProps.getProperty("instruction.y"));
                double promptX = (windowWidth - instructionFont.getWidth(prompt)) / 2.0;
                instructionFont.drawString(prompt, promptX, promptY);
            }
        }
    }
}
