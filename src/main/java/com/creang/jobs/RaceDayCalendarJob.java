package com.creang.jobs;

import com.creang.Main;
import com.creang.task.RaceDayCalendarTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class RaceDayCalendarJob implements Runnable {

    private final Logger logger = loggerUtil.getLogger();
    private final AtomicBoolean lock = new AtomicBoolean();
    private final RaceDayCalendarTask raceDayCalendarTask = new RaceDayCalendarTask();

    @Override
    public void run() {

        Main.activeJobsCount.incrementAndGet();

        if (lock.compareAndSet(false, true)) {

            raceDayCalendarTask.run();
            Main.activeJobsCount.decrementAndGet();
            lock.compareAndSet(true, false);

        } else {
            logger.info("Dry run of RaceDayCalendarJob");
            Main.activeJobsCount.decrementAndGet();
        }
    }
}
