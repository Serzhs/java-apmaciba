package lv.macibas.library.web;

import jakarta.validation.Valid;
import lv.macibas.library.dto.BookRequest;
import lv.macibas.library.dto.BookResponse;
import lv.macibas.library.dto.BorrowRequest;
import lv.macibas.library.dto.LoanResponse;
import lv.macibas.library.dto.PageResponse;
import lv.macibas.library.service.BookService;
import lv.macibas.library.service.LoanService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * ⚛️ React: šis ir tas, uz ko tavs fetch("/api/books") aizsūta pieprasījumu.
 *    Kā Next.js route.ts fails ar export function GET() / POST().
 *
 * REST KONTROLIERIS — tikai HTTP lietas: URL, statusa kodi, validācija, (de)serializācija.
 * Biznesa loģika NAV šeit — tā ir servisā ("thin controller").
 *
 * @RestController = @Controller + @ResponseBody (atgriezto objektu Jackson pārvērš JSON)
 *
 * REST resursu dizains:
 *   GET    /api/books              — saraksts (ar lapošanu, filtru)     200
 *   GET    /api/books/{id}         — viena grāmata                      200 / 404
 *   POST   /api/books              — izveidot                            201 + Location / 400 / 409
 *   PUT    /api/books/{id}         — pilnībā aizvietot                   200 / 404
 *   DELETE /api/books/{id}         — dzēst                               204 / 404
 *   POST   /api/books/{id}/loans   — aizņemties (izveido apakšresursu)   201 / 409
 *
 * URL — lietvārdi daudzskaitlī (books), nevis darbības vārdi (/getBooks ❌).
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    // GET /api/books?author=blau&page=0&size=5&sort=publishedYear,desc
    @GetMapping
    public PageResponse<BookResponse> list(
            @RequestParam(required = false) String author,
            @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        return bookService.findAll(author, pageable);
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request, UriComponentsBuilder uriBuilder) {
        BookResponse created = bookService.create(request);
        URI location = uriBuilder.path("/api/books/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created); // 201 Created + Location header
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @PostMapping("/{id}/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrow(@PathVariable Long id, @Valid @RequestBody BorrowRequest request) {
        return loanService.borrow(id, request.borrowerName().trim());
    }
}
