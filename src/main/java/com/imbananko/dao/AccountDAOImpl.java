package com.imbananko.dao;

import com.imbananko.database.EmbeddedH2Provider;
import com.imbananko.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl extends EmbeddedH2Provider implements AccountDAO {

    private final static Logger log = LoggerFactory.getLogger(AccountDAOImpl.class);

    public AccountDAOImpl() {
        super();
        log.info("Account table was created");
    }

    @Override
    protected PreparedStatement getCreateTableSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("CREATE TABLE IF NOT EXISTS account (id VARCHAR NOT NULL," +
                "amount DECIMAL NOT NULL, " +
                "PRIMARY KEY (id));");
    }

    @Override
    protected PreparedStatement getInsertSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO account (id, amount) VALUES (?, ?)");
    }

    @Override
    protected PreparedStatement getSelectAllSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT id, amount FROM account;");
    }

    @Override
    protected PreparedStatement getSelectByPrimarySQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("SELECT id, amount FROM account WHERE id = (?)");
    }

    private PreparedStatement getUpdateSQLStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE account " +
                "SET amount = ? " +
                "WHERE id = ?;");
    }

    @Override
    public void create(Account account) {
        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getInsertSQLStatement(connection)) {
                statement.setString(1, account.getId());
                statement.setBigDecimal(2, account.getAmount());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error creating new account={}. Exception: {}", account, e);
        }
    }

    @Override
    public void update(Account account) {
        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getUpdateSQLStatement(connection)) {
                statement.setBigDecimal(1, account.getAmount());
                statement.setString(2, account.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error updating account={}. Exception: {}", account, e);
        }
    }

    @Override
    public List<Account> findAll() {
        List<Account> accountsToReturn = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getSelectAllSQLStatement(connection)) {

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    accountsToReturn.add(extractEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Error getting all accounts. Exception=", e);
        }
        return accountsToReturn;
    }

    @Override
    public Account get(String id) {
        Account accountToReturn = null;

        try (Connection connection = DriverManager.getConnection(URL)) {
            try (PreparedStatement statement = getSelectByPrimarySQLStatement(connection)) {
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next() && accountToReturn == null) {
                    accountToReturn = extractEntity(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error("Error resolving account with id={}. Exception: {}", id, e);
        }

        return accountToReturn;
    }

    @Override
    protected Account extractEntity(ResultSet resultSet) throws SQLException {
        return new Account(resultSet.getString("id"), resultSet.getBigDecimal("amount"));
    }
}
