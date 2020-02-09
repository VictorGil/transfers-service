package net.devaction.transfersservice.core.transfersmanager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class InvalidTransferIdException extends Exception {
    private static final long serialVersionUID = 1359729895168821228L;

    InvalidTransferIdException(String errorMessage) {
        super(errorMessage);
    }
}
