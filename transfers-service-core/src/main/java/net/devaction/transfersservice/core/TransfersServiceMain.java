package net.devaction.transfersservice.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.api.util.json.JsonUnmarshaller;
import net.devaction.transfersservice.core.manager.AccountManager;
import net.devaction.transfersservice.core.response.Response;
import net.devaction.transfersservice.core.response.Status;

import spark.Spark;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransfersServiceMain {
    private static final Logger log = LoggerFactory.getLogger(TransfersServiceMain.class);

    private static final String TRANSFERS = "/transfers";
    private static final String APPLICATION_JSON = "application/json";

    private final AccountManager accountManager = new AccountManager();
    private final JsonUnmarshaller<Transfer> transferUnmarshaller = new JsonUnmarshaller<>(Transfer.class);
    private final ObjectWriter responseWriter = new ObjectMapper().writerFor(Response.class);

    public static void main(String[] args) {
        new TransfersServiceMain().run();
    }

    private void run() {
        log.info("Starting the Transfers service");

        Spark.post(TRANSFERS + "/new", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);
            String requestBody = sparkRequest.body();

            Transfer transfer = null;
            Response response = null;
            try {
                transfer = transferUnmarshaller.unmarshall(requestBody);
                accountManager.processTransfer(transfer);
            } catch (Exception ex) {
                response = new Response(Status.ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            return responseWriter.writeValueAsString(new Response());
        });
    }
}
