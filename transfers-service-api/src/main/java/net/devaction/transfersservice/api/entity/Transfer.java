package net.devaction.transfersservice.api.entity;

import java.beans.ConstructorProperties;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Transfer {

    private final String sourceAccountId;

    private final String targetAccountId;

    private final long amount;

    private final String currency;

    // Milliseconds from UNIX epoch
    private long timestamp;

    @ConstructorProperties({"source_account_id", "target_account_id", "amount", "currency", "timestamp"})
    public Transfer(String sourceAccountId, String targetAccountId, long amount, String currency, long timestamp){
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (amount ^ (amount >>> 32));
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((sourceAccountId == null) ? 0 : sourceAccountId.hashCode());
        result = prime * result + ((targetAccountId == null) ? 0 : targetAccountId.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
        Transfer other = (Transfer) obj;
        if (amount != other.amount) {
            return false;
        }
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            return false;
        }
        if (sourceAccountId == null) {
            if (other.sourceAccountId != null) {
                return false;
            }
        } else if (!sourceAccountId.equals(other.sourceAccountId)) {
            return false;
        }
        if (targetAccountId == null) {
            if (other.targetAccountId != null) {
                return false;
            }
        } else if (!targetAccountId.equals(other.targetAccountId)) {
            return false;
        }

        return timestamp == other.timestamp;
    }

    @Override
    public String toString() {
        return "Transfer [sourceAccountId: " + sourceAccountId + ", targetAccountId: " + targetAccountId
                + ", amount (in cents): " + amount + ", currency: " + currency + ", timestamp: " + timestamp + "]";
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
