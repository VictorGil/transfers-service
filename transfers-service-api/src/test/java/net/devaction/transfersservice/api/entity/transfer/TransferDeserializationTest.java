package net.devaction.transfersservice.api.entity.transfer;

import org.junit.jupiter.api.Test;

import net.devaction.transfersservice.api.entity.account.AccountType;
import net.devaction.transfersservice.api.util.json.FileReader;
import net.devaction.transfersservice.api.util.json.JsonUnmarshaller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransferDeserializationTest {

    @Test
    public void testFromJson() throws Exception {

        long milli = Instant.now().toEpochMilli();

        FileReader fileReader = new FileReader();
        String transferJson = fileReader.readFileFromClasspath("transfer_01.json");

        JsonUnmarshaller<Transfer> unmarshaller = new JsonUnmarshaller<>(Transfer.class);
        Transfer transfer = unmarshaller.unmarshall(transferJson);

        assertThat(transfer.getId()).hasSize(12);
        assertThat(transfer.getAmount()).isEqualTo(100L);
        assertThat(transfer.getCurrency()).isEqualTo("USD");

        assertThat(transfer.getSourceAccountId()).isEqualTo("22bfc5696816");
        assertThat(transfer.getTargetAccountId()).isEqualTo("6fcbdb359fcc");

        assertThat(transfer.getSourceAccountType()).isEqualTo(AccountType.INTERNAL);
        assertThat(transfer.getTargetAccountType()).isEqualTo(AccountType.INTERNAL);

        assertThat(transfer.getTimestamp()).isGreaterThan(milli);
    }
}
