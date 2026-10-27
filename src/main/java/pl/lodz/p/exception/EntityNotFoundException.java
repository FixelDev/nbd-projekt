package pl.lodz.p.exception;

public class EntityNotFoundException extends RepositoryException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
