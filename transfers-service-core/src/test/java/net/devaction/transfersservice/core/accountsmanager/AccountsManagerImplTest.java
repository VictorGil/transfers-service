package net.devaction.transfersservice.core.accountsmanager;

import org.junit.jupiter.api.Test;

import net.devaction.transfersservice.api.entity.account.AccountInfo;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.transfersmanager.InvalidAccountIdException;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;
import net.devaction.transfersservice.core.transfersmanager.TransferCheckerImpl;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
class AccountsManagerImplTest {
    private final AccountsManager manager = new AccountsManagerImpl(
            new ConcurrentHashMap<String, Account>(), new TransferCheckerImpl());

    @Test
    public void testOpenAndCloseAccount() {

        String accountId = null;
        try {
            accountId = manager.openNewAccount("USD");
        } catch (InvalidCurrencyException ex) {
            fail(InvalidCurrencyException.class.getSimpleName() + " was thrown");
        }

        assertThat(accountId).hasSize(12);

        try {
            manager.closeAccount(accountId);
        } catch (AccountDoesNotExistException | InvalidAccountIdException | UnableToObtainMutexException
                    | AccountIsAlreadyBeingClosedException ex) {

            fail(ex.getClass().getSimpleName() + " was thrown");
        }
    }

    @Test
    public void testGetBalanceAndAccountInfo() {

        String accountId = null;
        try {
            accountId = manager.openNewAccount("GBP");
        } catch (InvalidCurrencyException ex) {
            fail(InvalidCurrencyException.class.getSimpleName() + " was thrown");
        }

        assertThat(accountId).hasSize(12);

        long balance = -1;
        try {
            balance = manager.getBalance(accountId);
        } catch (InvalidAccountIdException | AccountDoesNotExistException ex) {
            fail(ex.getClass().getSimpleName() + " was thrown");
        }

        assertThat(balance).isEqualTo(0L);

        AccountInfo info = null;
        try {
            info = manager.getAccountInfo(accountId);
        } catch (InvalidAccountIdException | AccountDoesNotExistException
                | UnableToObtainMutexException ex) {

            fail(ex.getClass().getSimpleName() + " was thrown");
        }

        assertThat(info.getAccountId()).isEqualTo(accountId);
        assertThat(info.getBalance()).isEqualTo(0L);
        assertThat(info.getCurrency()).isEqualTo("GBP");
        assertThat(info.getHistory()).hasSize(0);
    }
}