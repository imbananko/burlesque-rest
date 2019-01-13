package com.imbananko.service;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.imbananko.config.LightweightModule;
import com.imbananko.model.Account;
import com.imbananko.model.Transaction;
import org.junit.*;

import java.math.BigDecimal;

import static com.google.inject.Guice.createInjector;
import static com.imbananko.utils.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static spark.Spark.stop;

public class TransferServiceTest {

    private static final String TEST_ACCOUNT_ID_FROM = "TEST_ACCOUNT_ID_FROM";
    private static final String TEST_ACCOUNT_ID_TO = "TEST_ACCOUNT_ID_TO";

    private static final BigDecimal TEST_AMOUNT = new BigDecimal(123.123).setScale(2, BigDecimal.ROUND_HALF_DOWN);

    private Account accountFrom;
    private Account accountTo;

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

    @Before
    public void setUp() throws Exception {
        accountFrom = createAccount(TEST_ACCOUNT_ID_FROM, TEST_AMOUNT, testServer);
        accountTo = createAccount(TEST_ACCOUNT_ID_TO, new BigDecimal(0), testServer);
    }

    @Test
    public void shouldUpdateBalancesWhenSendingTransfer() throws HttpClientException {
        sendMoneyTransfering(TEST_ACCOUNT_ID_FROM, TEST_ACCOUNT_ID_TO, TEST_AMOUNT, testServer);
        accountFrom = getAccountByID(accountFrom.getId(), testServer);
        accountTo = getAccountByID(accountTo.getId(), testServer);

        assertEquals(0, accountFrom.getAmount().intValue());
        assertEquals(TEST_AMOUNT, accountTo.getAmount());
    }

    @Test
    public void shouldCreateTransactionWhenTransferIsSuccessful() throws HttpClientException {
        Transaction transaction = createTransfer(TEST_ACCOUNT_ID_FROM, TEST_ACCOUNT_ID_TO, TEST_AMOUNT, testServer);

        assertEquals(accountFrom.getId(), transaction.getTradingAccountID());
        assertEquals(accountTo.getId(), transaction.getCounterpartyAccountID());
        assertEquals(TEST_AMOUNT, transaction.getAmount());
    }

    @Test
    public void shouldReturnErrorWhenTryingToLimitNegativeBalance() throws HttpClientException {
        BigDecimal invalidAmountToTransfer = TEST_AMOUNT.multiply(new BigDecimal(5)).setScale(2, BigDecimal.ROUND_HALF_DOWN);

        HttpResponse responseOfPOST = sendMoneyTransfering(TEST_ACCOUNT_ID_FROM,
                TEST_ACCOUNT_ID_TO, invalidAmountToTransfer, testServer);

        assertEquals(400, responseOfPOST.code());
        assertEquals("Requested amount=" + invalidAmountToTransfer + " is bigger than trading account amount=" + accountFrom.getAmount(), new String(responseOfPOST.body()));
    }

    @Test
    public void shouldReturnErrorWhenTryingToTransferWithInvalidAccount() throws HttpClientException {
        String invalidAccountID = "invalid_account_id";

        HttpResponse responseOfPOST = sendMoneyTransfering(invalidAccountID, TEST_ACCOUNT_ID_FROM, TEST_AMOUNT, testServer);
        assertEquals(400, responseOfPOST.code());
        assertEquals("Cannot resolve trading account with id=" + invalidAccountID, new String(responseOfPOST.body()));

        responseOfPOST = sendMoneyTransfering(TEST_ACCOUNT_ID_TO, invalidAccountID, TEST_AMOUNT, testServer);
        assertEquals(400, responseOfPOST.code());
        assertEquals("Cannot resolve counterparty account with id=" + invalidAccountID, new String(responseOfPOST.body()));
    }

}
