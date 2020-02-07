package net.devaction.transfersservice.api.entity.balance;

import org.junit.jupiter.api.Test;

import net.devaction.transfersservice.api.entity.balance.Balance;
import net.devaction.transfersservice.api.util.json.FileReader;
import net.devaction.transfersservice.api.util.json.JsonUnmarshaller;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class BalanceDeserializationTest {

    @Test
    public void testFromJson01() throws Exception {
        FileReader fileReader = new FileReader();
        String balanceJson = fileReader.readFileFromClasspath("balance_01.json");

        JsonUnmarshaller<Balance> unmarshaller = new JsonUnmarshaller<>(Balance.class);
        Balance balance = unmarshaller.unmarshall(balanceJson);

        assertThat(balance.getAccountId()).isEqualTo("test_account_01");
        assertThat(balance.getAmount()).isEqualTo(300);
    }
}
