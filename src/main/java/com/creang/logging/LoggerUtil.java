package com.creang.logging;

import com.creang.common.Util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {

    private final Logger logger;

    public LoggerUtil(String loggerName, String logFileName) {

        logger = Logger.getLogger(loggerName);

        try {
            FileHandler fileHandler = new FileHandler(Util.USER_DIR + Util.FILE_SEPARATOR + logFileName, true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleLogFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
