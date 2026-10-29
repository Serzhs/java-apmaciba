package lv.macibas.library.dto;

import lv.macibas.library.domain.Author;

public record AuthorResponse(Long id, String name) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}
