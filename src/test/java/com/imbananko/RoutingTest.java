package com.imbananko;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.common.reflect.TypeToken;
import com.imbananko.config.LightweightModule;
import com.imbananko.config.Path;
import com.imbananko.model.Account;
import org.junit.*;

import java.math.BigDecimal;

import static com.google.inject.Guice.createInjector;
import static com.imbananko.utils.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static spark.Spark.stop;

public class RoutingTest {

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

    private static final String TEST_ACCOUNT_ID = "TEST_ACCOUNT_ID";
    private static final String TEST_TRANSACTION_ID = "TEST_TRANSACTION_ID ";
    private static final BigDecimal TEST_ACCOUNT_AMOUNT = new BigDecimal(123000.123);

    @Test
    public void shouldReturnSuccessWhenCreatingAccount() throws HttpClientException {
        HttpResponse responseOfPOST = sendCreateAccountWithRandomID(testServer);
        assertEquals(201, responseOfPOST.code());
        assertEquals("Created", responseOfPOST.message());

        Account createdAccount = extractEntityFromResponse(responseOfPOST, TypeToken.of(Account.class));
        Assert.assertNotNull(createdAccount.getId());
        assertEquals(0, createdAccount.getAmount().intValue());
    }

    @Test
    public void shouldReturnSuccessWhenCreatingAccountWithParams() throws HttpClientException {
        Account expectedAccount = new Account(TEST_ACCOUNT_ID, TEST_ACCOUNT_AMOUNT);

        HttpResponse responseOfPOST = sendCreateAccount(expectedAccount, testServer);
        assertEquals(201, responseOfPOST.code());
        assertEquals("Created", responseOfPOST.message());

        Account actualAccount = extractEntityFromResponse(responseOfPOST, TypeToken.of(Account.class));
        assertEquals("Accounts are different", expectedAccount, actualAccount);
    }

    @Test
    public void shouldReturnSuccessWhenGettingAllAccounts() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(testServer.get(Path.ACCOUNTS.ALL, false));
        assertEquals(200, responseOfGET.code());
    }

    @Test
    public void shouldReturnSuccessWhenGettingAccountByID() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(
                testServer.get(Path.ACCOUNTS.BY_ID.replace(":id", TEST_ACCOUNT_ID), false));
        assertEquals(200, responseOfGET.code());
    }

    @Test
    public void shouldReturnSuccessWhenCreatingTransaction() throws HttpClientException {
        createAccount("to", new BigDecimal(1), testServer);
        createAccount("from", new BigDecimal(1), testServer);

        HttpResponse responseOfPOST = testServer.execute(testServer.post(Path.TRANSFER.BASE_PATH,
                "from=from&to=to&amount=1", false));
        assertEquals(201, responseOfPOST.code());
        assertEquals("Created", responseOfPOST.message());
    }

    @Test
    public void shouldReturnSuccessWhenGettingAllTransactions() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(testServer.get(Path.TRANSACTIONS.ALL, false));
        assertEquals(200, responseOfGET.code());
    }

    @Test
    public void shouldReturnSuccessWhenGettingTransactionByID() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(
                testServer.get(Path.TRANSACTIONS.BY_ID.replace(":id", TEST_TRANSACTION_ID), false));
        assertEquals(200, responseOfGET.code());
    }

    @Test
    public void shouldReturnSuccessWhenGettingTransactionByTradingAccountID() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(
                testServer.get(Path.TRANSACTIONS.BY_TRADING_ACCOUNT_ID.replace(":id", TEST_ACCOUNT_ID), false));
        assertEquals(200, responseOfGET.code());
    }

    @Test
    public void shouldReturnSuccessWhenGettingTransactionByContraAccountID() throws HttpClientException {
        HttpResponse responseOfGET = testServer.execute(
                testServer.get(Path.TRANSACTIONS.BY_CONTRA_ACCOUNT_ID.replace(":id", TEST_ACCOUNT_ID), false));
        assertEquals(200, responseOfGET.code());
    }
}
