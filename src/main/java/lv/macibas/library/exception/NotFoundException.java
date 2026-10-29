package lv.macibas.library.exception;

/** Resurss nav atrasts -> HTTP 404 (skat. GlobalExceptionHandler). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super(resource + " ar id=" + id + " nav atrasts");
    }
}
