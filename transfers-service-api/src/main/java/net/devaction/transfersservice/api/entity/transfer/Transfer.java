package net.devaction.transfersservice.api.entity.transfer;

import java.beans.ConstructorProperties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Transfer {
    private static final Logger log = LoggerFactory.getLogger(Transfer.class);

    private static final String SAME_ID_ERROR_MESSAGE =
            "Two different entities have same id value:\\n{}\\nvs\\n{}";

    // This is an automatically (internally) generated
    // random id
    private final String id;

    private final String sourceAccountId;

    private final String targetAccountId;

    private final long amount;

    private final String currency;

    // Milliseconds from UNIX epoch
    private long timestamp;

    @ConstructorProperties({"source_account_id", "target_account_id", "amount", "currency", "timestamp"})
    public Transfer(String sourceAccountId, String targetAccountId, long amount, String currency, long timestamp) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.id = generateRandomId();
    }


    private String generateRandomId() {
        // last 12 hexadecimal digits of the random UUID
        return UUID.randomUUID().toString().substring(24);
    }

    @Override
    public String toString() {
        return "Transfer [id: " + id + ", sourceAccountId: " + sourceAccountId + ", targetAccountId: " + targetAccountId
                + ", amount (in cents): " + amount + ", currency: " + currency + ", timestamp: " + timestamp + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // In theory just taking into account the id should be enough
        // but we use all the fields in the hashcode computation to be
        // on the safe side
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (other.getId() == null) {
            // This should never happen
            log.error("Transfer entity has null id:\n{}", other);
            return false;
        }

        if (!id.equals(other.getId())) {
            return false;
        }

        // Since both id values are equal, the rest of the fields should be equal too
        // but we check them just in case we get extremely unlucky
        if (amount != other.amount) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (sourceAccountId == null) {
            if (other.sourceAccountId != null) {
                return false;
            }
        } else if (!sourceAccountId.equals(other.sourceAccountId)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (targetAccountId == null) {
            if (other.targetAccountId != null) {
                return false;
            }
        } else if (!targetAccountId.equals(other.targetAccountId)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (timestamp != other.timestamp) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        return true;
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

    public String getId() {
        return id;
    }
}
