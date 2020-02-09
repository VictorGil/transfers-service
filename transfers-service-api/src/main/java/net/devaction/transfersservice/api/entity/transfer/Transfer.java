package net.devaction.transfersservice.api.entity.transfer;

import java.beans.ConstructorProperties;

import java.time.Instant;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.transfersservice.api.entity.account.AccountType;
import net.devaction.transfersservice.api.util.timestamp.TimestampFormatter;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Transfer {
    private static final Logger log = LoggerFactory.getLogger(Transfer.class);

    private static final String SAME_ID_ERROR_MESSAGE =
            "Two different entities have same id value:\n{}\nvs\n{}";

    // This is an automatically (internally) generated
    // random id
    private final String id;

    private final String sourceAccountId;
    private final AccountType sourceAccountType;

    private final String targetAccountId;
    private final AccountType targetAccountType;

    private final long amount;

    private final String currency;

    // Milliseconds from UNIX epoch
    private long timestamp;

    @ConstructorProperties({"source_account_id", "source_account_type",
            "target_account_id", "target_account_type", "amount", "currency"})
    public Transfer(String sourceAccountId, AccountType sourceAccountType,
            String targetAccountId, AccountType targetAccountType, long amount, String currency) {

        checkConstructorArguments(sourceAccountId, sourceAccountType, targetAccountId,
                targetAccountType, amount, currency);

        this.sourceAccountId = sourceAccountId;
        this.sourceAccountType = sourceAccountType;

        this.targetAccountId = targetAccountId;
        this.targetAccountType = targetAccountType;

        this.amount = amount;
        this.currency = currency;

        timestamp = Instant.now().toEpochMilli();
        id = generateRandomId();
    }

    private String generateRandomId() {
        // last 12 hexadecimal digits of the random UUID
        return UUID.randomUUID().toString().substring(24);
    }

    @Override
    public String toString() {
        return "Transfer [id: " + id + ", sourceAccountId: " + sourceAccountId + ", sourceAccountType: "
                + sourceAccountType + ", targetAccountId: " + targetAccountId + ", targetAccountType: "
                + targetAccountType + ", amount (in cents): " + amount + ", currency: " + currency
                + ", timestamp: " + TimestampFormatter.getTimestampString(timestamp) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // In theory just taking into account the id should be enough
        // but we use all the fields in the hashcode computation to be
        // on the safe side
        result = prime * result + id.hashCode();
        result = prime * result + (int) (amount ^ (amount >>> 32));
        result = prime * result + currency.hashCode();

        result = prime * result + sourceAccountId.hashCode();
        result = prime * result + sourceAccountType.hashCode();

        result = prime * result + targetAccountId.hashCode();
        result = prime * result + targetAccountType.hashCode();

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

        if (!currency.equals(other.currency)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (!sourceAccountId.equals(other.sourceAccountId)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (sourceAccountType != other.getSourceAccountType()) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (!targetAccountId.equals(other.targetAccountId)) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (targetAccountType != other.getTargetAccountType()) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        if (timestamp != other.timestamp) {
            log.warn(SAME_ID_ERROR_MESSAGE, other, this);
            return false;
        }

        return true;
    }

    void checkConstructorArguments(String sourceAccountId, AccountType sourceAccountType,
            String targetAccountId, AccountType targetAccountType, long amount, String currency) {

        if (sourceAccountId == null || sourceAccountType == null || targetAccountId == null
                || targetAccountType == null || currency == null || amount < 1) {

            String errorMessage = "One or more of the arguments provided are invalid";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
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

    public AccountType getSourceAccountType() {
        return sourceAccountType;
    }

    public AccountType getTargetAccountType() {
        return targetAccountType;
    }
}
