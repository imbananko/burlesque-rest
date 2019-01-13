package com.imbananko.utils;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.imbananko.BurlesqueREST;
import com.imbananko.config.LightweightModule;
import com.imbananko.config.Path;
import com.imbananko.model.Account;
import com.imbananko.model.Transaction;
import spark.servlet.SparkApplication;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import static com.google.inject.Guice.createInjector;

public class TestUtils {

    public static <T> T extractEntityFromResponse(HttpResponse response, TypeToken<T> typeToken) {
        Type token = typeToken.getType();
        return new Gson().fromJson(new String(response.body()), token);
    }

    public static HttpResponse sendCreateAccount(Account account, SparkServer testServer) throws HttpClientException {
        return testServer.execute(testServer.post(Path.ACCOUNTS.CREATE,
                "id=" + account.getId() + "&amount=" + account.getAmount(), false));
    }

    public static HttpResponse sendCreateAccountWithRandomID(SparkServer testServer) throws HttpClientException {
        return testServer.execute(testServer.post(Path.ACCOUNTS.CREATE, "", false));
    }

    public static Account createAccount(String accountID, BigDecimal accountAmount, SparkServer testServer) throws HttpClientException {
        String body = "";

        if (accountID != null) {
            body += "id=" + accountID;
        }

        if (accountAmount != null) {
            body += "&amount=" + accountAmount;
        }

        HttpResponse response = testServer.execute(testServer.post(Path.ACCOUNTS.CREATE, body, false));
        return extractEntityFromResponse(response, TypeToken.of(Account.class));
    }

    public static Account getAccountByID(String id, SparkServer testServer) throws HttpClientException {
        HttpResponse response = testServer.execute(testServer.get(Path.ACCOUNTS.BY_ID.replace(":id", id), false));
        return extractEntityFromResponse(response, TypeToken.of(Account.class));
    }

    public static HttpResponse sendMoneyTransfering(String from, String to, BigDecimal amount, SparkServer testServer) throws HttpClientException {
        return testServer.execute(testServer.post(Path.TRANSFER.BASE_PATH, "from=" + from + "&to=" + to + "&amount=" + amount, false));
    }

    public static Transaction createTransfer(String from, String to, BigDecimal amount, SparkServer testServer) throws HttpClientException {
        return extractEntityFromResponse(sendMoneyTransfering(from, to, amount, testServer), TypeToken.of(Transaction.class));
    }

    public static class TestApplication implements SparkApplication {
        @Override
        public void init() {
            Guice.createInjector(new LightweightModule()).getInstance(BurlesqueREST.class);
        }
    }
}
