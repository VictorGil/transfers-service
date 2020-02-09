package net.devaction.transfersservice.core.transfersmanager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

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
            InvalidTimestampException, AmountTooBigException {

        log.debug("New \"Transfer\" object to be processed:\n{}", transfer);
        transferChecker.checkTransfer(transfer);

        Account sourceAccount = accountMap.get(transfer.getSourceAccountId());
        if (sourceAccount == null) {
            String errorMessage = "Failed to process transfer, account with id " + transfer.getSourceAccountId() + " does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }

        Account targetAccount = accountMap.get(transfer.getTargetAccountId());
        if (targetAccount == null) {
            String errorMessage = "Failed to process transfer, account with id " + transfer.getTargetAccountId() + " does not exist";
            log.error(errorMessage);
            throw new AccountDoesNotExistException(errorMessage);
        }

        // First we need to grab both mutex objects, one for each of the accounts involved
        log.trace("Going to try to grab the mutex for the source account");
        Object sourceAccountMutex = sourceAccount.getMutex();
        Object targetAccountMutex = null;

        log.trace("Going to try to grab the mutex for the target account");
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
