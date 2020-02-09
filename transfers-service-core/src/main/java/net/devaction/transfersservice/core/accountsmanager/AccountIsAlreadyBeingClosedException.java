package net.devaction.transfersservice.core.accountsmanager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountIsAlreadyBeingClosedException extends Exception {

    private static final long serialVersionUID = -1584628854777887663L;

    public AccountIsAlreadyBeingClosedException(String errorMessage) {
        super(errorMessage);
    }
}
