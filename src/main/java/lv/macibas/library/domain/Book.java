package lv.macibas.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lv.macibas.library.exception.BusinessRuleException;

/**
 * Grāmata. Šeit redzams "rich domain model" — biznesa likumi (vai var aizņemties) dzīvo
 * PAŠĀ ENTITY (iekapsulēšana!), nevis izkaisīti pa servisiem ar setteriem.
 */
@Entity
@Table(name = "books", indexes = @Index(name = "idx_books_title", columnList = "title"))
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 13) // unique -> DB līmeņa ierobežojums
    private String isbn;

    private Integer publishedYear;

    @Column(nullable = false)
    private boolean available = true;

    // @ManyToOne pēc noklusējuma ir EAGER! Labā prakse — vienmēr LAZY, un ielādēt ar JOIN FETCH/EntityGraph, kad vajag.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    // Optimistic locking: Hibernate pievieno "WHERE version = ?" pie UPDATE.
    // Ja divi lietotāji vienlaicīgi aizņemas to pašu grāmatu, otrais dabūs OptimisticLockException -> 409.
    @Version
    private Long version;

    protected Book() {}

    public Book(String title, String isbn, Integer publishedYear, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.author = author;
    }

    public void updateDetails(String title, String isbn, Integer publishedYear, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.author = author;
    }

    public void markBorrowed() {
        if (!available) {
            throw new BusinessRuleException("Grāmata '" + title + "' jau ir izsniegta");
        }
        available = false;
    }

    public void markReturned() {
        available = true;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getIsbn() { return isbn; }

    public Integer getPublishedYear() { return publishedYear; }

    public boolean isAvailable() { return available; }

    public Author getAuthor() { return author; }

    public Long getVersion() { return version; }

    // equals/hashCode JPA entītijām ir viltīga tēma:
    //  - NElieto visus laukus (lazy lauki, mainīgi lauki)
    //  - id ir null pirms save() -> bieži lieto id + konstantu hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // stabils arī pirms un pēc persist
    }
}
