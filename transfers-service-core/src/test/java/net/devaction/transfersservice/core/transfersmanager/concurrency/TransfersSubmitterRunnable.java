package net.devaction.transfersservice.core.transfersmanager.concurrency;

import static net.devaction.transfersservice.api.entity.account.AccountType.INTERNAL;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.transfersmanager.TransfersManager;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransfersSubmitterRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TransfersSubmitterRunnable.class);

    private final TransfersManager transfersManager;
    private final List<AccountIndexPair> accountIndexPairs;
    private final long amount;
    private final String currency;
    private final Map<Integer, String> indexAccountMap;

    public TransfersSubmitterRunnable(TransfersManager transfersManager,
            List<AccountIndexPair> accountIndexPairs, long amount, 
            String currency, Map<Integer, String> indexAccountMap) {

        this.transfersManager = transfersManager;
        this.accountIndexPairs = accountIndexPairs;
        this.amount = amount;
        this.currency = currency;
        this.indexAccountMap = indexAccountMap;
    }

    @Override
    public void run() {

        for (AccountIndexPair accountPair : accountIndexPairs) {
            Transfer transfer = new Transfer(indexAccountMap.get(accountPair.getSourceAccountIndex()),
                    INTERNAL, indexAccountMap.get(accountPair.getTargetAccountIndex()), INTERNAL,
                    amount, currency);

            try {
                transfersManager.processTransfer(transfer);
            } catch (Exception ex) {
                log.error("{}", ex, ex);
            }
        }
    }
}
