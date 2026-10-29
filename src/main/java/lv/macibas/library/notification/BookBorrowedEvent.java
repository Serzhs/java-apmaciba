package lv.macibas.library.notification;

import java.time.LocalDate;

/** Domēna notikums — "kaut kas notika". Observer šablons Spring stilā. */
public record BookBorrowedEvent(Long loanId, String bookTitle, String borrowerName, LocalDate dueDate) {}
