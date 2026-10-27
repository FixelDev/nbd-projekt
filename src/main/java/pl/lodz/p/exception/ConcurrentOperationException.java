package pl.lodz.p.exception;

public class ConcurrentOperationException extends RepositoryException {
    public ConcurrentOperationException(String message) {
        super(message);
    }


}
