package net.devaction.transfersservice.api.entity.balance;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.devaction.transfersservice.api.entity.balance.Balance;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class BalanceSerializationTest {

    @Test
    public void testFromJson01() throws JsonProcessingException {
        String accountId = "test_account_id_02";
        long amount = 500;

        Balance balance = new Balance(accountId, amount);

        String balanceJson = new ObjectMapper().writeValueAsString(balance);
        assertThat(balanceJson).contains(accountId);
        assertThat(balanceJson).contains(String.valueOf(amount));
    }
}
