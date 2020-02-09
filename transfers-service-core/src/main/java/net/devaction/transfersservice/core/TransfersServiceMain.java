package net.devaction.transfersservice.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.devaction.transfersservice.api.entity.balance.Balance;
import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.api.util.json.JsonUnmarshaller;
import net.devaction.transfersservice.core.accountsmanager.AccountManager;
import net.devaction.transfersservice.core.manager.transfer.TransferManager;
import net.devaction.transfersservice.core.manager.transfer.TransferManagerImpl;
import net.devaction.transfersservice.core.response.Response;

import static net.devaction.transfersservice.core.response.Status.SUCCESS;
import static net.devaction.transfersservice.core.response.Status.ERROR;

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

    private final AccountManager accountManager = new AccountManager(null, null);
    private final TransferManager transferManager = new TransferManagerImpl();

    private final JsonUnmarshaller<Transfer> transferUnmarshaller = new JsonUnmarshaller<>(Transfer.class);
    private final ObjectWriter responseWriter = new ObjectMapper().writerFor(Response.class);

    public static void main(String[] args) {
        new TransfersServiceMain().run();
    }

    private void run() {
        log.info("Starting the Transfers service");

        Spark.post(TRANSFERS + "/transfer", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);
            String requestBody = sparkRequest.body();
            log.trace("Request to process a new \"Transfer\" has been received:\n{}", requestBody);

            Transfer transfer = null;
            Response response = null;
            try {
                transfer = transferUnmarshaller.unmarshall(requestBody);
                transferManager.processTransfer(transfer);
            } catch (Exception ex) {
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            return responseWriter.writeValueAsString(new Response(SUCCESS));
        });

        Spark.post(TRANSFERS + "/account", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = null;
            String currency = sparkRequest.queryParams("currency");
            try {
                accountId = accountManager.openNewAccount(currency);
            } catch (Exception ex) {
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.get(TRANSFERS + "/balance", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams("accountId");
            try {
                // TODO
                // balance = accountManager.getBalance(accountID);
            } catch (Exception ex) {
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId, 0L);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.get(TRANSFERS + "/history", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams("accountId");
            try {
                // TODO
                // accountManager.getHistory(accountID);
            } catch (Exception ex) {
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId, 0L);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.delete(TRANSFERS + "/account", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams("accountId");
            try {
                // TODO
                accountManager.closeAccount(accountId);
            } catch (Exception ex) {
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            return responseWriter.writeValueAsString(new Response(SUCCESS));
        });
    }
}
