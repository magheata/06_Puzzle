/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Config;

import Domain.ShiftDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Constants used in the project
 */
public class Constants {

    public static int PAUSE_DURATION = 300;

    public static int DEFAULT_BOARD_SIZE = 3;

    public static int WIDTH_WINDOW = 600;
    public static int HEIGHT_WINDOW = 675;
    public static int WIDTH_BOARD = 600;
    public static int HEIGHT_BOARD = 600;

    public static int TILE_SIZE = WIDTH_BOARD / DEFAULT_BOARD_SIZE;

    public static Dimension DIM_WINDOW = new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW);
    public static Dimension DIM_BOARD = new Dimension(WIDTH_BOARD, HEIGHT_BOARD);

    public static String TEXT_PUZZLE_MENU = "Puzzle";
    public static String TEXT_NEW_PUZZLE_ITEM = "New...";
    public static String TEXT_IMPORT_PUZZLE_ITEM = "Import image...";

    public static String TEXT_TOOLS_MENU = "Tools";
    public static String TEXT_SOLVE_ITEM = "Solve puzzle";
    public static String TEXT_SHUFFLE_ITEM = "Shuffle tiles";

    public static String TEXT_ENABLE_HINTS_ITEM = "Enable hints";
    public static String TEXT_DISABLE_HINTS_ITEM = "Disable hints";

    public final static String PATH_NEW_PUZZLE_ICON = "src/Presentation/Images/new.png";
    public final static String PATH_IMPORT_PUZZLE_ICON = "src/Presentation/Images/import.png";
    public final static String PATH_SOLVE_ICON = "src/Presentation/Images/solve.png";
    public final static String PATH_SHUFFLE_ICON = "src/Presentation/Images/shuffle.png";
    public final static String PATH_ENABLED_HINTS_ICON = "src/Presentation/Images/hint.png";
    public final static String PATH_DISABLED_HINTS_ICON = "src/Presentation/Images/disabled_hint.png";

    public static final Color FG_COLOR = new Color(0xFFFFFF);
    public static final Color BG_COLOR = new Color(0x3B5998);

    public static final ArrayList<ShiftDirection> POSSIBLE_MOVES = new ArrayList<>(Arrays.asList(
            ShiftDirection.UP,
            ShiftDirection.DOWN,
            ShiftDirection.LEFT,
            ShiftDirection.RIGHT));
}
