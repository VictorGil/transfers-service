package net.devaction.transfersservice.core.transfersmanager.concurrency;

import static net.devaction.transfersservice.api.entity.account.AccountType.EXTERNAL;
import static net.devaction.transfersservice.api.entity.account.AccountType.INTERNAL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import net.devaction.transfersservice.api.entity.account.AccountInfo;
import net.devaction.transfersservice.api.entity.transfer.Transfer;

import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.accountsmanager.AccountsManager;
import net.devaction.transfersservice.core.accountsmanager.AccountsManagerImpl;
import net.devaction.transfersservice.core.transfersmanager.TransferChecker;
import net.devaction.transfersservice.core.transfersmanager.TransferCheckerImpl;
import net.devaction.transfersservice.core.transfersmanager.TransfersManager;
import net.devaction.transfersservice.core.transfersmanager.TransfersManagerImpl;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransfersManagerImplConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(TransfersManagerImplConcurrencyTest.class);

    // Using global variables is not a best practice but they are ok
    // here because this is testing code
    public static final String TEST_ACCOUNT = "test-account-";
    public static final String CURRENCY = "NZD";

    private final TransfersManager transfersManager;
    private final AccountsManager accountsManager;

    public TransfersManagerImplConcurrencyTest() {
        Map<String, Account> accountMap = new ConcurrentHashMap<>();
        TransferChecker transferChecker = new TransferCheckerImpl();

        transfersManager = new TransfersManagerImpl(accountMap, transferChecker);
        accountsManager = new AccountsManagerImpl(accountMap, transferChecker);
    }

    @Test
    public void concurrencyTestWithTwoThreads() {

        try {
            // 2 threads * 10 account pairs * 2 rounds = 40 internal transfers (80 account history records).
            // The amount of half of those internal transfers is 100 cents and the amount
            // of the other half is 200 cents.
            performTheTest(2, 10, 2, 10000, 100);
        } catch (Exception ex) {
            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        Set<String> allAccountIds = accountsManager.getAllAccountIds();
        long totalAmount = 0L;
        for (String accountId : allAccountIds) {
            AccountInfo info = null;
            try {
                info = accountsManager.getAccountInfo(accountId);
            } catch (Exception ex) {
                fail(ex.getClass().getSimpleName() + " was thrown");
                return;
            }

            // The initial external transfer plus 8 internal transfer records
            assertThat(info.getHistory()).hasSize(9);

            // The transfers should have cancelled each other out unless there has been
            // a race condition
            assertThat(info.getBalance()).isEqualTo(10000);

            totalAmount += info.getBalance();
        }

        // We assume no transfer failed because the
        // corresponding thread was unable to grab the lock
        assertThat(totalAmount).isEqualTo(10000 * 10);
    }

    @Test
    public void concurrencyTestWithTwentyThreads() {

        try {
            // 20 threads, 50 accounts, 5 rounds
            // 5000 internal transfers to be processed
            performTheTest(20, 50, 5, 1000000, 100);
        } catch (Exception ex) {
            fail(ex.getClass().getSimpleName() + " was thrown");
            return;
        }

        Set<String> allAccountIds = accountsManager.getAllAccountIds();
        long totalAmount = 0L;
        int totalNumOfInternalHistoryItems = 0;
        for (String accountId : allAccountIds) {
            AccountInfo info = null;
            try {
                info = accountsManager.getAccountInfo(accountId);
            } catch (Exception ex) {
                fail(ex.getClass().getSimpleName() + " was thrown");
                return;
            }

            int accountHistoryItems = info.getHistory().size() - 1;
            log.trace("Number of internal history items for the account: {}", accountHistoryItems);
            assertThat(accountHistoryItems).isLessThanOrEqualTo(20 * 5 * 2);

            totalNumOfInternalHistoryItems += accountHistoryItems;

            totalAmount += info.getBalance();
        }

        // Even if some transfers failed, the total amount should match
        assertThat(totalAmount).isEqualTo(1000000 * 50);

        log.trace("Total number of internal transfers: {}", totalNumOfInternalHistoryItems / 2);

        // We pass the test if less than 5% of the non-initial (internal) transfers failed
        assertThat(totalNumOfInternalHistoryItems).isGreaterThan((int) (20 * 50 * 5 * 2 * 0.95));
    }

    void performTheTest(long numOfThreads, int numOfAccounts, long numOfRounds,
            long initialBalance, long baseTransferAmount) throws Exception {

        Map<Integer, String> indexAccountMap = openAndInitializeAccounts(numOfAccounts, initialBalance);

        List<Thread> threads = new LinkedList<>();

        for (long i = 1; i <= numOfThreads; i++) {
            List<AccountIndexPair> accountIndexPairs = constructList(numOfAccounts, numOfRounds);

            TransfersSubmitterRunnable runnable = new TransfersSubmitterRunnable(transfersManager,
                    accountIndexPairs, baseTransferAmount * i, CURRENCY, indexAccountMap);

            Thread thread = new Thread(runnable, "runnable-thread-" + i);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private Map<Integer, String> openAndInitializeAccounts(int numOfAccounts, long initialBalance)
            throws Exception {

        Map<Integer, String> indexAccountMap = new HashMap<>();

        for (int i = 0; i < numOfAccounts; i++) {
            String accountId = accountsManager.openNewAccount(CURRENCY);

            Transfer initialTransfer = new Transfer("external-source-account", EXTERNAL, accountId, INTERNAL,
                    initialBalance, CURRENCY);

            transfersManager.processTransfer(initialTransfer);

            indexAccountMap.put(i, accountId);
        }

        return indexAccountMap;
    }

    private List<AccountIndexPair> constructList(int numOfAccounts, long numOfRounds) {
        List<AccountIndexPair> pairs = new LinkedList<>();

        for (int i = 0; i < numOfRounds; i++) {
            pairs.addAll(innerConstructList(numOfAccounts));
        }

        return pairs;
    }

    private List<AccountIndexPair> innerConstructList(int numOfAccounts) {

        List<Integer> accountIndices1 = new LinkedList<>();
        List<Integer> accountIndices2 = new LinkedList<>();
        for (int i = 0; i < numOfAccounts; i++) {
            accountIndices1.add(i);
            accountIndices2.add(i);
        }

        Collections.shuffle(accountIndices1);
        Collections.shuffle(accountIndices2);

        Iterator<Integer> iter1 = accountIndices1.iterator();
        Iterator<Integer> iter2 = accountIndices2.iterator();

        List<AccountIndexPair> pairs = new LinkedList<>();

        while (iter1.hasNext()) {
            int account1Index = iter1.next();

            if (!iter2.hasNext()) {
                iter2 = accountIndices2.iterator();
            }
            int account2Index = iter2.next();

            if (account1Index == account2Index) {
                if (!iter2.hasNext()) {
                    iter2 = accountIndices2.iterator();
                }
                account2Index = iter2.next();
            }

            pairs.add(new AccountIndexPair(account1Index, account2Index));
            iter1.remove();
            iter2.remove();
        }
        return pairs;
    }
}
