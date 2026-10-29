package lv.macibas.library.exception;

/**
 * Pārkāpts biznesa likums (grāmata jau izsniegta, ISBN aizņemts...) -> HTTP 409 Conflict.
 * Unchecked (RuntimeException), lai @Transactional automātiski izdarītu rollback.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
