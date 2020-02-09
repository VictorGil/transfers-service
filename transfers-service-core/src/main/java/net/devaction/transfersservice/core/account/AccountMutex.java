package net.devaction.transfersservice.core.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 * 
 * The reason for using this Enum as a mutex lock instead of
 * just an "Object" is that it may happen that while processing
 * a "delete/close account" request, there may be other threads waiting
 * to add a transaction related to this account and we want to let
 * them know that the account is being closed.
 */
public enum AccountMutex {

    ACCOUNT_IS_OPEN,
    ACCOUNT_HAS_BEEN_CLOSED
}
