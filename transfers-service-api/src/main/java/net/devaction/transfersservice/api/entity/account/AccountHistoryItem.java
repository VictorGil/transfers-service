package net.devaction.transfersservice.api.entity.account;

import net.devaction.transfersservice.api.util.timestamp.TimestampFormatter;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */

// Note: we do not override "hashCode" method nor "equals" method
// because the AccountHistoryItem objects are stored in a "List"
public class AccountHistoryItem {

    private final String counterpartyAccountId;
    private final String transferId;
    private final long amount;
    private final Direction direction;
    private final long timestamp;

    public AccountHistoryItem(String transferId, String counterpartyAccountId, long amount,
            Direction direction, long timestamp) {

        this.transferId = transferId;
        this.counterpartyAccountId = counterpartyAccountId;
        this.amount = amount;
        this.direction = direction;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AccountHistoryItem [counterpartyAccountId: " + counterpartyAccountId
                + ", transferId: " + transferId + ", amount: " + amount + ", direction: "
                + direction + ", timestamp: " + TimestampFormatter.getTimestampString(timestamp) + "]";
    }

    public String getCounterpartyAccountId() {
        return counterpartyAccountId;
    }

    public String getTransferId() {
        return transferId;
    }

    public long getAmount() {
        return amount;
    }

    public Direction getDirection() {
        return direction;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
