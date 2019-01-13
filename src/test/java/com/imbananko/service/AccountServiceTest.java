package com.imbananko.service;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.common.reflect.TypeToken;
import com.imbananko.config.LightweightModule;
import com.imbananko.config.Path;
import com.imbananko.model.Account;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.inject.Guice.createInjector;
import static com.imbananko.utils.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static spark.Spark.stop;

public class AccountServiceTest {

    @ClassRule
    public static SparkServer testServer = new SparkServer<>(TestApplication.class, 4567);

    @After
    public void tearDown() {
        createInjector(new LightweightModule());
    }

    @AfterClass
    public static void cleanUp() throws InterruptedException {
        stop();
        Thread.sleep(2000);
    }

    private final static String TEST_ACCOUNT_ID = "TEST_ACCOUNT_ID";
    private static final BigDecimal TEST_ACCOUNT_AMOUNT = new BigDecimal(123000.12).setScale(2, BigDecimal.ROUND_HALF_DOWN);

    @Test
    public void shouldStoreAccountWhenCreate() throws HttpClientException {
        Account expectedAccount = createAccount(TEST_ACCOUNT_ID, TEST_ACCOUNT_AMOUNT, testServer);

        Account actualAccount = getAccountByID(expectedAccount.getId(), testServer);
        assertEquals(expectedAccount, actualAccount);

        //We expect UID generating if ID parameter is missing
        String generatedID = createAccount(null, TEST_ACCOUNT_AMOUNT, testServer).getId();
        actualAccount = getAccountByID(generatedID, testServer);
        assertFalse(isNullOrEmpty(actualAccount.getId()));
        assertEquals(TEST_ACCOUNT_AMOUNT, actualAccount.getAmount());

        //We expect both UID generating and 0 amount if ID and amount parameters are missing
        generatedID = createAccount(null, null, testServer).getId();
        actualAccount = getAccountByID(generatedID, testServer);
        assertFalse(isNullOrEmpty(actualAccount.getId()));
        assertEquals(0, actualAccount.getAmount().intValue());
    }

    @Test
    public void shouldReturnAllAccountWhenGetAll() throws HttpClientException {
        sendCreateAccountWithRandomID(testServer);
        sendCreateAccountWithRandomID(testServer);
        sendCreateAccountWithRandomID(testServer);

        HttpResponse responseOfGET = testServer.execute(testServer.get(Path.ACCOUNTS.ALL, false));
        Account[] createdAccounts = extractEntityFromResponse(responseOfGET, TypeToken.of(Account[].class));

        assertEquals(3, createdAccounts.length);
    }
}
