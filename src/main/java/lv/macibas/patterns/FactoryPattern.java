package lv.macibas.patterns;

/**
 * FACTORY — objekta izveides loģika ir paslēpta vienā vietā; klients prasa "dod man X",
 * nezinot konkrēto klasi. Tips: Creational.
 *
 * Varianti:
 *  - Simple/Static Factory: static metode, kas izlemj, kuru klasi veidot (List.of, Optional.of)
 *  - Factory Method: apakšklase izlemj, kādu objektu veidot
 *  - Abstract Factory: rūpnīca, kas ražo veselu saistītu objektu ģimeni
 *
 * Spring pats ir milzīga rūpnīca: BeanFactory / ApplicationContext.
 */
public class FactoryPattern {

    interface Notification {
        void send(String to, String message);
    }

    static class EmailNotification implements Notification {
        public void send(String to, String message) { System.out.println("📧 " + to + ": " + message); }
    }

    static class SmsNotification implements Notification {
        public void send(String to, String message) { System.out.println("📱 " + to + ": " + message); }
    }

    static class PushNotification implements Notification {
        public void send(String to, String message) { System.out.println("🔔 " + to + ": " + message); }
    }

    enum Channel { EMAIL, SMS, PUSH }

    // Simple Factory — viena vieta, kur ir "new". Jauns kanāls = izmaiņa tikai šeit.
    static final class NotificationFactory {
        private NotificationFactory() {}

        static Notification create(Channel channel) {
            return switch (channel) { // Java 14+ switch expression
                case EMAIL -> new EmailNotification();
                case SMS -> new SmsNotification();
                case PUSH -> new PushNotification();
            };
        }
    }

    public static void main(String[] args) {
        for (Channel ch : Channel.values()) {
            NotificationFactory.create(ch).send("Jānis", "Tava grāmata ir gatava saņemšanai");
        }
    }
}
