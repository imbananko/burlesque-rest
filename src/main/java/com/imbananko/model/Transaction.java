package com.imbananko.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Transaction {
    private String id;
    private String tradingAccountID;
    private String counterpartyAccountID;
    private BigDecimal amount;
    private Date lastUpdateTime;

    public Transaction(String id) {
        this.id = id;
    }

    public Transaction(String tradingAccountID, String counterpartyAccountID, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();;
        this.tradingAccountID = tradingAccountID;
        this.counterpartyAccountID = counterpartyAccountID;
        this.amount = amount;
        this.lastUpdateTime = Date.from(Instant.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradingAccountID() {
        return tradingAccountID;
    }

    public void setTradingAccountID(String tradingAccountID) {
        this.tradingAccountID = tradingAccountID;
    }

    public String getCounterpartyAccountID() {
        return counterpartyAccountID;
    }

    public void setCounterpartyAccountID(String counterpartyAccountID) {
        this.counterpartyAccountID = counterpartyAccountID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", tradingAccountID='" + tradingAccountID + '\'' +
                ", counterpartyAccountID='" + counterpartyAccountID + '\'' +
                ", amount=" + amount +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
