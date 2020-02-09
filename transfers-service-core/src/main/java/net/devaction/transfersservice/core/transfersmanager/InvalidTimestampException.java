package net.devaction.transfersservice.core.transfersmanager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class InvalidTimestampException extends Exception {
    private static final long serialVersionUID = 1359729895168821009L;

    InvalidTimestampException(String errorMessage) {
        super(errorMessage);
    }
}
