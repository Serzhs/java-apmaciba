package lv.macibas.library.config;

import lv.macibas.library.domain.Author;
import lv.macibas.library.domain.Book;
import lv.macibas.library.repository.AuthorRepository;
import lv.macibas.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ieliek testa datus, kad aplikācija palaižas.
 * @Profile("!test") — NEizpildās, ja aktīvs "test" profils (integrācijas testiem gribam tukšu DB).
 *
 * CommandLineRunner/ApplicationRunner — kods, kas izpildās vienreiz pēc konteksta startēšanas.
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataInitializer(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Author blaumanis = authorRepository.save(new Author("Rūdolfs Blaumanis"));
        Author rainis = authorRepository.save(new Author("Rainis"));
        Author bloch = authorRepository.save(new Author("Joshua Bloch"));
        Author martin = authorRepository.save(new Author("Robert C. Martin"));

        bookRepository.save(new Book("Zvejnieka dēls", "9789984000001", 1934, blaumanis));
        bookRepository.save(new Book("Nāves ēnā", "9789984000002", 1899, blaumanis));
        bookRepository.save(new Book("Uguns un nakts", "9789984000003", 1905, rainis));
        bookRepository.save(new Book("Effective Java", "9780134685991", 2018, bloch));
        bookRepository.save(new Book("Clean Code", "9780132350884", 2008, martin));
        bookRepository.save(new Book("Clean Architecture", "9780134494166", 2017, martin));

        log.info("📚 Ielādētas {} grāmatas. Mēģini: http://localhost:8080/api/books", bookRepository.count());
    }
}
