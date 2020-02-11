package net.devaction.transfersservice.core.transfersmanager;

import org.junit.jupiter.api.Test;

import static net.devaction.transfersservice.api.entity.account.AccountType.INTERNAL;
import static net.devaction.transfersservice.api.entity.account.AccountType.EXTERNAL;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.AmountTooBigException;
import net.devaction.transfersservice.core.account.NotEnoughBalanceException;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.accountsmanager.AccountDoesNotExistException;
import net.devaction.transfersservice.core.accountsmanager.AccountIsAlreadyBeingClosedException;
import net.devaction.transfersservice.core.accountsmanager.AccountsManager;
import net.devaction.transfersservice.core.accountsmanager.AccountsManagerImpl;
import net.devaction.transfersservice.core.transfersmanager.InvalidAccountIdException;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;
import net.devaction.transfersservice.core.transfersmanager.TransferCheckerImpl;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
class TransfersManagerImplTest {

    private final TransfersManager transfersManager;
    private final AccountsManager accountsManager;

    public TransfersManagerImplTest() {
        Map<String, Account> accountMap = new ConcurrentHashMap<>();
        TransferChecker transferChecker = new TransferCheckerImpl();

        transfersManager = new TransfersManagerImpl(accountMap, transferChecker);
        accountsManager = new AccountsManagerImpl(accountMap, transferChecker);
    }

    @Test
    public void testProcessExternalTransfer() {

        String accountId = null;
        try {
            accountId = accountsManager.openNewAccount("EUR");
        } catch (InvalidCurrencyException ex) {
            fail(InvalidCurrencyException.class.getSimpleName() + " was thrown");
            return;
        }

        Transfer transfer = new Transfer("external-account-3bx1", EXTERNAL, accountId, INTERNAL,
                100000L, "EUR");

        assertThat(transfer.getId()).hasSize(12);

        try {
            transfersManager.processTransfer(transfer);
        } catch (AccountDoesNotExistException | UnableToObtainMutexException | NotEnoughBalanceException | InvalidAccountIdException
                | InvalidCurrencyException | InvalidAmountException | InvalidTimestampException | AmountTooBigException
                | BothAccountsAreExternalException | AccountIsAlreadyBeingClosedException ex) {

            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        long balance = -1;
        try {
            balance = accountsManager.getBalance(accountId);
        } catch (InvalidAccountIdException | AccountDoesNotExistException ex) {
            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        assertThat(balance).isEqualTo(transfer.getAmount());
    }

    @Test
    public void testProcessInternalTransfer() {

        String accountId1 = null;
        String accountId2 = null;
        try {
            accountId1 = accountsManager.openNewAccount("AUD");
            accountId2 = accountsManager.openNewAccount("AUD");
        } catch (InvalidCurrencyException ex) {
            fail(InvalidCurrencyException.class.getSimpleName() + " was thrown");
            return;
        }

        Transfer externalTransfer1 = new Transfer("external-account-3c2f", EXTERNAL, accountId1, INTERNAL,
                50000L, "AUD");

        Transfer externalTransfer2 = new Transfer("external-account-0b18", EXTERNAL, accountId2, INTERNAL,
                30000L, "AUD");

        Transfer internalTransfer = new Transfer(accountId1, INTERNAL, accountId2, INTERNAL,
                15000L, "AUD");
        try {
            transfersManager.processTransfer(externalTransfer1);
            transfersManager.processTransfer(externalTransfer2);
            transfersManager.processTransfer(internalTransfer);
        } catch (AccountDoesNotExistException | UnableToObtainMutexException | NotEnoughBalanceException | InvalidAccountIdException
                | InvalidCurrencyException | InvalidAmountException | InvalidTimestampException | AmountTooBigException
                | BothAccountsAreExternalException | AccountIsAlreadyBeingClosedException ex) {

            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        long balance1 = -1;
        long balance2 = -1;
        try {
            balance1 = accountsManager.getBalance(accountId1);
            balance2 = accountsManager.getBalance(accountId2);
        } catch (InvalidAccountIdException | AccountDoesNotExistException ex) {
            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        assertThat(balance1).isEqualTo(35000L);
        assertThat(balance2).isEqualTo(45000L);
    }
}
