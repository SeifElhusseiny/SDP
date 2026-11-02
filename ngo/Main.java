package ngo;

import ngo.controller.NGOController;
import ngo.model.NGOModel;
import ngo.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        NGOModel model = NGOModel.getInstance();
        ConsoleView view = new ConsoleView();
        NGOController controller = new NGOController(model, view);
        controller.run();
    }
}
