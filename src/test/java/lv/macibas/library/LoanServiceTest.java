package lv.macibas.library;

import lv.macibas.library.config.LibraryProperties;
import lv.macibas.library.domain.Author;
import lv.macibas.library.domain.Book;
import lv.macibas.library.domain.Loan;
import lv.macibas.library.dto.LoanResponse;
import lv.macibas.library.exception.BusinessRuleException;
import lv.macibas.library.exception.NotFoundException;
import lv.macibas.library.notification.BookBorrowedEvent;
import lv.macibas.library.repository.BookRepository;
import lv.macibas.library.repository.LoanRepository;
import lv.macibas.library.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UNIT TESTS — testē VIENU klasi izolēti. Bez Spring, bez DB -> ļoti ātri (milisekundes).
 * Atkarības aizvieto ar MOCK (viltus objektiem), kuriem mēs pasakām, ko atgriezt.
 *
 * React analoģija: kā testēt komponentu ar jest.mock('./api') — tu neizsauc īsto serveri.
 *
 * Struktūra: Given / When / Then (jeb Arrange / Act / Assert)
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock BookRepository bookRepository;
    @Mock LoanRepository loanRepository;
    @Mock ApplicationEventPublisher events;

    // Fiksēts laiks -> testi vienmēr dod vienu rezultātu (tāpēc Clock ir bean!)
    final Clock clock = Clock.fixed(Instant.parse("2026-01-10T10:00:00Z"), ZoneOffset.UTC);
    final LibraryProperties properties = new LibraryProperties(2, 14);

    LoanService loanService;
    Book book;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(bookRepository, loanRepository, properties, clock, events);
        book = new Book("Clean Code", "9780132350884", 2008, new Author("Robert C. Martin"));
    }

    @Test
    @DisplayName("Aizņemoties grāmatu, tā kļūst nepieejama un termiņš = šodiena + 14 dienas")
    void borrow_success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.countByBorrowerNameAndReturnedAtIsNull("Jānis")).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        LoanResponse result = loanService.borrow(1L, "Jānis");

        // Then
        assertThat(book.isAvailable()).isFalse();
        assertThat(result.borrowedAt()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(result.dueDate()).isEqualTo(LocalDate.of(2026, 1, 24));
        verify(events).publishEvent(any(BookBorrowedEvent.class));
    }

    @Test
    void borrow_bookAlreadyBorrowed_throws() {
        book.markBorrowed();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> loanService.borrow(1L, "Jānis"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("jau ir izsniegta");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void borrow_tooManyActiveLoans_throws() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.countByBorrowerNameAndReturnedAtIsNull("Jānis")).thenReturn(2L);

        assertThatThrownBy(() -> loanService.borrow(1L, "Jānis"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("max 2");

        assertThat(book.isAvailable()).isTrue(); // nekas netika mainīts
    }

    @Test
    void borrow_unknownBook_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrow(99L, "Jānis"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void returnBook_makesBookAvailableAgain() {
        book.markBorrowed();
        Loan loan = new Loan(book, "Jānis", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        LoanResponse result = loanService.returnBook(5L);

        assertThat(result.returnedAt()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(book.isAvailable()).isTrue();
    }
}
