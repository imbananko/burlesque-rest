package com.imbananko.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.imbananko.dao.TransactionDAO;
import com.imbananko.model.Transaction;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionHistoryService {

    @Inject
    private static TransactionDAO transactionDAO;

    public static Route findAll = (Request request, Response response) -> {
        response.type("application/json");
        response.status(200);
        return new Gson().toJson(transactionDAO.findAll());
    };

    public static Route findByID = (Request request, Response response) -> {
        response.type("application/json");
        response.status(200);

        return new Gson().toJson(transactionDAO.get(request.queryParams("id")));
    };

    public static Route findByTradingAccountID = (Request request, Response response) -> {
        response.type("application/json");
        response.status(200);

        List<Transaction> transactions = transactionDAO
                .findAll()
                .stream()
                .filter(t -> t.getTradingAccountID().equals(request.queryParams("id")))
                .collect(Collectors.toList());

        return new Gson().toJson(transactions);
    };

    public static Route findByContraAccountID = (Request request, Response response) -> {
        response.type("application/json");
        response.status(200);

        List<Transaction> transactions = transactionDAO
                .findAll()
                .stream()
                .filter(t -> t.getCounterpartyAccountID().equals(request.queryParams("id")))
                .collect(Collectors.toList());

        return new Gson().toJson(transactions);
    };
}
