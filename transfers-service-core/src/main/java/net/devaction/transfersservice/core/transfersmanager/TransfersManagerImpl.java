package net.devaction.transfersservice.core.transfersmanager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.devaction.transfersservice.api.entity.account.AccountType;
import static net.devaction.transfersservice.api.entity.account.AccountType.INTERNAL;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.account.AmountTooBigException;
import net.devaction.transfersservice.core.account.NotEnoughBalanceException;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.accountsmanager.AccountDoesNotExistException;

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
            InvalidTimestampException, AmountTooBigException, BothAccountsAreExternalException {

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
            InvalidCurrencyException {

        if (transfer.getSourceAccountType() == transfer.getTargetAccountType()) {
            String errorMessage = "Failed to process thransfer, both the source account "
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

        log.trace("Going to try to grab the mutex lock object for the internal account, id: {}",
                internalAccount.getId());
        Object internalAccountMutex = internalAccount.getMutex();
        try {
            internalAccount.add(transfer);
        } finally {
            internalAccount.returnMutex(internalAccountMutex);
            log.trace("Mutex lock object for internal account id \"{}\" has been released",
                    internalAccount.getId());
        }
    }

    void processInternalTransfer(Transfer transfer) throws UnableToObtainMutexException,
            NotEnoughBalanceException, AmountTooBigException, InvalidCurrencyException {

        Account sourceAccount = accountMap.get(transfer.getSourceAccountId());

        Account targetAccount = accountMap.get(transfer.getTargetAccountId());

        // First we need to grab both mutex objects, one for each of the accounts involved
        log.trace("Going to try to grab the mutex lock object for the source (internal) account: \"{}\"",
                sourceAccount.getId());
        Object sourceAccountMutex = sourceAccount.getMutex();

        Object targetAccountMutex = null;
        log.trace("Going to try to grab the mutex lock object for the target (internal) account: \"{}\"",
                targetAccount.getId());
        try {
            targetAccountMutex = targetAccount.getMutex();
        } catch (UnableToObtainMutexException ex) {
            sourceAccount.returnMutex(sourceAccountMutex);
            log.trace("The mutex lock object for the internal source account id \"{}\" has been released",
                    sourceAccount.getId());
            throw ex;
        }

        try {
            sourceAccount.add(transfer);
            targetAccount.add(transfer);
        } finally {
            sourceAccount.returnMutex(sourceAccountMutex);
            targetAccount.returnMutex(targetAccountMutex);
            log.trace("Both mutex lock objects for internal account ids \"{}\" and \"{}\" have been released",
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
