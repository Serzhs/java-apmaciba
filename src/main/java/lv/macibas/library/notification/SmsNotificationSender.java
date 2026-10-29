package lv.macibas.library.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationSender.class);

    @Override
    public String channel() {
        return "sms";
    }

    @Override
    public void send(String recipient, String message) {
        log.info("📱 [sms -> {}] {}", recipient, message); // īstenībā: Twilio API
    }
}
