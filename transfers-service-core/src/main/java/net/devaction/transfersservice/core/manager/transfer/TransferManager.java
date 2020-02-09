package net.devaction.transfersservice.core.manager.transfer;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.account.AmountTooBigException;
import net.devaction.transfersservice.core.account.NotEnoughBalanceException;
import net.devaction.transfersservice.core.account.UnableToObtainMutexException;
import net.devaction.transfersservice.core.manager.account.AccountDoesNotExistException;
import net.devaction.transfersservice.core.manager.account.InvalidCurrencyException;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public interface TransferManager {

    public void processTransfer(Transfer transfer) throws AccountDoesNotExistException,
            UnableToObtainMutexException, NotEnoughBalanceException, InvalidAccountIdException,
            InvalidCurrencyException, InvalidAmountException, InvalidTimestampException,
            AmountTooBigException;
}
