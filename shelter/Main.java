package shelter;

import shelter.database.JsonDatabase;
import shelter.model.SampleData;
import shelter.model.ShelterModel;
import shelter.view.SwingMainFrame;

import javax.swing.SwingUtilities;

// for console-based interface
// import shelter.controller.MainController;
// import shelter.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        ShelterModel model = ShelterModel.getInstance();
        JsonDatabase database = JsonDatabase.getInstance();

        if (database.isFirstRun()) {
            // First run only: the JSON files were just created, fill them with sample data.
            SampleData.load(model);
            database.save(model);
            System.out.println("Database created in: " + database.getFolderPath());
        } else {
            database.load(model);
            System.out.println("Data loaded from: " + database.getFolderPath());
        }

        // Uncomment the following line to run the console-based interface instead of the Swing GUI.
        // new MainController(new ConsoleView()).run();
        
        SwingUtilities.invokeLater(() -> {
            SwingMainFrame frame = new SwingMainFrame(model, database);
            frame.setVisible(true);
        });

    }
}
