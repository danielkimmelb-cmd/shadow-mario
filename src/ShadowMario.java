import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;

import java.util.Properties;

/**
 * Entry point and top-level state machine for the Shadow Mario game.
 * Switches between the start screen, the three playable levels, and the
 * end screen, as appropriate.
 */
public class ShadowMario extends AbstractGame {

    private enum State {
        START,
        PLAYING,
        END
    }

    private final Properties gameProps;
    private final Properties messageProps;

    private final StartScreen startScreen;
    private final EndScreen endScreen;

    private State state = State.START;
    private Level currentLevel;
    private boolean lastLevelWon = false;

    /**
     * The constructor.
     *
     * @param gameProps    game configuration properties
     * @param messageProps display text properties
     */
    public ShadowMario(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("windowWidth")),
                Integer.parseInt(gameProps.getProperty("windowHeight")),
                messageProps.getProperty("title"));

        this.gameProps = gameProps;
        this.messageProps = messageProps;
        this.startScreen = new StartScreen(gameProps, messageProps);
        this.endScreen = new EndScreen(gameProps, messageProps);
    }

    /**
     * The entry point for the program.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowMario game = new ShadowMario(game_props, message_props);
        game.run();
    }

    /**
     * Performs a state update: closes the window on escape, and otherwise
     * delegates to whichever screen is currently active.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        switch (state) {
            case START:
                int chosenLevel = startScreen.update(input);
                if (chosenLevel != 0) {
                    startLevel(chosenLevel);
                }
                break;
            case PLAYING:
                Level.Status status = currentLevel.update(input);
                currentLevel.draw();
                if (status == Level.Status.WON) {
                    lastLevelWon = true;
                    state = State.END;
                } else if (status == Level.Status.LOST) {
                    lastLevelWon = false;
                    state = State.END;
                }
                break;
            case END:
                if (endScreen.update(input, lastLevelWon)) {
                    state = State.START;
                }
                break;
            default:
                break;
        }
    }

    private void startLevel(int levelNumber) {
        String csvFile = gameProps.getProperty("level" + levelNumber + "File");
        currentLevel = new Level(csvFile, gameProps, messageProps);
        state = State.PLAYING;
    }
}
