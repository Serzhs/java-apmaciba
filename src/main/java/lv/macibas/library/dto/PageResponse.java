package lv.macibas.library.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stabils JSON formāts lapošanai (pagination).
 * Tieši atgriezt Spring Page<T> nav ieteicams — tā JSON struktūra nav garantēta starp versijām.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
