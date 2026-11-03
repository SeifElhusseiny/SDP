package shelter.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import shelter.database.JsonDatabase;
import shelter.model.ShelterModel;
import shelter.model.Adopter;
import shelter.model.Donor;
import shelter.model.Volunteer;
import shelter.model.User;
import shelter.patterns.factory.decorator.AbstractPackageAddonFactory;
import shelter.patterns.factory.strategy.AbstractNotificationFactory;
import shelter.patterns.factory.strategy.AbstractPaymentFactory;
import shelter.patterns.factory.user.AbstractUserFactory;
import shelter.service.AuthenticationService;
import shelter.service.Session;

public class SwingMainFrame extends JFrame {
    private static final String WELCOME = "welcome";
    private static final String LOGIN = "login";
    private static final String SIGNUP = "signup";
    private static final String DASHBOARD = "dashboard";

    private final ShelterModel model;
    private final JsonDatabase database;
    private final List<AbstractUserFactory> userFactories;
    private final List<AbstractNotificationFactory> notificationFactories;
    private final List<AbstractPaymentFactory> paymentFactories;
    private final List<AbstractPackageAddonFactory> addonFactories;
    private final AuthenticationService authenticationService;
    private final Session session = new Session();
    private final CardLayout mainCardLayout = new CardLayout();
    private final JPanel mainContentPanel = new JPanel(mainCardLayout);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final DashboardPanel dashboardPanel;
    private final JPanel dashboardShell = new JPanel(new BorderLayout());

    public SwingMainFrame(ShelterModel model, JsonDatabase database, List<AbstractUserFactory> userFactories,
            List<AbstractNotificationFactory> notificationFactories,
            List<AbstractPaymentFactory> paymentFactories,
            List<AbstractPackageAddonFactory> addonFactories) {
        this.model = model;
        this.database = database;
        this.userFactories = userFactories;
        this.notificationFactories = notificationFactories;
        this.paymentFactories = paymentFactories;
        this.addonFactories = addonFactories;
        authenticationService = new AuthenticationService(model, notificationFactories);
        dashboardPanel = new DashboardPanel(model);

        setTitle("Paws & Whiskers Animal Shelter");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);

        buildDashboardShell();
        buildMainContent();
        showWelcome();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                database.save(model);
                dispose();
            }
        });
    }

    private void buildDashboardShell() {
        dashboardShell.add(createHeader(), BorderLayout.NORTH);
        dashboardShell.add(createNavigation(), BorderLayout.WEST);
        dashboardShell.add(createContent(), BorderLayout.CENTER);
    }

    private void buildMainContent() {
        mainContentPanel.add(new WelcomePanel(this::showLoginPlaceholder, this::showSignupPlaceholder, this::exit),
                WELCOME);
        mainContentPanel.add(new LoginPanel(new LoginPanel.LoginHandler() {
            @Override
            public User login(String email, char[] password) {
                return authenticationService.login(email, password);
            }

            @Override
            public void success(User user) {
                session.login(user);
                showRoleDashboard(user);
            }
        }, this::showWelcome), LOGIN);
        List<AbstractUserFactory> signupFactories = new java.util.ArrayList<>();
        for (AbstractUserFactory factory : userFactories) {
            if ("Adopter".equals(factory.getUserType()) || "Donor".equals(factory.getUserType())) {
                signupFactories.add(factory);
            }
        }
        mainContentPanel.add(new SignupPanel(signupFactories, notificationFactories,
                (userFactory, name, email, phone, channels, password) -> {
            User user = authenticationService.signup(userFactory, name, email, phone, channels, password);
            database.save(model);
            return user;
        }, this::showWelcome), SIGNUP);
        setContentPane(mainContentPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 80, 72));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Paws & Whiskers Animal Shelter");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel createNavigation() {
        JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 8));
        navigation.setBackground(new Color(238, 243, 241));
        navigation.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        navigation.setPreferredSize(new Dimension(190, 0));

        addNavigationButton(navigation, "Dashboard", DASHBOARD);
        addNavigationButton(navigation, "Animals", "animals");
        addNavigationButton(navigation, "Users", "users");
        addNavigationButton(navigation, "Donations", "donations");
        addNavigationButton(navigation, "Events", "events");
        addNavigationButton(navigation, "Adoptions", "adoptions");
        addNavigationButton(navigation, "Communication", "communication");
        addNavigationButton(navigation, "Reports", "reports");
        JButton logoutButton = new JButton("Logout");
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMargin(new Insets(10, 14, 10, 14));
        logoutButton.addActionListener(event -> logout());
        navigation.add(logoutButton);
        return navigation;
    }

    private void addNavigationButton(JPanel navigation, String label, String cardName) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(10, 14, 10, 14));
        button.addActionListener(event -> {
            if (DASHBOARD.equals(cardName)) {
                dashboardPanel.refresh();
            }
            cardLayout.show(contentPanel, cardName);
        });
        navigation.add(button);
    }

    private JPanel createContent() {
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        contentPanel.add(dashboardPanel, DASHBOARD);
        contentPanel.add(createPlaceholder("Animals"), "animals");
        contentPanel.add(createPlaceholder("Users"), "users");
        contentPanel.add(createPlaceholder("Donations"), "donations");
        contentPanel.add(createPlaceholder("Events"), "events");
        contentPanel.add(createPlaceholder("Adoptions"), "adoptions");
        contentPanel.add(createPlaceholder("Communication"), "communication");
        contentPanel.add(createPlaceholder("Reports"), "reports");
        return contentPanel;
    }

    private void showWelcome() {
        session.logout();
        mainCardLayout.show(mainContentPanel, WELCOME);
        revalidate();
        repaint();
    }

    private void showLoginPlaceholder() {
        mainCardLayout.show(mainContentPanel, LOGIN);
        revalidate();
        repaint();
    }

    private void showSignupPlaceholder() {
        mainCardLayout.show(mainContentPanel, SIGNUP);
        revalidate();
        repaint();
    }

    private JPanel createAuthPlaceholder(String title, String message, Runnable backAction) {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(messageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> backAction.run());
        JPanel actions = new JPanel();
        actions.add(backButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void exit() {
        database.save(model);
        dispose();
    }

    private void showRoleDashboard(User user) {
        JPanel dashboard;
        switch (user.getRole()) {
            case "Admin":
                dashboard = new AdminDashboardPanel(model, database, user, userFactories,
                        notificationFactories, paymentFactories, this::logout);
                break;
            case "Volunteer":
                dashboard = new VolunteerDashboardPanel(model, database, (Volunteer) user, this::logout);
                break;
            case "Adopter":
                dashboard = new AdopterDashboardPanel(model, database, (Adopter) user,
                        addonFactories, notificationFactories, this::logout);
                break;
            case "Donor":
                dashboard = new DonorDashboardPanel(model, database, (Donor) user,
                        paymentFactories, notificationFactories, this::logout);
                break;
            default:
                throw new IllegalArgumentException("Unsupported user role: " + user.getRole());
        }
        mainContentPanel.add(dashboard, DASHBOARD);
        mainCardLayout.show(mainContentPanel, DASHBOARD);
        revalidate();
        repaint();
    }

    private void showActionPlaceholder() {
        javax.swing.JOptionPane.showMessageDialog(this,
                "This action will be implemented in a later phase.",
                "Coming soon", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        session.logout();
        mainCardLayout.show(mainContentPanel, WELCOME);
        revalidate();
        repaint();
    }

    private JPanel createPlaceholder(String sectionName) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(sectionName + " screen will be implemented in a later phase.",
                SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
