package net.devaction.transfersservice.core.manager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class InvalidTimestampException extends Exception {
    private static final long serialVersionUID = 1359729895168821228L;

    InvalidTimestampException(String errorMessage) {
        super(errorMessage);
    }
}
