package net.devaction.transfersservice.core.accountsmanager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.devaction.transfersservice.api.entity.account.AccountInfo;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.AccountMutex;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.transfersmanager.InvalidAccountIdException;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;
import net.devaction.transfersservice.core.transfersmanager.TransferChecker;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountsManagerImpl implements AccountsManager {
    private static final Logger log = LoggerFactory.getLogger(AccountsManagerImpl.class);

    private final Map<String, Account> accountMap;
    private final TransferChecker transferChecker;

    @Inject
    public AccountsManagerImpl(Map<String, Account> accountMap, TransferChecker transferChecker) {
        this.accountMap = accountMap;
        this.transferChecker = transferChecker;
    }

    @Override
    public String openNewAccount(String currency) throws InvalidCurrencyException {

        log.trace("Going to open a new account, currency: {}", currency);
        transferChecker.checkCurrency(currency);

        Account account = new Account(currency);
        accountMap.put(account.getId(), account);

        String accountId = account.getId();
        log.trace("New account has been created, account id: {}", accountId);
        return accountId;
    }

    @Override
    public void closeAccount(String accountId) throws AccountDoesNotExistException, InvalidAccountIdException,
            UnableToObtainMutexException, AccountIsAlreadyBeingClosedException {

        log.trace("Going to close the account with id: \"{}\"", accountId);
        checkAccountExists(accountId);

        Account account = accountMap.get(accountId);
        log.trace("Going to try to grab the mutex lock object for the account which is going to be closed, id: {}", accountId);
        AccountMutex mutex = null;
        mutex = account.getMutex();

        if (mutex == AccountMutex.ACCOUNT_HAS_BEEN_CLOSED) {
            account.returnMutex(AccountMutex.ACCOUNT_HAS_BEEN_CLOSED);
            log.trace("The mutex lock object for the internal source account id \"{}\" has been released",
                    accountId);
            String errorMessage = "Account with id \"" + accountId + "\" is already being closed";
            log.error(errorMessage);
            throw new AccountIsAlreadyBeingClosedException(errorMessage);
        }

        accountMap.remove(accountId);

        account.returnMutex(AccountMutex.ACCOUNT_HAS_BEEN_CLOSED);
        log.trace("Account with id \"{}\" has been closed and its mutex has been released", accountId);
    }

    @Override
    public long getBalance(String accountId) throws InvalidAccountIdException, AccountDoesNotExistException {

        log.trace("Going to get the balance of the account with id \"{}\"", accountId);
        checkAccountExists(accountId);

        long balance = accountMap.get(accountId).getBalance();

        log.trace("Current balance of \"{}\" account in cents: {}", accountId, balance);
        return balance;
    }

    @Override
    public AccountInfo getAccountInfo(String accountId) throws InvalidAccountIdException,
            AccountDoesNotExistException, UnableToObtainMutexException {

        log.trace("Going to get the info and history of the account with id \"{}\"", accountId);
        checkAccountExists(accountId);

        Account account = accountMap.get(accountId);

        return account.getAccountInfo();
    }

    @Override
    public Set<String> getAllAccountIds() {
        log.trace("Going to get the ids of all the accounts");
        Set<String> accountIdsView = accountMap.keySet();

        // We use "defensive copying" because we want to decouple the set from the map
        Set<String> accountIds = Collections.unmodifiableSet(new TreeSet<>(accountIdsView));

        log.trace("All account ids: {}", accountIds);
        return accountIds;
    }

    void checkAccountExists(String accountId) throws InvalidAccountIdException, AccountDoesNotExistException {
        transferChecker.checkAccountId(accountId);

        if (!accountMap.containsKey(accountId)) {
            String errorMessage = "Account with id \"" + accountId + "\" does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }
    }
}
