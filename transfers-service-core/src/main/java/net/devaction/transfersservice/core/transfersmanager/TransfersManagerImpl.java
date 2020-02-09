package net.devaction.transfersservice.core.transfersmanager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.devaction.transfersservice.api.entity.account.AccountType;
import static net.devaction.transfersservice.api.entity.account.AccountType.INTERNAL;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.AccountMutex;
import net.devaction.transfersservice.core.account.AmountTooBigException;
import net.devaction.transfersservice.core.account.NotEnoughBalanceException;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.accountsmanager.AccountDoesNotExistException;
import net.devaction.transfersservice.core.accountsmanager.AccountIsAlreadyBeingClosedException;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransfersManagerImpl implements TransfersManager {
    private static final Logger log = LoggerFactory.getLogger(TransfersManagerImpl.class);

    private final Map<String, Account> accountMap;
    private final TransferChecker transferChecker;

    @Inject
    public TransfersManagerImpl(Map<String, Account> accountMap,
            TransferChecker transferChecker) {

        this.accountMap = accountMap;
        this.transferChecker = transferChecker;
    }

    @Override
    public void processTransfer(Transfer transfer)
            throws AccountDoesNotExistException, UnableToObtainMutexException, NotEnoughBalanceException,
            InvalidAccountIdException, InvalidCurrencyException, InvalidAmountException,
            InvalidTimestampException, AmountTooBigException, BothAccountsAreExternalException,
            AccountIsAlreadyBeingClosedException {

        log.debug("New \"Transfer\" object to be processed:\n{}", transfer);
        transferChecker.checkTransfer(transfer);

        checkAccountExists(transfer.getSourceAccountId(), transfer.getSourceAccountType());
        checkAccountExists(transfer.getTargetAccountId(), transfer.getTargetAccountType());

        if (transfer.getSourceAccountType() == INTERNAL && transfer.getTargetAccountType() == INTERNAL) {
            processInternalTransfer(transfer);
        } else {
            processExternalTransfer(transfer);
        }
    }

    void processExternalTransfer(Transfer transfer) throws BothAccountsAreExternalException,
            UnableToObtainMutexException, NotEnoughBalanceException, AmountTooBigException,
            InvalidCurrencyException, AccountIsAlreadyBeingClosedException {

        if (transfer.getSourceAccountType() == transfer.getTargetAccountType()) {
            String errorMessage = "Failed to process transfer, both the source account "
                    + "and the target account are external";
            log.error(errorMessage);
            throw new BothAccountsAreExternalException(errorMessage);
        }

        Account internalAccount = null;
        if (transfer.getSourceAccountType() == INTERNAL) {
            internalAccount = accountMap.get(transfer.getSourceAccountId());
        } else {
            internalAccount = accountMap.get(transfer.getTargetAccountId());
        }

        log.trace("Going to try to grab the mutex lock object for the (internal) account, id: {}",
                internalAccount.getId());
        AccountMutex internalAccountMutex = internalAccount.getMutex();

        if (internalAccountMutex == AccountMutex.ACCOUNT_HAS_BEEN_CLOSED) {
            internalAccount.returnMutex(internalAccountMutex);
            String errorMessage = "Account with id \"" + internalAccount.getId()
                    + "\" is already being closed. Its mutex has been released";
            log.error(errorMessage);
            throw new AccountIsAlreadyBeingClosedException(errorMessage);
        }

        try {
            internalAccount.add(transfer);
            log.trace("Successful external transfer processing");
        } finally {
            internalAccount.returnMutex(internalAccountMutex);
            log.trace("Mutex lock object for internal account id \"{}\" has been released",
                    internalAccount.getId());
        }
    }

    void processInternalTransfer(Transfer transfer) throws UnableToObtainMutexException,
            NotEnoughBalanceException, AmountTooBigException, InvalidCurrencyException,
            AccountIsAlreadyBeingClosedException {

        Account sourceAccount = accountMap.get(transfer.getSourceAccountId());

        Account targetAccount = accountMap.get(transfer.getTargetAccountId());

        // First we need to grab both mutex objects, one for each of the accounts involved
        log.trace("Going to try to grab the mutex lock object for the source (internal) account: \"{}\"",
                sourceAccount.getId());
        AccountMutex sourceAccountMutex = sourceAccount.getMutex();

        if (sourceAccountMutex == AccountMutex.ACCOUNT_HAS_BEEN_CLOSED) {
            sourceAccount.returnMutex(sourceAccountMutex);
            String errorMessage = "Source account with id \"" + sourceAccount.getId() + "\" is already being closed"
                    + ". Its mutex has been released";
            log.error(errorMessage);
            throw new AccountIsAlreadyBeingClosedException(errorMessage);
        }

        AccountMutex targetAccountMutex = null;
        log.trace("Going to try to grab the mutex lock object for the target (internal) account: \"{}\"",
                targetAccount.getId());
        try {
            targetAccountMutex = targetAccount.getMutex();
        } catch (UnableToObtainMutexException ex) {
            sourceAccount.returnMutex(sourceAccountMutex);
            log.trace("The mutex lock object for the (internal) source account id \"{}\" has been released",
                    sourceAccount.getId());
            throw ex;
        }

        if (targetAccountMutex == AccountMutex.ACCOUNT_HAS_BEEN_CLOSED) {
            sourceAccount.returnMutex(sourceAccountMutex);
            targetAccount.returnMutex(targetAccountMutex);
            String errorMessage = "Target account with id \"" + targetAccount.getId() + "\" is already being closed"
                    + ". Its mutex has been released and also the source account mutex";
            log.error(errorMessage);
            throw new AccountIsAlreadyBeingClosedException(errorMessage);
        }

        try {
            sourceAccount.add(transfer);
            targetAccount.add(transfer);
        } finally {
            sourceAccount.returnMutex(sourceAccountMutex);
            targetAccount.returnMutex(targetAccountMutex);
            log.trace("Successful internal transfer processing, both mutex "
                    + "lock objects for internal account ids \"{}\" and \"{}\" have been released",
                    sourceAccount.getId(), targetAccount.getId());
        }
    }

    void checkAccountExists(String accountId, AccountType accountType) throws AccountDoesNotExistException {
        if (accountType == INTERNAL && !accountMap.containsKey(accountId)) {
            String errorMessage = "Failed to process transfer, internal account with id \"" + accountId + "\" does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }
    }
}
