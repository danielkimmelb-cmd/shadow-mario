import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/**
 * The screen shown after a level ends, reporting whether the player won
 * or lost, and returning to the start screen when space is pressed.
 */
public class EndScreen {

    private final Properties gameProps;
    private final Properties messageProps;
    private final int windowWidth;
    private final Font messageFont;

    /**
     * Creates the end screen.
     *
     * @param gameProps    game configuration properties
     * @param messageProps display text properties
     */
    public EndScreen(Properties gameProps, Properties messageProps) {
        this.gameProps = gameProps;
        this.messageProps = messageProps;
        this.windowWidth = Integer.parseInt(gameProps.getProperty("windowWidth"));
        this.messageFont = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("message.fontSize")));
    }

    /**
     * Draws the win or loss message and checks whether the player pressed
     * space to return to the start screen.
     *
     * @param input the current frame's input
     * @param won   true to show the win message, false to show the loss message
     * @return true if space was just pressed, requesting a return to the start screen
     */
    public boolean update(Input input, boolean won) {
        double messageY = Double.parseDouble(gameProps.getProperty("message.y"));
        String text = messageProps.getProperty(won ? "gameWon" : "gameOver");
        String[] lines = text.split("\n");
        double lineSpacing = Integer.parseInt(gameProps.getProperty("message.fontSize")) + 10;

        for (int i = 0; i < lines.length; i++) {
            double x = (windowWidth - messageFont.getWidth(lines[i])) / 2.0;
            messageFont.drawString(lines[i], x, messageY + i * lineSpacing);
        }

        return input.wasPressed(Keys.SPACE);
    }
}
