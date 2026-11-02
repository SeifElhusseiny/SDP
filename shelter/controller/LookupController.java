package shelter.controller;

import java.util.List;

import shelter.model.LookupTable;
import shelter.view.ConsoleView;

// CRUD for the lookup tables (species, skills, event categories, donation types).
public class LookupController extends BaseController {

    public LookupController(ConsoleView view) {
        super(view);
    }

    @Override
    public void run() {
        List<LookupTable> tables = model.getAllLookupTables();
        String[] names = new String[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            names[i] = tables.get(i).getName();
        }
        while (true) {
            int choice = view.showMenu("LOOKUP TABLES", names, "Back");
            if (choice == 0) {
                return;
            }
            manage(tables.get(choice - 1));
        }
    }

    private void manage(LookupTable table) {
        String[] options = {"Add value", "View values", "Update value", "Delete value"};
        while (true) {
            int choice = view.showMenu(table.getName().toUpperCase(), options, "Back");
            switch (choice) {
                case 1:
                    boolean added = table.add(view.readText("New value: "));
                    view.showMessage(added ? "Added." : "That value already exists.");
                    break;
                case 2:
                    showValues(table);
                    break;
                case 3:
                    if (showValues(table)) {
                        int index = view.readInt("Number to update: ", 1, table.getAll().size()) - 1;
                        table.update(index, view.readText("New value: "));
                        view.showMessage("Updated.");
                    }
                    break;
                case 4:
                    if (showValues(table)) {
                        int index = view.readInt("Number to delete: ", 1, table.getAll().size()) - 1;
                        table.delete(index);
                        view.showMessage("Deleted.");
                    }
                    break;
                default:
                    return;
            }
            save();
        }
    }

    // Returns false when the table is empty.
    private boolean showValues(LookupTable table) {
        List<String> values = table.getAll();
        if (values.isEmpty()) {
            view.showMessage("(empty)");
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            view.showMessage((i + 1) + ". " + values.get(i));
        }
        return true;
    }
}
