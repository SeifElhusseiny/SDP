package shelter.model;

// Input rules used when users are added or updated.
public class Validator {
    public static final String EMAIL_RULE = "Email must look like name@example.com";
    public static final String PHONE_RULE = "Phone must contain digits only (8 to 15 digits), e.g. 01012345678";

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{8,15}");
    }
}
