package shelter.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

// MVC - View: the only class that reads from or prints to the console.
public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);

    public void showTitle(String title) {
        System.out.println();
        System.out.println("==================== " + title + " ====================");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    // Prints "1. option" for each option, then "0. backLabel". Returns the chosen number.
    public int showMenu(String title, String[] options, String backLabel) {
        showTitle(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("0. " + backLabel);
        return readInt("Choose: ", 0, options.length);
    }

    public void showList(String title, List<?> items) {
        System.out.println("--- " + title + " ---");
        if (items.isEmpty()) {
            System.out.println("(nothing yet)");
        }
        for (Object item : items) {
            System.out.println(item);
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    public double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // falls through to the message below
            }
            System.out.println("Please enter a number greater than 0.");
        }
    }

    // Reads text that cannot be empty.
    public String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    // Used when updating: pressing Enter keeps the current value.
    public String readOptional(String prompt, String currentValue) {
        System.out.print(prompt + " [" + currentValue + "] (Enter to keep): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? currentValue : input;
    }

    // Keeps asking until the input passes the rule.
    public String readValid(String prompt, Predicate<String> rule, String ruleMessage) {
        while (true) {
            String input = readText(prompt);
            if (rule.test(input)) {
                return input;
            }
            System.out.println(ruleMessage);
        }
    }

    // Like readOptional, but a new value must pass the rule.
    public String readOptionalValid(String prompt, String currentValue, Predicate<String> rule, String ruleMessage) {
        while (true) {
            String input = readOptional(prompt, currentValue);
            if (input.equals(currentValue) || rule.test(input)) {
                return input;
            }
            System.out.println(ruleMessage);
        }
    }

    // Shows a numbered list and returns the chosen value.
    public String chooseFrom(String title, List<String> options) {
        System.out.println(title + ":");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i));
        }
        int choice = readInt("Choose: ", 1, options.size());
        return options.get(choice - 1);
    }

    // Lets the user pick one or more options, e.g. "1,3". Returns the chosen values.
    public List<String> chooseMultiple(String title, List<String> options) {
        System.out.println(title + " (you can choose more than one):");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i));
        }
        while (true) {
            System.out.print("Choose numbers separated by commas (e.g. 1,3): ");
            String[] parts = scanner.nextLine().split(",");
            List<String> chosen = new ArrayList<>();
            boolean valid = true;
            for (String part : parts) {
                try {
                    int number = Integer.parseInt(part.trim());
                    if (number < 1 || number > options.size()) {
                        valid = false;
                    } else if (!chosen.contains(options.get(number - 1))) {
                        chosen.add(options.get(number - 1));
                    }
                } catch (NumberFormatException e) {
                    valid = false;
                }
            }
            if (valid && !chosen.isEmpty()) {
                return chosen;
            }
            System.out.println("Please enter numbers from 1 to " + options.size() + ", separated by commas.");
        }
    }

    public boolean confirm(String question) {
        System.out.print(question + " (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }
}
