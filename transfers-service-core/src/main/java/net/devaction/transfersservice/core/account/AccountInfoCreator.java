package net.devaction.transfersservice.core.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.account.AccountInfo;

/**
 * @author Víctor Gil
 *
 * since February 2020
 * 
 * The purpose of this class is to decouple the public API
 * and the core.
 */
public class AccountInfoCreator {
    private static final Logger log = LoggerFactory.getLogger(AccountInfoCreator.class);

    AccountInfo create(Account account) {
        return new AccountInfo(account.getId(), account.getCurrency(),
                account.getBalance(), account.getHistory());
    }
}
