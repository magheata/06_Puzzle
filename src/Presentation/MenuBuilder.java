/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */

package Presentation;

import Application.Controller;
import Config.Constants;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Class that helps build the menu
 */
public class MenuBuilder {

    public static Controller controller;

    public MenuBuilder(Controller controller) {
        this.controller = controller;
    }

    public static final String[] MENU_ITEMS_ORDER = new String[]{
            Constants.TEXT_PUZZLE_MENU,
            Constants.TEXT_TOOLS_MENU
    };

    public static final ArrayList<String> ADD_SEPARATION_AFTER = new ArrayList<>(Arrays.asList(
            Constants.TEXT_SOLVE_ITEM
    ));

    public static final Map<String, ArrayList<String>> MAP_MENU_ITEMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_PUZZLE_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_NEW_PUZZLE_ITEM);
                    add(Constants.TEXT_IMPORT_PUZZLE_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_TOOLS_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_SOLVE_ITEM);
                    add(Constants.TEXT_SHUFFLE_ITEM);
                    add(Constants.TEXT_ENABLE_HINTS_ITEM);
                }
            }
            )
    );

    public static final Map<String, String> MENU_ICONS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_PUZZLE_ITEM, Constants.PATH_NEW_PUZZLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_IMPORT_PUZZLE_ITEM, Constants.PATH_IMPORT_PUZZLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHUFFLE_ITEM, Constants.PATH_SHUFFLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_HINTS_ITEM, Constants.PATH_DISABLED_HINTS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_HINTS_ITEM, Constants.PATH_ENABLED_HINTS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SOLVE_ITEM, Constants.PATH_SOLVE_ICON)
    );

    public static final Map<String, JMenuItem> MENU_ITEMS = new HashMap<>();


    public static final Map<String, ActionListener> MENU_ACTIONLISTENERS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_PUZZLE_ITEM, e -> {
                controller.setImageUsed(false);
                controller.resetBoard();
                controller.prepare(null);
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_IMPORT_PUZZLE_ITEM, e -> controller.importImage()),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHUFFLE_ITEM, e -> controller.shuffleTiles()),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SOLVE_ITEM, e -> controller.solvePuzzle()),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_HINTS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).getText().equals(Constants.TEXT_ENABLE_HINTS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setText(Constants.TEXT_DISABLE_HINTS_ITEM);
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_DISABLE_HINTS_ITEM)));

                } else {
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setText(Constants.TEXT_ENABLE_HINTS_ITEM);
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_ENABLE_HINTS_ITEM)));
                }
                controller.toggleHints();
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_HINTS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_DISABLE_HINTS_ITEM).getText().equals(Constants.TEXT_DISABLE_HINTS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_HINTS_ITEM).setText(Constants.TEXT_ENABLE_HINTS_ITEM);
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_ENABLE_HINTS_ITEM)));
                } else {
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_HINTS_ITEM).setText(Constants.TEXT_DISABLE_HINTS_ITEM);
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_DISABLE_HINTS_ITEM)));
                }
                controller.toggleHints();
            })
    );
}
