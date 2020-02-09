package net.devaction.transfersservice.core.accountsmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.transfersmanager.InvalidAccountIdException;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;
import net.devaction.transfersservice.core.transfersmanager.TransferChecker;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountsManager {
    private static final Logger log = LoggerFactory.getLogger(AccountsManager.class);

    private final TransferChecker transferChecker = new TransferChecker();

    private final Map<String, Account> accountMap = new ConcurrentHashMap<>();

    public AccountsManager(TransferChecker transferChecker, Map<String, Account> accountMap) {
        
    }

    public String openNewAccount(String currency) throws InvalidCurrencyException {
        log.trace("Going to open a new account, currency: {}", currency);
        transferChecker.checkCurrency(currency);

        Account account = new Account(currency);
        accountMap.put(account.getId(), account);

        String accountId = account.getId();
        log.trace("New account has been created, account id: {}", accountId);
        return accountId;
    }

    public void closeAccount(String accountId) throws AccountDoesNotExistException, InvalidAccountIdException {
        log.trace("Going to close the account with id: \"{}\"", accountId);
        transferChecker.checkAccountId(accountId);

        if (!accountMap.containsKey(accountId)) {
            String errorMessage = "Failed to close account, account with id \"" + accountId + "\" does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }
        accountMap.remove(accountId);

        log.trace("Account with id \"{}\" has been closed", accountId);
    }
}
