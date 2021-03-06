package net.devaction.transfersservice.core.account;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.account.AccountHistoryItem;
import net.devaction.transfersservice.api.entity.account.AccountInfo;
import net.devaction.transfersservice.api.entity.account.Direction;
import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.core.transfersmanager.InvalidCurrencyException;

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

    // Since we use manual locking for the write/update operations
    // we just use "volatile" to prevent "dirty reads" when getting
    // just the current account balance.
    private volatile long balance;

    private final List<AccountHistoryItem> history = new LinkedList<>();

    // We use this lock to allow multiple concurrent read operations (getAccountInfo)
    // while preventing dirty reads
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // This is used for concurrency, to avoid race conditions when we need to process
    // two or more requests which have one or both accounts in common.
    private BlockingQueue<AccountMutex> mutexQueue = new LinkedBlockingQueue<>(1);

    private final Random randomGenerator = new Random();

    public Account(String currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }

        this.id = generateRandomId();
        this.currency = currency;

        mutexQueue.add(AccountMutex.ACCOUNT_IS_OPEN);
    }

    public void add(Transfer transfer) throws NotEnoughBalanceException, AmountTooBigException,
            InvalidCurrencyException, UnableToObtainMutexException {

        if (!transfer.getCurrency().equals(currency)) {
            String errorMessage = "The transfer currency does not match this "
                    + "account currency: " + transfer.getCurrency() + " vs "
                    + currency;
            log.error(errorMessage);
            throw new InvalidCurrencyException(errorMessage);
        }

        AccountHistoryItem historyItem = createHistoryItem(transfer);

        updateBalanceAndAddItemToHistory(historyItem);
    }

    /*
     * We need to use a write lock to prevent "dirty reads".
     * See the other method which also uses the same lock below.
     * */
    private void updateBalanceAndAddItemToHistory(AccountHistoryItem historyItem)
            throws NotEnoughBalanceException, AmountTooBigException, UnableToObtainMutexException {

        boolean writeLockAcquired;
        try {
            writeLockAcquired = readWriteLock.writeLock().tryLock(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            String errorMessage = "Failed to process transfer, unable to obtain write lock, "
                    + "the thread got interrupted";
            Thread.currentThread().interrupt();
            log.error(errorMessage, ex);
            throw new UnableToObtainMutexException(errorMessage);
        }

        if (!writeLockAcquired) {
            String errorMessage = "Failed to process transfer, unable to obtain the write lock, "
                    + "time out reached while waiting";
            log.error(errorMessage);
            throw new UnableToObtainMutexException(errorMessage);
        }

        balance = updateBalance(balance, historyItem.getAmount(),
                historyItem.getDirection());

        history.add(historyItem);

        readWriteLock.writeLock().unlock();
    }

    private String generateRandomId() {
        // last 12 hexadecimal digits of the random UUID
        return UUID.randomUUID().toString().substring(24);
    }

    AccountHistoryItem createHistoryItem(Transfer transfer) {
        String counterpartyId = extractCounterpartyId(transfer);
        Direction direction = extractDirection(transfer);

        return new AccountHistoryItem(transfer.getId(),
                counterpartyId, transfer.getAmount(), direction, transfer.getTimestamp());
    }

    long updateBalance(long balance, long amount, Direction direction) throws NotEnoughBalanceException, AmountTooBigException {

        long updatedBalance;
        if (direction == RECEIVED) {
            // This is to prevent "long overflow"
            if (amount > Long.MAX_VALUE - balance) {
                String errorMessage = "The amount is too big for the current balance";
                log.error(errorMessage);
                throw new AmountTooBigException(errorMessage);
            }
            updatedBalance = balance + amount;
        } else {
            updatedBalance = balance - amount;
            if (updatedBalance < 0) {
                String errorMessage = "Not enough balance in account";
                log.error(errorMessage);
                throw new NotEnoughBalanceException(errorMessage);
            }
        }

        return updatedBalance;
    }

    String extractCounterpartyId(Transfer transfer) {
        if (transfer.getSourceAccountId().equals(id)) {
            return transfer.getTargetAccountId();
        }

        if (transfer.getTargetAccountId().equals(id)) {
            return transfer.getSourceAccountId();
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
                + "this account id \"" + id + "\". Transfer:\n" + transfer;
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    /*
     * We need to use a read lock to prevent "dirty reads".
     * See the other method which also uses the same lock above.
     * */
    public AccountInfo getAccountInfo() throws UnableToObtainMutexException {

        boolean readLockAcquired;
        try {
            readLockAcquired = readWriteLock.readLock().tryLock(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            String errorMessage = "Failed to get the account info, unable to obtain the read lock, "
                    + "the thread got interrupted";
            Thread.currentThread().interrupt();
            log.error(errorMessage, ex);
            throw new UnableToObtainMutexException(errorMessage);
        }

        if (!readLockAcquired) {
            String errorMessage = "Failed get the account info, unable to obtain the read lock, "
                    + "time out reached while waiting";
            log.error(errorMessage);
            throw new UnableToObtainMutexException(errorMessage);
        }

        // We use "defensive copying" because we want to decouple the two lists
        List<AccountHistoryItem> historyCopy = Collections.unmodifiableList(new LinkedList<>(history));

        AccountInfo accountInfo = new AccountInfo(id, currency, balance, historyCopy);
        log.trace("AccountInfo:\n{}", accountInfo);
        return accountInfo;
    }

    public AccountMutex getMutex() throws UnableToObtainMutexException {
        AccountMutex mutex = null;
        final int maxNumberOfAttempts = 5;
        int count = 1;

        try {
            while (count <= maxNumberOfAttempts) {
                mutex = mutexQueue.poll(getRandomMillis(), TimeUnit.MILLISECONDS);
                if (mutex == null) {
                    log.warn("Unable to obtain mutex, attempt number: {}", count);
                    count++;
                } else {
                    return mutex;
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            String errorMessage = "Thread interrupted while waiting for mutex";
            log.error(errorMessage);
            throw new UnableToObtainMutexException(errorMessage);
        }

        String errorMessage = "Unable to obtain mutex after " + maxNumberOfAttempts + " attempts";
        log.error(errorMessage);
        throw new UnableToObtainMutexException(errorMessage);
    }

    public void returnMutex(AccountMutex mutex) {
        if (!mutexQueue.offer(mutex)) {
            log.error("Unable to return the mutex to the queue");
        }
    }

    long getRandomMillis() {
        return randomGenerator.nextInt(51) + 50L; // from 50 to 100 inclusive
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Account other = (Account) obj;
        // We know there is no way an id can be null
        if (!id.equals(other.id)) {
            return false;
        }

        // We know there is no way a currency can be null
        if (!currency.equals(other.currency)) {
            log.warn("We have detected two different Account objects with the same id");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Account [id: " + id + ", currency: " + currency + ", balance: "
                + balance + ", number of history items: " + history.size() + "]";
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
}
