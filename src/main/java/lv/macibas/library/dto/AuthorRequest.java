package lv.macibas.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "Vārds ir obligāts")
        @Size(max = 100)
        String name
) {}
