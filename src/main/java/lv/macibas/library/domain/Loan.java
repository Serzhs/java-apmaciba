package lv.macibas.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lv.macibas.library.exception.BusinessRuleException;

import java.time.LocalDate;

/** Grāmatas izsniegšana lasītājam. */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 100)
    private String borrowerName;

    @Column(nullable = false)
    private LocalDate borrowedAt;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnedAt; // null = vēl nav atgriezta

    protected Loan() {}

    public Loan(Book book, String borrowerName, LocalDate borrowedAt, LocalDate dueDate) {
        this.book = book;
        this.borrowerName = borrowerName;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public void markReturned(LocalDate date) {
        if (!isActive()) {
            throw new BusinessRuleException("Izsniegums " + id + " jau ir noslēgts");
        }
        this.returnedAt = date;
        book.markReturned();
    }

    public Long getId() { return id; }

    public Book getBook() { return book; }

    public String getBorrowerName() { return borrowerName; }

    public LocalDate getBorrowedAt() { return borrowedAt; }

    public LocalDate getDueDate() { return dueDate; }

    public LocalDate getReturnedAt() { return returnedAt; }
}
