package lv.macibas.library.notification;

/**
 * Strategy šablons + Spring: vairākas implementācijas vienam interfeisam.
 *
 * Kā Spring izlemj, kuru injicēt, ja ir vairāki beani?
 *  - Injicē List<NotificationSender> -> dabū VISUS (to dara NotificationService)
 *  - @Primary uz vienas implementācijas -> tā ir noklusējuma
 *  - @Qualifier("smsNotificationSender") -> konkrēti norādi
 *  - Ja nekā no šī un injicē vienu -> NoUniqueBeanDefinitionException startējot
 */
public interface NotificationSender {

    String channel();

    void send(String recipient, String message);
}
