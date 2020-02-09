package net.devaction.transfersservice.core.transfersmanager;

import net.devaction.transfersservice.api.entity.transfer.Transfer;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public interface TransferChecker {

    public void checkTransfer(Transfer transfer) throws InvalidAccountIdException, InvalidCurrencyException,
            InvalidAmountException, InvalidTimestampException;

    public void checkAccountId(String accountId) throws InvalidAccountIdException;

    void checkTransferId(String transferId) throws InvalidTransferIdException;

    void checkAmount(long amount) throws InvalidAmountException;

    public void checkCurrency(String currency) throws InvalidCurrencyException;

    void checkTimestamp(long epochMilli) throws InvalidTimestampException;
}
