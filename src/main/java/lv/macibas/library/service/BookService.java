package lv.macibas.library.service;

import lv.macibas.library.domain.Author;
import lv.macibas.library.domain.Book;
import lv.macibas.library.dto.BookRequest;
import lv.macibas.library.dto.BookResponse;
import lv.macibas.library.dto.PageResponse;
import lv.macibas.library.exception.BusinessRuleException;
import lv.macibas.library.exception.NotFoundException;
import lv.macibas.library.repository.AuthorRepository;
import lv.macibas.library.repository.BookRepository;
import lv.macibas.library.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ⚛️ React: kā custom hook (useBooks) — loģika atdalīta no "UI" (kontroliera).
 *
 * SERVISA SLĀNIS — biznesa loģika + transakciju robežas.
 *
 * Slāņi: Controller (HTTP) -> Service (bizness) -> Repository (DB)
 *  - Kontrolieris NEzina par DB
 *  - Repozitorijs NEzina par HTTP
 *  - Serviss koordinē un nosaka transakciju
 *
 * @Transactional(readOnly = true) uz klases -> visas metodes pēc noklusējuma tikai lasa
 * (Hibernate var optimizēt: nav dirty checking). Rakstošās metodes pārraksta ar @Transactional.
 *
 * Constructor injection (nevis @Autowired uz lauka), jo:
 *  - lauki var būt final (immutable)
 *  - atkarības ir skaidri redzamas
 *  - viegli testēt: new BookService(mockRepo, ...) bez Spring
 *  - ja ir tikai viens konstruktors, @Autowired nav vajadzīgs
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.loanRepository = loanRepository;
    }

    public PageResponse<BookResponse> findAll(String authorName, Pageable pageable) {
        Page<Book> page = (authorName == null || authorName.isBlank())
                ? bookRepository.findAll(pageable)
                : bookRepository.findByAuthorNameContainingIgnoreCase(authorName, pageable);
        return PageResponse.from(page.map(BookResponse::from));
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(getBook(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Grāmata ar ISBN " + request.isbn() + " jau eksistē");
        }
        Author author = getAuthor(request.authorId());
        Book book = new Book(request.title().trim(), request.isbn(), request.publishedYear(), author);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = getBook(id);
        bookRepository.findByIsbn(request.isbn())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new BusinessRuleException("ISBN " + request.isbn() + " jau pieder citai grāmatai");
                });
        book.updateDetails(request.title().trim(), request.isbn(), request.publishedYear(), getAuthor(request.authorId()));
        // ★ Nav bookRepository.save(book)! Hibernate "DIRTY CHECKING":
        //   entity ir "managed" stāvoklī -> transakcijas beigās Hibernate pats pamana izmaiņas un izpilda UPDATE.
        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = getBook(id);
        if (loanRepository.existsByBookId(id)) {
            throw new BusinessRuleException("Grāmatu ar izsniegumu vēsturi dzēst nevar");
        }
        bookRepository.delete(book);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Grāmata", id));
    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Autors", id));
    }
}
