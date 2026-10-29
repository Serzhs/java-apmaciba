package lv.macibas.library.notification;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Klausās BookBorrowedEvent un sūta paziņojumus pa VISIEM kanāliem.
 *
 * @TransactionalEventListener(AFTER_COMMIT) — izpildās TIKAI ja transakcija veiksmīgi saglabājās.
 * Ar parasto @EventListener e-pasts aizietu pat tad, ja pēc tam DB rollback — klasiska kļūda!
 *
 * LoanService nezina par e-pastiem/SMS -> vāja sasaiste (Observer).
 * Jauns kanāls (Slack) = jauna @Component klase, nekas cits nav jāmaina (Open/Closed).
 */
@Component
public class NotificationService {

    private final List<NotificationSender> senders; // Spring injicē VISAS implementācijas

    public NotificationService(List<NotificationSender> senders) {
        this.senders = senders;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookBorrowed(BookBorrowedEvent event) {
        String message = "Tu aizņēmies '" + event.bookTitle() + "'. Atdod līdz " + event.dueDate() + ".";
        senders.forEach(sender -> sender.send(event.borrowerName(), message));
    }
}
