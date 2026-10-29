package lv.macibas.library.repository;

import lv.macibas.library.domain.Loan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // WHERE borrower_name = ? AND returned_at IS NULL
    long countByBorrowerNameAndReturnedAtIsNull(String borrowerName);

    @EntityGraph(attributePaths = "book")
    List<Loan> findByBorrowerNameAndReturnedAtIsNullOrderByDueDateAsc(String borrowerName);

    boolean existsByBookId(Long bookId);
}
