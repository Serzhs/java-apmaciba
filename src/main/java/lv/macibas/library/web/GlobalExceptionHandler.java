package lv.macibas.library.web;

import lv.macibas.library.exception.BusinessRuleException;
import lv.macibas.library.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ⚛️ React: kā <ErrorBoundary> — noķer kļūdas no VISIEM kontrolieriem vienuviet.
 *
 * CENTRALIZĒTA KĻŪDU APSTRĀDE — viena vieta, kas pārvērš izņēmumus HTTP atbildēs.
 * Kontrolieriem nav jābūt pilniem ar try/catch.
 *
 * Atbildes formāts: RFC 9457 "Problem Details" (application/problem+json):
 *   { "type": "about:blank", "title": "Not Found", "status": 404, "detail": "Grāmata ar id=99 nav atrasts", "instance": "/api/books/99" }
 *
 * ResponseEntityExceptionHandler jau apstrādā standarta Spring MVC kļūdas (slikts JSON -> 400,
 * nepareiza HTTP metode -> 405 utt.), mēs pievienojam savējās.
 *
 * ⚠️ Drošība: 500 kļūdās NEKAD neatgriez stack trace vai iekšējos ziņojumus klientam.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // @Version konflikts — kāds cits paspēja izmainīt to pašu ierakstu
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Ierakstu vienlaikus mainīja kāds cits. Mēģini vēlreiz.");
    }

    // DB ierobežojums (unique, FK) — pēdējā aizsardzības līnija, ja servisa pārbaude palaida garām (race condition)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("DB ierobežojuma pārkāpums: {}", ex.getMostSpecificCause().getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Dati konfliktē ar esošiem ierakstiem");
    }

    // @Valid kļūdas -> 400 ar katra lauka kļūdu
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Pieprasījumā ir nederīgi lauki");
        problem.setTitle("Validācijas kļūda");
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // Viss pārējais — 500. Logojam pilnu kļūdu sev, klientam — vispārīgu ziņu.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Negaidīta kļūda", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Radās iekšēja kļūda");
    }
}
