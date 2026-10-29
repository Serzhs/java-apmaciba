package lv.macibas.library.dto;

import lv.macibas.library.domain.Loan;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String borrowerName,
        LocalDate borrowedAt,
        LocalDate dueDate,
        LocalDate returnedAt
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBorrowerName(),
                loan.getBorrowedAt(),
                loan.getDueDate(),
                loan.getReturnedAt()
        );
    }
}
