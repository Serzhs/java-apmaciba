package lv.macibas.library.service;

import lv.macibas.library.config.LibraryProperties;
import lv.macibas.library.domain.Book;
import lv.macibas.library.domain.Loan;
import lv.macibas.library.dto.LoanResponse;
import lv.macibas.library.exception.BusinessRuleException;
import lv.macibas.library.exception.NotFoundException;
import lv.macibas.library.notification.BookBorrowedEvent;
import lv.macibas.library.repository.BookRepository;
import lv.macibas.library.repository.LoanRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Grāmatu izsniegšana — labs TRANSAKCIJU piemērs.
 *
 * ⚛️ React: kā "optimistic update" ar rollback — ja kaut kas neizdodas, viss atgriežas kā bija.
 *
 * borrow() maina DIVAS lietas: Book.available = false UN izveido Loan.
 * Abām jānotiek kopā vai nevienai (ACID — Atomicity). Ja kaut kas izmet RuntimeException,
 * Spring izdara ROLLBACK un DB paliek kā bija.
 *
 * ★ @Transactional slazdi (bieži jautā!):
 *  1. Strādā caur proxy -> this.borrow() no tās pašas klases NEATVER transakciju
 *  2. Nestrādā uz private metodēm
 *  3. Rollback pēc noklusējuma tikai uz RuntimeException/Error, NE uz checked Exception
 *     (vajag @Transactional(rollbackFor = Exception.class))
 *  4. Propagation: REQUIRED (noklusējums — pievienojas esošai vai izveido jaunu),
 *     REQUIRES_NEW (vienmēr jauna, esošā tiek apturēta) u.c.
 */
@Service
public class LoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LibraryProperties properties;
    private final Clock clock;
    private final ApplicationEventPublisher events;

    public LoanService(BookRepository bookRepository,
                       LoanRepository loanRepository,
                       LibraryProperties properties,
                       Clock clock,
                       ApplicationEventPublisher events) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.properties = properties;
        this.clock = clock;
        this.events = events;
    }

    @Transactional
    public LoanResponse borrow(Long bookId, String borrowerName) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Grāmata", bookId));

        long activeLoans = loanRepository.countByBorrowerNameAndReturnedAtIsNull(borrowerName);
        if (activeLoans >= properties.maxActiveLoansPerBorrower()) {
            throw new BusinessRuleException(
                    borrowerName + " jau ir " + activeLoans + " grāmatas (max " + properties.maxActiveLoansPerBorrower() + ")");
        }

        book.markBorrowed(); // biznesa likums iekš entity; met izņēmumu, ja jau izsniegta

        LocalDate today = LocalDate.now(clock);
        Loan loan = loanRepository.save(new Loan(book, borrowerName, today, today.plusDays(properties.loanPeriodDays())));

        // Paziņojums tiks nosūtīts TIKAI pēc veiksmīga commit (skat. NotificationService)
        events.publishEvent(new BookBorrowedEvent(loan.getId(), book.getTitle(), borrowerName, loan.getDueDate()));

        return LoanResponse.from(loan);
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Izsniegums", loanId));
        loan.markReturned(LocalDate.now(clock));
        return LoanResponse.from(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findActiveLoans(String borrowerName) {
        return loanRepository.findByBorrowerNameAndReturnedAtIsNullOrderByDueDateAsc(borrowerName).stream()
                .map(LoanResponse::from)
                .toList();
    }
}
