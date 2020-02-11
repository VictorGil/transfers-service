package net.devaction.transfersservice.core.transfersmanager.concurrency;

import java.util.Objects;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class AccountIndexPair {

    private final int sourceAccountIndex;
    private final int targetAccountIndex;

    public AccountIndexPair(int sourceAccountIndex, int targetAccountIndex) {
        this.sourceAccountIndex = sourceAccountIndex;
        this.targetAccountIndex = targetAccountIndex;
    }

    @Override
    public String toString() {
        return "AccountPair [sourceAccountIndex:" + sourceAccountIndex
                + ", targetAccountIndex:" + targetAccountIndex + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceAccountIndex, targetAccountIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AccountIndexPair)) {
            return false;
        }
        AccountIndexPair other = (AccountIndexPair) obj;
        return sourceAccountIndex == other.sourceAccountIndex && targetAccountIndex == other.targetAccountIndex;
    }

    public int getSourceAccountIndex() {
        return sourceAccountIndex;
    }

    public int getTargetAccountIndex() {
        return targetAccountIndex;
    }
}
