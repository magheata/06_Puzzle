/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Presentation;

import Application.Controller;
import Config.Constants;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.util.*;

public class MenuBuilder {

    public static Controller controller;

    public MenuBuilder(Controller controller) {
        this.controller = controller;
    }

    public static final String[] MENU_ITEMS_ORDER = new String[]{
            Constants.TEXT_PUZZLE_MENU,
            Constants.TEXT_TOOLS_MENU,
            Constants.TEXT_PREFERENCES_MENU
    };

    public static final ArrayList<String> IS_SUBMENU = new ArrayList<>(Arrays.asList(Constants.TEXT_ALGORITHM_ITEM));

    public static final ArrayList<String> ADD_SEPARATION_AFTER = new ArrayList<>(Arrays.asList(
            Constants.TEXT_RESET_ITEM,
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
                    add(Constants.TEXT_RESET_ITEM);
                    add(Constants.TEXT_ENABLE_HINTS_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_PREFERENCES_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_ALGORITHM_ITEM);
                }
            })
    );

    public static final Map<String, String> MENU_ICONS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_PUZZLE_ITEM, Constants.PATH_NEW_PUZZLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_IMPORT_PUZZLE_ITEM, Constants.PATH_IMPORT_PUZZLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHUFFLE_ITEM, Constants.PATH_SHUFFLE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_RESET_ITEM, Constants.PATH_RESET_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_HINTS_ITEM, Constants.PATH_DISABLED_HINTS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_HINTS_ITEM, Constants.PATH_ENABLED_HINTS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SOLVE_ITEM, Constants.PATH_SOLVE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ALGORITHM_ITEM, Constants.PATH_ALGORITHM_ICON)
    );

    public static final Map<String, JMenuItem> MENU_ITEMS = new HashMap<>();


    /*
    public static final Map<String, ActionListener> MENU_ACTIONLISTENERS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_UNDO_ITEM, undoAction),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_REDO_ITEM, redoAction),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FILE_ITEM, e -> controller.resetNotepad(true)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FROM_EXISTING_ITEM, e -> controller.openFileChooser(true)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_OPEN_FILE_ITEM, e -> controller.openFileChooser(false)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).getText().equals(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM);
                }
                controller.toggleSuggestions();
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).getText().equals(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM);
                }
                controller.toggleSuggestions();
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SPELLING_ITEM, e -> controller.correctSpellingFromText()),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_WORD_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.enableFindPanel(true);
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_REPLACE_WORD_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.enableFindReplacePanel(true);
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_HIDE_PANEL_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).getText().equals(Constants.TEXT_HIDE_PANEL_ITEM)){
                        MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).setText(Constants.TEXT_SHOW_PANEL_ITEM);
                        controller.enableSidebarPanel(false);
                    } else {
                        MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).setText(Constants.TEXT_HIDE_PANEL_ITEM);
                        controller.enableSidebarPanel(true);
                    }
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHOW_PANEL_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).getText().equals(Constants.TEXT_SHOW_PANEL_ITEM)){
                        MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).setText(Constants.TEXT_HIDE_PANEL_ITEM);
                        controller.enableSidebarPanel(true);
                    } else {
                        MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).setText(Constants.TEXT_SHOW_PANEL_ITEM);
                        controller.enableSidebarPanel(false);
                    }
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_EDIT_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).getText().equals(Constants.TEXT_ENABLE_EDIT_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).setText(Constants.TEXT_DISABLE_EDIT_ITEM);
                    controller.enableNotepad(true);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).setText(Constants.TEXT_ENABLE_EDIT_ITEM);
                    controller.enableNotepad(false);
                    controller.checkText();
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_EDIT_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).getText().equals(Constants.TEXT_ENABLE_EDIT_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).setText(Constants.TEXT_DISABLE_EDIT_ITEM);
                    controller.enableNotepad(true);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).setText(Constants.TEXT_ENABLE_EDIT_ITEM);
                    controller.enableNotepad(false);
                    controller.checkText();
                }
            })
    );*/
}
