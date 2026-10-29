package lv.macibas.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BorrowRequest(
        @NotBlank(message = "Lasītāja vārds ir obligāts")
        @Size(max = 100)
        String borrowerName
) {}
