package net.devaction.transfersservice.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.core.account.Account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountManager {
    private static final Logger log = LoggerFactory.getLogger(AccountManager.class);

    private Map<String, Account> accountMap = new ConcurrentHashMap();

    public String openNewAccount(String currency) {
        return null;
    }

}
