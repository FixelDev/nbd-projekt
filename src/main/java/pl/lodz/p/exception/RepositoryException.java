package pl.lodz.p.exception;

public class RepositoryException extends RuntimeException{
    public RepositoryException(String errorMessage) {
        super(errorMessage);
    }
}
