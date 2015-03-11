package ua.pp.condor.ioc.service.exception;

public class NoSuchEntityException extends RuntimeException {
    private static final long serialVersionUID = -2678506231796754868L;

    public NoSuchEntityException() {
    }

    public NoSuchEntityException(long id) {
        super("with id = " + id);
    }
}
