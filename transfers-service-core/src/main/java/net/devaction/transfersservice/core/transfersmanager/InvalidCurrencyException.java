package net.devaction.transfersservice.core.transfersmanager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class InvalidCurrencyException extends Exception {
    private static final long serialVersionUID = 1359729895168821901L;

    public InvalidCurrencyException(String errorMessage) {
        super(errorMessage);
    }
}
