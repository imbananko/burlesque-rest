package com.imbananko.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.imbananko.dao.AccountDAO;
import com.imbananko.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class AccountService {

    private final static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Inject
    private static AccountDAO accountDAO;

    public static Route createAccount = (Request request, Response response) -> {
        String id = request.queryParams("id");
        String amount = request.queryParams("amount");

        Account account = new Account();

        if (!isNullOrEmpty(id)) {
            account.setId(id);
        }

        if (!isNullOrEmpty(amount)) {
            account.setAmount(new BigDecimal(amount));
        }

        log.debug("Creating account: " + account.toString());
        accountDAO.create(account);

        response.type("application/json");
        response.status(201);
        return new Gson().toJson(account);
    };

    public static Route findAllAccounts = (Request request, Response response) -> {
        List<Account> accounts = accountDAO.findAll();

        log.debug("Returning all accounts. Size: " + accounts.size());

        response.type("application/json");
        response.status(200);
        return new Gson().toJson(accounts);
    };

    public static Route getAccountByID = (Request request, Response response) -> {
        String id = request.params(":id");

        response.type("application/json");
        response.status(200 );
        return new Gson().toJson(accountDAO.get(id));
    };
}
