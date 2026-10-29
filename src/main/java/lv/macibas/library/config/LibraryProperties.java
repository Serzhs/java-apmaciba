package lv.macibas.library.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Tipizēta konfigurācija no application.yml (prefikss "library").
 * library.max-active-loans-per-borrower -> maxActiveLoansPerBorrower (relaxed binding)
 *
 * @ConfigurationProperties vs @Value:
 *  - @Value("${library.loan-period-days}") — viena vērtība, ok mazām lietām
 *  - @ConfigurationProperties — grupa, tipu drošība, validācija, vieglāk testēt. Ieteicams.
 *
 * @Validated -> aplikācija NEPALAIDĪSIES, ja konfigurācija nederīga ("fail fast").
 */
@Validated
@ConfigurationProperties(prefix = "library")
public record LibraryProperties(
        @Min(1) @Max(20) int maxActiveLoansPerBorrower,
        @Min(1) @Max(90) int loanPeriodDays
) {}
