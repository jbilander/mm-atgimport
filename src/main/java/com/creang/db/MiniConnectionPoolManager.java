package com.creang.db;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public enum MiniConnectionPoolManager {
    INSTANCE;

    private final Logger logger = loggerUtil.getLogger();
    private final String COULD_NOT_RECYCLE_CONNECTION = "Could not recycle connection, ";
    private MariaDbPoolDataSource connectionPoolDataSource;
    private final ConcurrentLinkedQueue<Connection> recycledConnections = new ConcurrentLinkedQueue<>(); //Thread-safe FIFO-Queue.

    public void setConnectionPoolDataSource(MariaDbPoolDataSource connectionPoolDataSource) {
        this.connectionPoolDataSource = connectionPoolDataSource;
    }

    public Connection getValidConnection(int validCheckTimeoutInSeconds) throws SQLException {

        if (connectionPoolDataSource == null) {
            throw new SQLException("No ConnectionPoolDataSource is set! Make sure to set the ConnectionPoolDataSource before calling getValidConnection");
        }

        Connection conn = recycledConnections.poll();

        while (conn != null) {
            try {
                conn.isValid(validCheckTimeoutInSeconds);
                return conn;
            } catch (SQLException e) {
                logger.info("Connection not valid, removing from pool!");
                conn = recycledConnections.poll();
            }
        }

        try {
            conn = connectionPoolDataSource.getConnection();
            conn.isValid(validCheckTimeoutInSeconds);
            return conn;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            throw e;
        }
    }

    //Only call this method once for each connection.
    public void release(Connection conn) {

        if (conn == null) {
            logger.info(COULD_NOT_RECYCLE_CONNECTION + "connection is null.");
            return;
        }

        try {
            if (!conn.isClosed()) {
                recycledConnections.offer(conn);
            } else {
                logger.info(COULD_NOT_RECYCLE_CONNECTION + "connection is closed.");
            }
        } catch (SQLException e) {
            logger.info(COULD_NOT_RECYCLE_CONNECTION + e.getMessage());
        }
    }

    //closes all connections and clears the pool
    public void disposePool() {

        for (Connection conn = recycledConnections.poll(); conn != null; conn = recycledConnections.poll()) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }
    }

    public static MiniConnectionPoolManager getInstance() {
        return INSTANCE;
    }
}
