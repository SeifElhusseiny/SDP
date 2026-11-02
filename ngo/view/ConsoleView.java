package ngo.view;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);

    public void printHeader() {
        System.out.println("====================================================");
        System.out.println("NGO Management System - Phase 1");
        System.out.println("Donation, Volunteer, Event, Beneficiary, and Communication Hub");
        System.out.println("====================================================");
    }

    public void showMainMenu() {
        System.out.println("\nMain Menu");
        System.out.println("1. Register a new donation");
        System.out.println("2. Register a new volunteer");
        System.out.println("3. Create a new event");
        System.out.println("4. Add a beneficiary");
        System.out.println("5. Manage lookup tables");
        System.out.println("6. Send a communication message");
        System.out.println("7. View all records");
        System.out.println("8. Generate a donation receipt");
        System.out.println("9. Exit the system");
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printList(String title, List<String> items) {
        System.out.println("\n" + title + ":");
        if (items.isEmpty()) {
            System.out.println("No entries available.");
            return;
        }
        for (String item : items) {
            System.out.println("- " + item);
        }
    }

    public void printSeparator() {
        System.out.println("----------------------------------------");
    }

    public void printMenuOptionList(String title, List<String> options) {
        System.out.println("\n" + title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    public int readChoice(String title, List<String> options) {
        printMenuOptionList(title, options);

        while (true) {
            int choice = readInt("Choose an option: ");
            if (choice >= 1 && choice <= options.size()) {
                return choice;
            }
            System.out.println("Invalid option. Please select a number from the list.");
        }
    }

    public List<String> getDonorTypeOptions() {
        return Arrays.asList("Individual", "Business", "NGO", "School", "Government", "Other");
    }

    public String resolveDonorType(int choice) {
        List<String> options = getDonorTypeOptions();
        return options.get(choice - 1);
    }

    public List<String> getDonationTypeOptions() {
        return Arrays.asList("Cash donation", "In-kind donation", "Online donation", "Monthly support");
    }

    public String resolveDonationType(int choice) {
        switch (choice) {
            case 1: return "cash";
            case 2: return "in-kind";
            case 3: return "online";
            case 4: return "monthly";
            default: throw new IllegalArgumentException("Invalid donation type choice: " + choice);
        }
    }

    public List<String> getPaymentMethodOptions() {
        return Arrays.asList("Cash", "Visa", "MasterCard", "Bank transfer", "Cheque", "Other");
    }

    public String resolvePaymentMethod(int choice) {
        switch (choice) {
            case 1: return "Cash";
            case 2: return "Visa";
            case 3: return "MasterCard";
            case 4: return "Bank transfer";
            case 5: return "Cheque";
            case 6: return "Other";
            default: throw new IllegalArgumentException("Invalid payment method choice: " + choice);
        }
    }

    public List<String> getCommunicationChannelOptions() {
        return Arrays.asList("Email", "SMS", "Social media", "Phone call");
    }

    public String resolveCommunicationChannel(int choice) {
        switch (choice) {
            case 1: return "email";
            case 2: return "sms";
            case 3: return "social";
            case 4: return "phone";
            default: throw new IllegalArgumentException("Invalid communication channel choice: " + choice);
        }
    }
}
