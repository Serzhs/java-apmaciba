package lv.macibas.library.repository;

import lv.macibas.library.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ⚛️ React: kā tavs api.ts fails — vienīgā vieta, kas runā ar datu avotu (te — datubāzi).
 *
 * Spring Data JPA repozitorijs — tu raksti TIKAI interfeisu, Spring izpildes laikā
 * izveido implementāciju (proxy). JpaRepository jau dod: save, findById, findAll, deleteById, count...
 *
 * Trīs veidi, kā rakstīt vaicājumus:
 *  1. Derived query — no metodes nosaukuma: findByIsbn -> WHERE isbn = ?
 *  2. @Query ar JPQL (strādā ar entītijām, nevis tabulām)
 *  3. @Query(nativeQuery = true) — tīrs SQL (kad vajag DB specifiskas lietas)
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // ★ N+1 PROBLĒMA:
    //   findAll() atgriež 10 grāmatas (1 vaicājums), tad book.getAuthor().getName()
    //   katrai izpilda vēl 1 vaicājumu -> 1 + 10 = 11 vaicājumi.
    //   Risinājums: @EntityGraph vai JOIN FETCH — viens vaicājums ar JOIN.
    @Override
    @EntityGraph(attributePaths = "author")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable);

    @Query("select b from Book b join fetch b.author where b.available = true order by b.title")
    List<Book> findAllAvailableWithAuthor();

    // Native SQL piemērs
    @Query(value = "select count(*) from books where published_year < :year", nativeQuery = true)
    long countPublishedBefore(@Param("year") int year);
}
