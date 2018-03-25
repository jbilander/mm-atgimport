package com.creang.db;

import com.creang.common.MyProperties;
import com.creang.common.Util;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.SQLException;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public enum ConnectionPoolHelper {
    INSTANCE;

    private final Logger logger = loggerUtil.getLogger();
    private final MariaDbPoolDataSource dataSource = new MariaDbPoolDataSource();

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
            logger.severe(e.getMessage());
        }
    }

    public static ConnectionPoolHelper getInstance() {
        return INSTANCE;
    }

    public MariaDbPoolDataSource getDataSource() {
        return dataSource;
    }
}
