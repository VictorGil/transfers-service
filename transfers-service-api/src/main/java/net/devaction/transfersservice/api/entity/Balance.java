package net.devaction.transfersservice.api.entity;

import java.beans.ConstructorProperties;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Balance {

    private final String accountId;
    private final long amount;
    private final long version;

    @ConstructorProperties({"account_id", "amount", "version"})
    public Balance(String accountId, long amount, long version) {
        this.accountId = accountId;
        this.amount = amount;
        this.version = version;
    }

    @Override
    public String toString() {
        return "Balance [accountId=" + accountId + ", amount=" + amount + ", version=" + version + "]";
    }

    public String getAccountId() {
        return accountId;
    }

    public long getAmount() {
        return amount;
    }

    public long getVersion() {
        return version;
    }
}
