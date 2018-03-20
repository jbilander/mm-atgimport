package com.creang.db;

import com.creang.common.MyProperties;
import com.creang.common.Util;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.SQLException;

public enum ConnectionPoolHelper {
    INSTANCE;

    private final MariaDbPoolDataSource dataSource = new MariaDbPoolDataSource();
    private final MiniConnectionPoolManager miniConnectionPoolManager = MiniConnectionPoolManager.getInstance();

    ConnectionPoolHelper() {

        MyProperties properties = MyProperties.getInstance();
        String username = properties.getProperties().getProperty(Util.JDBC_USERNAME_KEY);
        String password = properties.getProperties().getProperty(Util.JDBC_PASSWORD_KEY);

        try {

            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setMaxIdleTime(28800);

            if (Util.isWindows()) {
                dataSource.setUrl(Util.JDBC_URL_WINDOWS);
            } else {
                dataSource.setUrl(Util.JDBC_URL_LINUX);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        miniConnectionPoolManager.setConnectionPoolDataSource(dataSource);
    }

    public static ConnectionPoolHelper getInstance() {
        return INSTANCE;
    }

    public MiniConnectionPoolManager getMiniConnectionPoolManager() {
        return miniConnectionPoolManager;
    }
}
