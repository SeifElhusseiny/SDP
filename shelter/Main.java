package shelter;

import shelter.database.JsonDatabase;
import shelter.model.SampleData;
import shelter.model.ShelterModel;
import shelter.patterns.factory.decorator.AbstractPackageAddonFactory;
import shelter.patterns.factory.decorator.MicrochipAddonFactory;
import shelter.patterns.factory.decorator.NeuteringAddonFactory;
import shelter.patterns.factory.decorator.StarterKitAddonFactory;
import shelter.patterns.factory.decorator.VaccinationAddonFactory;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.factory.strategy.AbstractPaymentFactory;
import shelter.patterns.factory.strategy.BankTransferPaymentFactory;
import shelter.patterns.factory.strategy.CardPaymentFactory;
import shelter.patterns.factory.strategy.CashPaymentFactory;
import shelter.patterns.factory.strategy.ChequePaymentFactory;
import shelter.patterns.factory.strategy.EmailNotificationFactory;
import shelter.patterns.factory.strategy.SMSNotificationFactory;
import shelter.patterns.factory.strategy.SocialMediaNotificationFactory;
import shelter.patterns.factory.user.AbstractUserFactory;
import shelter.patterns.factory.user.AdminUserFactory;
import shelter.patterns.factory.user.AdopterUserFactory;
import shelter.patterns.factory.user.DonorUserFactory;
import shelter.patterns.factory.user.VolunteerUserFactory;
import shelter.view.SwingMainFrame;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

// for console-based interface
// import shelter.controller.MainController;
// import shelter.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        ShelterModel model = ShelterModel.getInstance();
        JsonDatabase database = JsonDatabase.getInstance();
        List<AbstractNotificationFactory> notificationFactories = Arrays.asList(new EmailNotificationFactory(),
                new SMSNotificationFactory(), new SocialMediaNotificationFactory());
        List<AbstractPaymentFactory> paymentFactories = Arrays.asList(new CashPaymentFactory(),
                new CardPaymentFactory(), new BankTransferPaymentFactory(), new ChequePaymentFactory());
        List<AbstractPackageAddonFactory> addonFactories = Arrays.asList(new VaccinationAddonFactory(),
                new MicrochipAddonFactory(), new NeuteringAddonFactory(), new StarterKitAddonFactory());
        List<AbstractUserFactory> userFactories = Arrays.asList(new DonorUserFactory(notificationFactories),
                new VolunteerUserFactory(notificationFactories), new AdopterUserFactory(notificationFactories),
                new AdminUserFactory(notificationFactories));

        if (database.isFirstRun()) {
            // First run only: the JSON files were just created, fill them with sample data.
            SampleData.load(model, notificationFactories);
            database.save(model);
            System.out.println("Database created in: " + database.getFolderPath());
        } else {
            database.load(model, userFactories, addonFactories);
            System.out.println("Data loaded from: " + database.getFolderPath());
        }

        // Uncomment the following line to run the console-based interface instead of the Swing GUI.
        // new MainController(new ConsoleView(), userFactories, notificationFactories,
        //         paymentFactories, addonFactories).run();
        
        SwingUtilities.invokeLater(() -> {
            SwingMainFrame frame = new SwingMainFrame(model, database, userFactories,
                    notificationFactories, paymentFactories, addonFactories);
            frame.setVisible(true);
        });

    }
}
