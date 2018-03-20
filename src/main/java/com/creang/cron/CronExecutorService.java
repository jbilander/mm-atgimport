package com.creang.cron;

import java.util.concurrent.ExecutorService;

public interface CronExecutorService extends ExecutorService {
    void schedule(Runnable task, CronExpression expression);
}
