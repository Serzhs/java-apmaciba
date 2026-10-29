package lv.macibas.library.dto;

import lv.macibas.library.domain.Book;

/** Ko API ATGRIEŽ klientam. Mapping no Entity notiek servisa slānī (transakcijas iekšā). */
public record BookResponse(
        Long id,
        String title,
        String isbn,
        Integer publishedYear,
        boolean available,
        Long authorId,
        String authorName
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.isAvailable(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}
