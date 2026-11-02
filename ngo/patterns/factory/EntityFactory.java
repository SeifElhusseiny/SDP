package ngo.patterns.factory;

import ngo.model.Beneficiary;
import ngo.model.Donation;
import ngo.model.Event;
import ngo.model.Volunteer;
import ngo.patterns.strategy.BankTransferPaymentStrategy;
import ngo.patterns.strategy.CashPaymentStrategy;
import ngo.patterns.strategy.ChequePaymentStrategy;
import ngo.patterns.strategy.CommunicationStrategy;
import ngo.patterns.strategy.EmailStrategy;
import ngo.patterns.strategy.PaymentStrategy;
import ngo.patterns.strategy.SMSStrategy;
import ngo.patterns.strategy.SocialMediaStrategy;
import ngo.patterns.strategy.VisaPaymentStrategy;

public class EntityFactory {
    private EntityFactory() {
    }

    public static Donation createDonation(int id, String donorName, String donorType, String donationType,
                                         double amount, String paymentMethod) {
        return new Donation(id, donorName, donorType, donationType, amount, paymentMethod);
    }

    public static Volunteer createVolunteer(int id, String name, String skill, String availability) {
        return new Volunteer(id, name, skill, availability);
    }

    public static Event createEvent(int id, String title, String type, String date, int capacity) {
        return new Event(id, title, type, date, capacity);
    }

    public static Beneficiary createBeneficiary(int id, String name, String need, String status) {
        return new Beneficiary(id, name, need, status);
    }

    public static CommunicationStrategy createCommunicationStrategy(String strategyName) {
        if (strategyName == null) {
            throw new IllegalArgumentException("Strategy name cannot be null");
        }

        switch (strategyName.trim().toLowerCase()) {
            case "email":
                return new EmailStrategy();
            case "sms":
                return new SMSStrategy();
            case "social":
            case "socialmedia":
                return new SocialMediaStrategy();
            default:
                throw new IllegalArgumentException("Unknown communication strategy: " + strategyName);
        }
    }

    public static PaymentStrategy createPaymentStrategy(String paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        String normalized = paymentMethod.trim().toLowerCase();

        switch (normalized) {
            case "cash":
                return new CashPaymentStrategy();
            case "visa":
            case "mastercard":
                return new VisaPaymentStrategy();
            case "bank transfer":
                return new BankTransferPaymentStrategy();
            case "cheque":
                return new ChequePaymentStrategy();
            default:
                throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        }
    }
}
