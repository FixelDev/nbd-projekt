package pl.lodz.p.exception;

public class ConcurrentOperationException extends RuntimeException {
    public ConcurrentOperationException(String message) {
        super(message);
    }


}
