package net.devaction.transfersservice.core.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class NotEnoughBalanceException extends Exception {
    private static final long serialVersionUID = 1359729895168821228L;

    NotEnoughBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
