import bagel.Font;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/**
 * The game's title screen. Displays the title and instructions, and lets
 * the player pick a level to start by pressing 1, 2 or 3.
 */
public class StartScreen {

    private final Properties gameProps;
    private final Properties messageProps;
    private final int windowWidth;

    private final Font titleFont;
    private final Font instructionFont;
    private final int instructionFontSize;

    /**
     * Creates the start screen.
     *
     * @param gameProps    game configuration properties
     * @param messageProps display text properties
     */
    public StartScreen(Properties gameProps, Properties messageProps) {
        this.gameProps = gameProps;
        this.messageProps = messageProps;
        this.windowWidth = Integer.parseInt(gameProps.getProperty("windowWidth"));

        String font = gameProps.getProperty("font");
        this.titleFont = new Font(font, Integer.parseInt(gameProps.getProperty("title.fontSize")));
        this.instructionFontSize = Integer.parseInt(gameProps.getProperty("instruction.fontSize"));
        this.instructionFont = new Font(font, instructionFontSize);
    }

    /**
     * Draws the title screen and checks whether a level was selected.
     *
     * @param input the current frame's input
     * @return 1, 2 or 3 if that level's key was just pressed, otherwise 0
     */
    public int update(Input input) {
        double titleX = Double.parseDouble(gameProps.getProperty("title.x"));
        double titleY = Double.parseDouble(gameProps.getProperty("title.y"));
        titleFont.drawString(messageProps.getProperty("title"), titleX, titleY);

        double instructionY = Double.parseDouble(gameProps.getProperty("instruction.y"));
        String[] lines = messageProps.getProperty("instruction").split("\n");
        double lineSpacing = instructionFontSize + 10;
        for (int i = 0; i < lines.length; i++) {
            double x = (windowWidth - instructionFont.getWidth(lines[i])) / 2.0;
            instructionFont.drawString(lines[i], x, instructionY + i * lineSpacing);
        }

        if (input.wasPressed(Keys.NUM_1)) {
            return 1;
        } else if (input.wasPressed(Keys.NUM_2)) {
            return 2;
        } else if (input.wasPressed(Keys.NUM_3)) {
            return 3;
        }
        return 0;
    }
}
