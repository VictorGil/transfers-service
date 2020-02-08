package net.devaction.transfersservice.core.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class UnableToObtainMutexException extends Exception {

    private static final long serialVersionUID = -1584628854777887663L;

    UnableToObtainMutexException(String errorMessage) {
        super(errorMessage);
    }
}
