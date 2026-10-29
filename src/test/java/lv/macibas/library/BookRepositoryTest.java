package lv.macibas.library;

import lv.macibas.library.domain.Author;
import lv.macibas.library.domain.Book;
import lv.macibas.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATU SLĀŅA TESTS (@DataJpaTest) — palaiž tikai JPA + in-memory DB.
 * Katrs tests ir transakcijā, kas beigās tiek atritināta (rollback) -> testi neietekmē viens otru.
 * Pārbauda, vai TAVI vaicājumi tiešām strādā.
 */
@DataJpaTest
class BookRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired BookRepository bookRepository;

    @Test
    void findByIsbn_findsBook() {
        Author author = em.persist(new Author("Frank Herbert"));
        em.persist(new Book("Dune", "9780441013593", 1965, author));

        assertThat(bookRepository.findByIsbn("9780441013593"))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Dune");
        assertThat(bookRepository.findByIsbn("0000000000")).isEmpty();
    }

    @Test
    void searchByAuthorName_isCaseInsensitiveAndPaged() {
        Author martin = em.persist(new Author("Robert C. Martin"));
        Author other = em.persist(new Author("Kāds Cits"));
        em.persist(new Book("Clean Code", "9780132350884", 2008, martin));
        em.persist(new Book("Clean Architecture", "9780134494166", 2017, martin));
        em.persist(new Book("Cita grāmata", "1234567890", 2000, other));

        Page<Book> page = bookRepository.findByAuthorNameContainingIgnoreCase("MARTIN", PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void countPublishedBefore_nativeQuery() {
        Author a = em.persist(new Author("A"));
        em.persist(new Book("Veca", "1111111111", 1900, a));
        em.persist(new Book("Jauna", "2222222222", 2020, a));

        assertThat(bookRepository.countPublishedBefore(1950)).isEqualTo(1);
    }
}
