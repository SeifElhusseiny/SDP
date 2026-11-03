package shelter.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;

import shelter.model.ShelterModel;
import shelter.model.User;
import shelter.model.Validator;
import shelter.patterns.factory.UserFactory;

public class AuthenticationService {
    private final ShelterModel model;

    public AuthenticationService(ShelterModel model) {
        this.model = model;
    }

    public User login(String email, char[] password) {
        User user = findByEmail(email);
        if (user == null || !matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return user;
    }

    public User signup(String role, String name, String email, String phone, char[] password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException(Validator.EMAIL_RULE);
        }
        if (!Validator.isValidPhone(phone)) {
            throw new IllegalArgumentException(Validator.PHONE_RULE);
        }
        if (password == null || password.length < 6) {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (!"Adopter".equals(role) && !"Donor".equals(role)) {
            throw new IllegalArgumentException("Public signup is only available for adopters and donors.");
        }
        User user = UserFactory.createUser(role, model.nextUserId(), name, email, phone,
                Collections.singletonList("Email"), hash(password));
        model.addUser(user);
        return user;
    }

    public static String hash(char[] password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(new String(password).getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte value : bytes) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private boolean matches(char[] password, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) {
            Arrays.fill(password, '\0');
            return false;
        }
        String candidate = hash(password);
        return MessageDigest.isEqual(candidate.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8));
    }

    private User findByEmail(String email) {
        for (User user : model.getAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                return user;
            }
        }
        return null;
    }
}
