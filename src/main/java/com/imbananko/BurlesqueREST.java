package com.imbananko;

import com.google.inject.Inject;
import com.imbananko.config.Path;
import com.imbananko.service.AccountService;
import com.imbananko.service.TransactionHistoryService;
import com.imbananko.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class BurlesqueREST {

    private final static Logger log = LoggerFactory.getLogger(BurlesqueREST.class);
    private final static int PORT = 4567;

    @Inject
    public BurlesqueREST() {
        log.info("Starting main service at port={}", PORT);
        port(PORT);

        before(Path.BASE_WILDCARD_PATH, (request, response) -> log.info("Received api call: " + request.pathInfo()));

        get(Path.ACCOUNTS.ALL,                          AccountService.findAllAccounts);
        get(Path.ACCOUNTS.BY_ID,                        AccountService.getAccountByID);
        post(Path.ACCOUNTS.CREATE,                      AccountService.createAccount);

        get(Path.TRANSACTIONS.ALL,                      TransactionHistoryService.findAll);
        get(Path.TRANSACTIONS.BY_ID,                    TransactionHistoryService.findByID);
        get(Path.TRANSACTIONS.BY_TRADING_ACCOUNT_ID,    TransactionHistoryService.findByTradingAccountID);
        get(Path.TRANSACTIONS.BY_CONTRA_ACCOUNT_ID,     TransactionHistoryService.findByContraAccountID);

        post(Path.TRANSFER.BASE_PATH,                   TransferService.transfer);
    }
}