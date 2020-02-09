package net.devaction.transfersservice.core.transfersmanager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class BothAccountsAreExternalException extends Exception {
    private static final long serialVersionUID = 1359729895168821517L;

    BothAccountsAreExternalException(String errorMessage) {
        super(errorMessage);
    }
}
