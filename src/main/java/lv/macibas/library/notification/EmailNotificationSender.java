package lv.macibas.library.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Override
    public String channel() {
        return "email";
    }

    @Override
    public void send(String recipient, String message) {
        log.info("📧 [email -> {}] {}", recipient, message); // īstenībā: JavaMailSender / SendGrid API
    }
}
