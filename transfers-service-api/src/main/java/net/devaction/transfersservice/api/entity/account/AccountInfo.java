package net.devaction.transfersservice.api.entity.account;

import java.util.List;

import net.devaction.transfersservice.api.entity.ResponseData;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountInfo implements ResponseData {

    private final String accountId;
    private final String currency;
    private final long balance;
    private final List<AccountHistoryItem> history;

    public AccountInfo(String accountId, String currency, long balance,
            List<AccountHistoryItem> history) {

        this.accountId = accountId;
        this.currency = currency;
        this.balance = balance;
        this.history = history;
    }

    @Override
    public String toString(){
        return "AccountInfo [accountId: " + accountId + ", currency: " + currency
                + ", balance: " + balance + ", history: " + history + "]";
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public long getBalance() {
        return balance;
    }

    public List<AccountHistoryItem> getHistory() {
        return history;
    }
}
