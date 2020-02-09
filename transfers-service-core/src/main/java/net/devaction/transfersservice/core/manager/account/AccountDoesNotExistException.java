package net.devaction.transfersservice.core.manager.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountDoesNotExistException extends Exception {
    private static final long serialVersionUID = 1359729895168821228L;

    public AccountDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
