package com.imbananko.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.imbananko.dao.AccountDAO;
import com.imbananko.dao.TransactionDAO;
import com.imbananko.model.Account;
import com.imbananko.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransferService {

    //Separate business logic from database logic: another controller class for routing and another service class for business logic

    private final static Logger log = LoggerFactory.getLogger(TransferService.class);
    private static Lock lock = new ReentrantLock();

    @Inject
    private static AccountDAO accountDAO;

    @Inject
    private static TransactionDAO transactionDAO;

    public static Route transfer = (Request request, Response response) -> {

        String tradingAccountID = request.queryParams("from");
        String contraTradingAccountID = request.queryParams("to");
        BigDecimal amount = new BigDecimal(request.queryParams("amount")).setScale(2, BigDecimal.ROUND_HALF_DOWN);

        response.type("application/json");
        response.status(400);

        Account tradingAccount = accountDAO.get(tradingAccountID);
        log.debug("Resolved trading account: " + tradingAccount);
        if (tradingAccount == null) {
            return "Cannot resolve trading account with id=" + tradingAccountID;
        }

        Account contraTradingAccount = accountDAO.get(contraTradingAccountID);
        log.debug("Resolved trading account: " + contraTradingAccount);
        if (contraTradingAccount == null) {
            return "Cannot resolve counterparty account with id=" + contraTradingAccountID;
        }

        if (tradingAccount.getAmount().compareTo(amount) < 0) {
            return "Requested amount=" + amount + " is bigger than trading account amount=" + tradingAccount.getAmount();
        }

        Transaction transaction;

        lock.lock();

        //lock first then second id
        //concurrent hashmap: key - id, value - amount

        try {
            Account accountToWithdraw = accountDAO.get(tradingAccountID);
            log.debug("Resolved trading account: " + accountToWithdraw);

            Account accountToDeposit = accountDAO.get(contraTradingAccountID);
            log.debug("Resolved counterparty account: " + accountToDeposit);

            accountToWithdraw.updateAmount(amount.negate());
            accountDAO.update(accountToWithdraw);

            accountToDeposit.updateAmount(amount);
            accountDAO.update(accountToDeposit);

            transaction = new Transaction(tradingAccountID, contraTradingAccountID, amount);

            log.info("Creating transaction: " + transaction);
            transactionDAO.create(transaction);

            response.type("application/json");
            response.status(201);
            return new Gson().toJson(transaction);
        } finally {
            lock.unlock();
        }
    };
}
