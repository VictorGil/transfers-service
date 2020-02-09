package net.devaction.transfersservice.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.devaction.transfersservice.api.entity.balance.Balance;
import net.devaction.transfersservice.api.entity.transfer.Transfer;
import net.devaction.transfersservice.api.util.json.JsonUnmarshaller;
import net.devaction.transfersservice.core.accountsmanager.AccountsManager;
import net.devaction.transfersservice.core.response.Response;
import net.devaction.transfersservice.core.transfersmanager.TransfersManager;

import static net.devaction.transfersservice.core.response.Status.SUCCESS;
import static net.devaction.transfersservice.core.response.Status.ERROR;

import spark.Spark;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TransfersServiceMain {

    private static final Logger log = LoggerFactory.getLogger(TransfersServiceMain.class);

    private static final String TRANSFERS = "/transfers";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCOUNT_ID = "accountId";

    private final AccountsManager accountsManager;
    private final TransfersManager transfersManager;

    private final JsonUnmarshaller<Transfer> transferUnmarshaller = new JsonUnmarshaller<>(Transfer.class);
    private final ObjectWriter responseWriter = new ObjectMapper().writerFor(Response.class);

    @Inject
    public TransfersServiceMain(AccountsManager accountsManager, TransfersManager transfersManager) {
        this.accountsManager = accountsManager;
        this.transfersManager = transfersManager;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        TransfersServiceMain main = injector.getInstance(TransfersServiceMain.class);
        main.run();
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
                transfersManager.processTransfer(transfer);
            } catch (Exception ex) {
                // In case it is a runtime exception which has not been logged yet
                log.error("{}", ex, ex);
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
                accountId = accountsManager.openNewAccount(currency);
            } catch (Exception ex) {
                // In case it is a runtime exception which has not been logged yet
                log.error("{}", ex, ex);
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.get(TRANSFERS + "/balance", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams(ACCOUNT_ID);
            long balance = -1L;
            try {
                balance = accountsManager.getBalance(accountId);
            } catch (Exception ex) {
                // In case it is a runtime exception which has not been logged yet
                log.error("{}", ex, ex);
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId, balance);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.get(TRANSFERS + "/history", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams(ACCOUNT_ID);
            try {
                // TODO
                // accountManager.getHistory(accountID);
            } catch (Exception ex) {
                // In case it is a runtime exception which has not been logged yet
                log.error("{}", ex, ex);
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            Balance accountBalance = new Balance(accountId, 0L);
            return responseWriter.writeValueAsString(new Response(SUCCESS, accountBalance));
        });

        Spark.delete(TRANSFERS + "/account", (sparkRequest, sparkResponse) -> {
            sparkResponse.type(APPLICATION_JSON);

            Response response = null;
            String accountId = sparkRequest.queryParams(ACCOUNT_ID);
            try {
                accountsManager.closeAccount(accountId);
            } catch (Exception ex) {
                // In case it is a runtime exception which has not been logged yet
                log.error("{}", ex, ex);
                response = new Response(ERROR, ex.toString());
                return responseWriter.writeValueAsString(response);
            }

            return responseWriter.writeValueAsString(new Response(SUCCESS));
        });
    }
}
