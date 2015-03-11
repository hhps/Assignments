package ua.pp.condor.ioc.service.exception;

public class IllegalEntityStateException extends RuntimeException {
    private static final long serialVersionUID = -2678506231796754868L;

    public IllegalEntityStateException() {
    }

    public IllegalEntityStateException(String msg) {
        super(msg);
    }
}
