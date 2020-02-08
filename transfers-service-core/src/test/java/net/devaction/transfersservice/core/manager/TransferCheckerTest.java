package net.devaction.transfersservice.core.manager;

import org.junit.jupiter.api.Test;

// import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
class TransferCheckerTest {
    private final TransferChecker checker = new TransferChecker();

    @Test
    public void testCheckAmount_01() {
        try {
            checker.checkAmount(0L);
        } catch (InvalidAmountException ex) {
            return;
        }
        fail(InvalidAmountException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckAmount_02() {
        try {
            checker.checkAmount(-1L);
        } catch (InvalidAmountException ex) {
            return;
        }
        fail(InvalidAmountException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckAmount_03() {
        try {
            // This is the biggest amount and it will work if the target account
            // balance is zero, hence it is technically a valid amount
            checker.checkAmount(Long.MAX_VALUE);
        } catch (InvalidAmountException ex) {
            fail(InvalidAmountException.class.getSimpleName() + " was thrown");
        }
    }

    @Test
    public void testCheckAmount_04() {
        try {
            checker.checkAmount(Long.MIN_VALUE);
        } catch (InvalidAmountException ex) {
            return;
        }
        fail(InvalidAmountException.class.getSimpleName() + " was not thrown");
    }

    @Test
    void testCheckTimestamp_01() {
        try {
            checker.checkTimestamp(Long.MIN_VALUE);
        } catch (InvalidTimestampException ex) {
            return;
        }
        fail(InvalidTimestampException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckTimestamp_02() {
        try {
            checker.checkTimestamp(1000L);
        } catch (InvalidTimestampException ex) {
            return;
        }
        fail(InvalidTimestampException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckTimestamp_03() {
        try {
            checker.checkTimestamp(Instant.now().toEpochMilli());
        } catch (InvalidTimestampException ex) {
            fail(InvalidTimestampException.class.getSimpleName() + " was thrown");
        }
    }

    @Test
    void testCheckAccountId_01() {
        try {
            checker.checkAccountId("");
        } catch (InvalidAccountIdException ex) {
            return;
        }
        fail(InvalidAccountIdException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckTransferId_01() {
        try {
            checker.checkTransferId("");
        } catch (InvalidTransferIdException ex) {
            return;
        }
        fail(InvalidTransferIdException.class.getSimpleName() + " was not thrown");
    }

    @Test
    public void testCheckCurreny_01() {
        try {
            checker.checkCurrency("");
        } catch (InvalidCurrencyException ex) {
            return;
        }
        fail(InvalidCurrencyException.class.getSimpleName() + " was not thrown");
    }
}
