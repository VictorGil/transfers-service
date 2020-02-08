package net.devaction.transfersservice.api.entity.balance;

import java.beans.ConstructorProperties;

import net.devaction.transfersservice.api.entity.ResponseData;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Balance implements ResponseData {

    private final String accountId;
    private final long amount;

    @ConstructorProperties({"account_id", "amount"})
    public Balance(String accountId, long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Balance [accountId=" + accountId + ", amount=" + amount + "]";
    }

    public String getAccountId() {
        return accountId;
    }

    public long getAmount() {
        return amount;
    }
}
