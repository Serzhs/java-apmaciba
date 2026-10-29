package lv.macibas.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity — Java klase, kas atbilst DB tabulai.
 *
 * Attiecība: viens autors -> daudzas grāmatas (@OneToMany).
 * mappedBy="author" nozīmē: attiecības "īpašnieks" ir Book.author lauks (tur ir FK kolonna author_id).
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB auto-increment
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // @OneToMany pēc noklusējuma ir LAZY — grāmatas ielādē tikai tad, kad tām piekļūst
    @OneToMany(mappedBy = "author")
    private List<Book> books = new ArrayList<>();

    protected Author() {
        // JPA prasa bezparametru konstruktoru (var būt protected)
    }

    public Author(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public List<Book> getBooks() { return books; }

    public void rename(String name) { this.name = name; }
}
