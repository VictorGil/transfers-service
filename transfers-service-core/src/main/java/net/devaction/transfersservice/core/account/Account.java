package net.devaction.transfersservice.core.account;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.account.AccountHistoryItem;
import net.devaction.transfersservice.api.entity.account.Direction;
import net.devaction.transfersservice.api.entity.transfer.Transfer;

import static net.devaction.transfersservice.api.entity.account.Direction.RECEIVED;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Account {
    private static final Logger log = LoggerFactory.getLogger(Account.class);

    private final String id;
    private final String currency;
    private long balance;
    private final List<AccountHistoryItem> history = new LinkedList<>();

    // This is used for concurrency, to avoid race conditions
    private BlockingQueue<Object> mutexQueue = new LinkedBlockingQueue<>(1);
    private final Random randomGenerator = new Random();

    public Account(String id, String currency) {
        this.id = id;
        this.currency = currency;
    }

    public void add(Transfer transfer) {
        // This should never happen because we should have checked before
        if (!transfer.getCurrency().equals(currency)) {
            String errorMessage = "The transfer currency does not match this "
                    + " account currency: " + transfer.getCurrency() + " vs "
                    + currency;
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        AccountHistoryItem historyItem = createHistoryItem(transfer);

        balance = updateBalance(balance, historyItem.getAmount(),
                historyItem.getDirection());

        history.add(historyItem);
    }

    long updateBalance(long balance, long amount, Direction direction) {

        long updatedBalance;
        if (direction == RECEIVED) {
            updatedBalance = balance + amount;
        } else {
            updatedBalance = balance - amount;
        }

        // This should never happen because we should have checked before
        if (updatedBalance < 0) {
            String errorMessage = "The account balance cannot be lower than 0";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return updatedBalance;
    }

    AccountHistoryItem createHistoryItem(Transfer transfer) {
        String counterpartyId = extractCounterpartyId(transfer);
        Direction direction = extractDirection(transfer);

        return new AccountHistoryItem(transfer.getId(),
                counterpartyId, transfer.getAmount(), direction, transfer.getTimestamp());
    }

    String extractCounterpartyId(Transfer transfer) {
        if (transfer.getSourceAccountId().equals(id)) {
            return transfer.getTargetAccountId();
        }

        if (transfer.getTargetAccountId().equals(id)) {
            return transfer.getTargetAccountId();
        }

        String errorMessage = "Either the source or the target account id should match "
                + "this account id. Transfer:\n" + transfer;
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    Direction extractDirection(Transfer transfer) {
        if (transfer.getSourceAccountId().equals(id)) {
            return Direction.SENT;
        }

        if (transfer.getTargetAccountId().equals(id)) {
            return Direction.RECEIVED;
        }

        String errorMessage = "Either the source or the target account id should match "
                + "this account id. Transfer:\n" + transfer;
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    public String getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public long getBalance() {
        return balance;
    }

    public List<AccountHistoryItem> getHistory() {
        // We use "defensive copying" because we want to decouple the two lists
        return Collections.unmodifiableList(new LinkedList<>(history));
    }

    public Object getMutex() throws UnableToObtainMutexException {
        Object mutex = null;
        final int maxNumberOfAttempts = 5;
        int count = 1;

        try {
            while (count <= maxNumberOfAttempts && mutex == null) {
                mutex = mutexQueue.poll(getRandomMillis(), TimeUnit.MILLISECONDS);
                log.warn("Unable to obtain mutex, attempt number: {}", count);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            String errorMessage = "Thread interrupted while waiting for mutex";
            log.error(errorMessage);
            throw new UnableToObtainMutexException(errorMessage);
        }

        if (mutex == null) {
            String errorMessage = "Unable to obtain mutex after " + maxNumberOfAttempts + " attempts";
            log.error(errorMessage);
            throw new UnableToObtainMutexException(errorMessage);
        }

        return mutex;
    }

    long getRandomMillis() {
        return randomGenerator.nextInt(51) + 50; // from 50 to 100 inclusive
    }
}
