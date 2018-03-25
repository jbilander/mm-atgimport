package com.creang;

import com.creang.common.Util;
import com.creang.logging.LoggerUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public final static AtomicInteger activeJobsCount = new AtomicInteger();
    public final static LoggerUtil loggerUtil = new LoggerUtil(Util.LOGGER_NAME, Util.LOG_FILE_NAME);

    public static void main(String[] args) {

        JobScheduler jobScheduler = new JobScheduler();
        jobScheduler.run();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {

                jobScheduler.getCronThreadPoolExecutor().shutdown();

                while (activeJobsCount.get() > 0) {
                    Thread.sleep(3000);
                }

            } catch (Exception e) {
                loggerUtil.getLogger().severe(e.getMessage());
            }
        }));
    }
}
