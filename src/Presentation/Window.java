/* Created by andreea on 01/06/2020 */
package Presentation;

import Application.Controller;
import Config.Constants;

import javax.swing.*;

public class Window extends JFrame {

    public Window(Controller controller){
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setSize(Constants.DIM_WINDOW);
        this.setMinimumSize(Constants.DIM_WINDOW);
        this.setJMenuBar(createMenuBar());
        this.setVisible(true);
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        for (String menu : MenuBuilder.MENU_ITEMS_ORDER){
            JMenu newMenu = new JMenu(menu);
            for (String item : MenuBuilder.MAP_MENU_ITEMS.get(menu)){
                // If it's a submenu it's the Languages menu so we add the languages
                if (MenuBuilder.IS_SUBMENU.contains(item)){
                    JMenu subMenuItem = new JMenu(item);

                    newMenu.add(subMenuItem);
                } else {
                    JMenuItem menuItem = new JMenuItem(item);
                    menuItem.setIcon(new ImageIcon(MenuBuilder.MENU_ICONS.get(item)));
                    MenuBuilder.MENU_ITEMS.put(item, menuItem);
                    //menuItem.addActionListener(MenuBuilder.MENU_ACTIONLISTENERS.get(item));
                    newMenu.add(menuItem);
                }
                if (MenuBuilder.ADD_SEPARATION_AFTER.contains(item)){
                    newMenu.addSeparator();
                }
            }
            if (!MenuBuilder.IS_SUBMENU.contains(menu)){
                menuBar.add(newMenu);
            }
        }
        return menuBar;
    }

}
