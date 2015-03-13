package ua.pp.condor.ioc.service.exception;

public class NotEnoughMoneyException extends RuntimeException {
    private static final long serialVersionUID = -2678506231796754868L;

    public NotEnoughMoneyException() {
    }

    public NotEnoughMoneyException(String msg) {
        super(msg);
    }
}
