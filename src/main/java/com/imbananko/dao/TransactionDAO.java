package com.imbananko.dao;

import com.imbananko.model.Transaction;

import java.util.List;

public interface TransactionDAO {
    void create(Transaction transaction);
    List<Transaction> findAll();
    Transaction get(String id);
}
