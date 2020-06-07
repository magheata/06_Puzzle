/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */

package Presentation;

import Application.Controller;
import Config.Constants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static Presentation.MenuBuilder.MENU_ICONS;
import static Presentation.MenuBuilder.MENU_ITEMS;

public class Window extends JFrame {

    private Board board;
    private MenuBuilder menuBuilder;
    private JOptionPane messageOptionPane;
    private JProgressBar progressBar;
    private JLabel hintLabel;

    public Window(Controller controller){
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setSize(Constants.DIM_WINDOW);
        this.setMinimumSize(Constants.DIM_WINDOW);
        this.setJMenuBar(createMenuBar());
        this.setVisible(true);
        board = new Board(controller);
        board.setBorder(new EmptyBorder(0, 0, 0, 0));
        menuBuilder = new MenuBuilder(controller);
        messageOptionPane = new JOptionPane();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
        controlPanel.setMinimumSize(new Dimension(this.getWidth(), 30));
        controlPanel.setPreferredSize(new Dimension(this.getWidth(), 30));

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Constants.BG_COLOR);
        progressBar.setForeground(Constants.BG_COLOR);

        hintLabel = new JLabel("Next move: ");
        hintLabel.setIcon(new ImageIcon(Constants.PATH_ENABLED_HINTS_ICON));
        hintLabel.setVisible(false);

        controlPanel.add(progressBar);
        controlPanel.add(hintLabel);

        this.setLayout(new BorderLayout());
        this.add(board, BorderLayout.NORTH);
        this.add(controlPanel);
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        for (String menu : MenuBuilder.MENU_ITEMS_ORDER){
            JMenu newMenu = new JMenu(menu);
            newMenu.setOpaque(true);
            for (String item : MenuBuilder.MAP_MENU_ITEMS.get(menu)){
                // If it's a submenu it's the Languages menu so we add the languages
                JMenuItem menuItem = new JMenuItem(item);
                menuItem.setIcon(new ImageIcon(MENU_ICONS.get(item)));
                MENU_ITEMS.put(item, menuItem);
                menuItem.addActionListener(MenuBuilder.MENU_ACTIONLISTENERS.get(item));
                newMenu.add(menuItem);
                if (MenuBuilder.ADD_SEPARATION_AFTER.contains(item)){
                    newMenu.addSeparator();
                }
            }
            menuBar.add(newMenu);
        }

        MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setEnabled(false);
        MENU_ITEMS.get(Constants.TEXT_SOLVE_ITEM).setEnabled(false);

        return menuBar;
    }

    public void showDialog(String text, String title, int type){
        messageOptionPane.showMessageDialog(this.getContentPane(), text, title, type);
    }

    public void startProgressBar(){
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
    }

    public void stopProgressBar(){
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    public void showHint(boolean show){
        hintLabel.setVisible(show);
        if (show){
            MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setText(Constants.TEXT_DISABLE_HINTS_ITEM);
            MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_DISABLE_HINTS_ITEM)));

        } else {
            MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setText(Constants.TEXT_ENABLE_HINTS_ITEM);
            MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setIcon(new ImageIcon(MENU_ICONS.get(Constants.TEXT_ENABLE_HINTS_ITEM)));
        }
    }

    public void editHint(String tile){
        hintLabel.setText("Next move:  " + tile);
    }

    public void enableHint(boolean enabled){
        MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).setEnabled(enabled);
    }

    public void enableSolve(boolean enabled){
        MENU_ITEMS.get(Constants.TEXT_SOLVE_ITEM).setEnabled(enabled);
    }

    public boolean isHintEnabled(){
        return MENU_ITEMS.get(Constants.TEXT_ENABLE_HINTS_ITEM).isEnabled();
    }

    public boolean isSolveEnabled(){
        return MENU_ITEMS.get(Constants.TEXT_SOLVE_ITEM).isEnabled();
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
