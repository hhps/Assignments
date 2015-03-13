package ua.pp.condor.ioc.service.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NoSuchEntityException extends RuntimeException {
    private static final long serialVersionUID = -2678506231796754868L;

    public NoSuchEntityException() {
    }

    public NoSuchEntityException(long id) {
        super("with id = " + id);
    }

    public NoSuchEntityException(DataIntegrityViolationException e) {
        super(e);
    }
}
