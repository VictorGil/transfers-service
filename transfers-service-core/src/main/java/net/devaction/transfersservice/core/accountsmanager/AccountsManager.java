package net.devaction.transfersservice.core.accountsmanager;

import net.devaction.transfersservice.api.entity.account.AccountInfo;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.transfersmanager.InvalidAccountIdException;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public interface AccountsManager {

    public String openNewAccount(String currency) throws InvalidCurrencyException;

    public void closeAccount(String accountId) throws AccountDoesNotExistException, InvalidAccountIdException,
            UnableToObtainMutexException, AccountIsAlreadyBeingClosedException;

    public long getBalance(String accountId) throws InvalidAccountIdException, AccountDoesNotExistException;

    public AccountInfo getAccountInfo(String accountId) throws InvalidAccountIdException, AccountDoesNotExistException,
            UnableToObtainMutexException;
}
