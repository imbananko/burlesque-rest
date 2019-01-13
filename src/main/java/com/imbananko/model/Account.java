package com.imbananko.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private String id;
    private BigDecimal amount;

    public Account(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    public Account() {
        id = UUID.randomUUID().toString();
        amount = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(amount, account.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                '}';
    }
}
