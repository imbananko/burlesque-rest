package com.imbananko.dao;

import com.imbananko.database.EmbeddedH2Provider;
import com.imbananko.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl extends EmbeddedH2Provider implements TransactionDAO {

    private final static Logger log = LoggerFactory.getLogger(TransactionDAOImpl.class);

    public TransactionDAOImpl() {
        super();
        log.info("Transaction table was created");
    }

    @Override
    protected PreparedStatement getCreateTableSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("CREATE TABLE IF NOT EXISTS transaction(" +
                "id VARCHAR NOT NULL, " +
                "amount DECIMAL NOT NULL, " +
                "trading_account VARCHAR NOT NULL, " +
                "counterparty_account VARCHAR NOT NULL, " +
                "last_update_time TIMESTAMP NOT NULL, " +
                "PRIMARY KEY (id));");
    }

    @Override
    protected PreparedStatement getInsertSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO transaction (id, trading_account, counterparty_account, amount, last_update_time)" +
                "VALUES (?, ?, ?, ?, ?)");
    }

    @Override
    protected PreparedStatement getSelectAllSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT id, trading_account, counterparty_account, amount, last_update_time FROM transaction;");
    }

    @Override
    protected PreparedStatement getSelectByPrimarySQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT id, trading_account, counterparty_account, amount, last_update_time " +
                "WHERE id = ?;");
    }

    @Override
    public void create(Transaction transaction) {
        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getInsertSQLStatement(connection)) {
                statement.setString(1, transaction.getId());
                statement.setString(2, transaction.getTradingAccountID());
                statement.setString(3, transaction.getCounterpartyAccountID());
                statement.setBigDecimal(4, transaction.getAmount());
                statement.setTimestamp(5, new Timestamp(transaction.getLastUpdateTime().getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error creating new transaction={}. Exception: {}", transaction, e);
        }

    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getSelectAllSQLStatement(connection)) {

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    transactions.add(extractEntity(resultSet));

                }
            }
        } catch (SQLException e) {
            log.error("Error getting all transactions. Exception: {}", e);
        }

        return transactions;
    }

    @Override
    public Transaction get(String id) {
        Transaction transaction = null;

        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getSelectAllSQLStatement(connection)) {

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next() && transaction == null) {
                    transaction = extractEntity(resultSet);

                }
            }
        } catch (SQLException e) {
            log.error("Error resolving transaction with id={}. Exception: {}", id, e);
        }

        return transaction;
    }

    @Override
    protected Transaction extractEntity(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction(resultSet.getString("id"));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setCounterpartyAccountID(resultSet.getString("counterparty_account"));
        transaction.setTradingAccountID(resultSet.getString("trading_account"));
        transaction.setLastUpdateTime(resultSet.getTimestamp("last_update_time"));
        return transaction;
    }
}
