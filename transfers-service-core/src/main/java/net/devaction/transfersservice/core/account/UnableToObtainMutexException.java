package net.devaction.transfersservice.core.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class UnableToObtainMutexException extends Exception {
 
    public UnableToObtainMutexException(String errorMessage) {
        super(errorMessage);
    }
}
