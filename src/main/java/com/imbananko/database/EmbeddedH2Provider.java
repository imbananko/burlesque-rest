package com.imbananko.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public abstract class EmbeddedH2Provider<E> {

    //move this to sql file
    protected static final String URL = "jdbc:h2:mem:burlesque;DB_CLOSE_DELAY=-1";
    private final static Logger log = LoggerFactory.getLogger(EmbeddedH2Provider.class);

    protected EmbeddedH2Provider() {
        try (Connection connection = DriverManager.getConnection(URL)) {
            //move this to sql file
            getCreateTableSQLStatement(connection).execute();
        } catch (SQLException e) {
            log.error("Error starting in-memory H2: " + e);
        }
    }

    protected abstract PreparedStatement getCreateTableSQLStatement(Connection connection) throws SQLException;

    protected abstract PreparedStatement getInsertSQLStatement(Connection connection) throws SQLException;

    protected abstract Statement getSelectAllSQLStatement(Connection connection) throws SQLException;

    protected abstract Statement getSelectByPrimarySQLStatement(Connection connection) throws SQLException;

    protected abstract E extractEntity(ResultSet resultSet) throws SQLException;
}
