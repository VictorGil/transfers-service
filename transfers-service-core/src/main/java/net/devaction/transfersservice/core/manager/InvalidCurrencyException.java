package net.devaction.transfersservice.core.manager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class InvalidCurrencyException extends Exception {
    private static final long serialVersionUID = 1359729895168821228L;

    InvalidCurrencyException(String errorMessage) {
        super(errorMessage);
    }
}
