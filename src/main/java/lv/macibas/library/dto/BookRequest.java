package lv.macibas.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ⚛️ React: tas ir tieši tas JSON, ko tu sūti ar fetch(url, { body: JSON.stringify(...) }).
 *    @NotBlank utt. — kā Zod/Yup validācija, bet serverī.
 *
 * DTO (Data Transfer Object) — ko klients SŪTA uz API.
 *
 * Kāpēc nevis tieši Entity?
 *  - Drošība: klients nevar uzstādīt laukus, ko nedrīkst (id, version, available) — "mass assignment"
 *  - API līgums nav piesiets DB struktūrai — var mainīt tabulas, nesalaužot klientus
 *  - Validācija atdalīta no persistences
 *  - Nav lazy loading / bezgalīgas rekursijas problēmu JSON serializācijā
 *
 * Validāciju iedarbina @Valid kontrolierī. Kļūdas gadījumā -> 400 Bad Request.
 */
public record BookRequest(
        @NotBlank(message = "Nosaukums ir obligāts")
        @Size(max = 200, message = "Nosaukums max 200 simboli")
        String title,

        @NotBlank(message = "ISBN ir obligāts")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN jābūt 10 vai 13 cipariem")
        String isbn,

        @Min(value = 1450, message = "Gads nevar būt pirms iespiedmašīnas")
        @Max(value = 2100, message = "Gads nav reāls")
        Integer publishedYear,

        @NotNull(message = "authorId ir obligāts")
        Long authorId
) {}
