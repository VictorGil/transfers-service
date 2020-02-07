package net.devaction.transfersservice.api.entity.account;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
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
