package shelter.controller;

import shelter.database.JsonDatabase;
import shelter.model.ShelterModel;
import shelter.view.ConsoleView;

// MVC - Controller: every controller talks to the same model (SINGLETON) and the view.
public abstract class BaseController {
    protected final ShelterModel model = ShelterModel.getInstance();
    protected final ConsoleView view;

    public BaseController(ConsoleView view) {
        this.view = view;
    }

    public abstract void run();

    // Writes every change to the JSON files (SINGLETON database).
    protected void save() {
        JsonDatabase.getInstance().save(model);
    }
}
