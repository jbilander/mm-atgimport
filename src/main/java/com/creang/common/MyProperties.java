package com.creang.common;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public enum MyProperties {
    INSTANCE;

    private final Logger logger = loggerUtil.getLogger();
    private final Properties properties = new Properties();

    MyProperties() {
        try {
            FileInputStream file = new FileInputStream(Util.USER_DIR + Util.FILE_SEPARATOR + Util.CONFIG_PROPERTIES_FILENAME);
            properties.load(file);
            file.close();
        } catch (Exception e) {
            logger.severe("Could not load properties: " + e.getMessage());
            System.exit(1);
        }
    }

    public static MyProperties getInstance() {
        return INSTANCE;
    }

    public Properties getProperties() {
        return properties;
    }
}
