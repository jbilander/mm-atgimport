package com.creang.jobs;

import com.creang.Main;
import com.creang.task.PurgeTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class PurgeJob implements Runnable {

    private final Logger logger = loggerUtil.getLogger();
    private final AtomicBoolean lock = new AtomicBoolean();
    private final PurgeTask purgeTask = new PurgeTask();

    @Override
    public void run() {

        Main.activeJobsCount.incrementAndGet();

        if (lock.compareAndSet(false, true)) {

            purgeTask.run();
            Main.activeJobsCount.decrementAndGet();
            lock.compareAndSet(true, false);

        } else {
            logger.info("Dry run of PurgeJob");
            Main.activeJobsCount.decrementAndGet();
        }
    }
}
