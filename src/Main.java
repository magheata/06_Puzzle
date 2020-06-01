/* Created by andreea on 26/05/2020 */

import Application.Controller;
import Presentation.Window;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setWindow(new Window(controller));
        controller.start();
    }
}
