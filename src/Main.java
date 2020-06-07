/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */

import Application.Controller;
import Presentation.Window;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setWindow(new Window(controller));
    }
}
