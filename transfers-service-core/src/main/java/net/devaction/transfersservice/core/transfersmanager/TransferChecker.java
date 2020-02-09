package net.devaction.transfersservice.core.transfersmanager;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.accountsmanager.InvalidCurrencyException;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransferChecker {
    private static final Logger log = LoggerFactory.getLogger(TransferChecker.class);

    private static final Instant INSTANT_2020 = Instant.parse("2020-01-01T00:00:00.000Z");

    void checkTransfer(Transfer transfer) throws InvalidAccountIdException, InvalidCurrencyException,
            InvalidAmountException, InvalidTimestampException {

        checkAccountId(transfer.getSourceAccountId());
        checkAccountId(transfer.getTargetAccountId());
        checkCurrency(transfer.getCurrency());
        checkAmount(transfer.getAmount());
        checkTimestamp(transfer.getTimestamp());
    }

    public void checkAccountId(String accountId) throws InvalidAccountIdException {
        if (accountId == null || accountId.length() < 12 || accountId.length() > 100) {
            String errorMessage = "Invalid accountId: " + accountId;
            log.error(errorMessage);
            throw new InvalidAccountIdException(errorMessage);
        }
    }

    void checkTransferId(String transferId) throws InvalidTransferIdException {
        if (transferId == null || transferId.length() < 12 || transferId.length() > 100) {
            String errorMessage = "Invalid accountId: " + transferId;
            log.error(errorMessage);
            throw new InvalidTransferIdException(errorMessage);
        }
    }

    void checkAmount(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            String errorMessage = "Invalid amount: " + amount;
            log.error(errorMessage);
            throw new InvalidAmountException(errorMessage);
        }
    }

    public void checkCurrency(String currency) throws InvalidCurrencyException {
        if (currency == null || currency.length() < 3 || currency.length() > 100) {
            String errorMessage = "Invalid currency: " + currency;
            throw new InvalidCurrencyException(errorMessage);
        }
    }

    void checkTimestamp(long epochMilli) throws InvalidTimestampException {
        if (Instant.ofEpochMilli(epochMilli).isBefore(INSTANT_2020)) {
            String errorMessage = "Invalid timestamp: " + epochMilli;
            log.error(errorMessage);
            throw new InvalidTimestampException(errorMessage);
        }
    }
}
