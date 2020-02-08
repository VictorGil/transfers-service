package net.devaction.transfersservice.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.NotEnoughBalanceException;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountManager {
    private static final Logger log = LoggerFactory.getLogger(AccountManager.class);

    private Map<String, Account> accountMap = new ConcurrentHashMap<>();

    public String openNewAccount(String currency) {
        Account account = new Account(currency);
        accountMap.put(account.getId(), account);
        return account.getId();
    }

    public void processTransfer(Transfer transfer) throws AccountDoesNotExistException,
                UnableToObtainMutexException, NotEnoughBalanceException {

        Account sourceAccount = accountMap.get(transfer.getSourceAccountId());
        if (sourceAccount == null) {
            String errorMessage = "Account with id " + transfer.getSourceAccountId() + " does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }

        Account targetAccount = accountMap.get(transfer.getTargetAccountId());
        if (targetAccount == null) {
            String errorMessage = "Account with id " + transfer.getTargetAccountId() + " does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }

        // First we need to grab both mutexes, one for each of the accounts involved
        Object sourceAccountMutex = sourceAccount.getMutex();
        Object targetAccountMutex = null;

        try {
            targetAccount.getMutex();
        } catch (UnableToObtainMutexException ex) {
            sourceAccount.returnMutex(sourceAccountMutex);
            throw ex;
        }

        try {
            sourceAccount.add(transfer);
            targetAccount.add(transfer);
        } finally {
            sourceAccount.returnMutex(sourceAccountMutex);
            targetAccount.returnMutex(targetAccountMutex);
        }
    }
}
