package com.creang.jobs;

import com.creang.Main;
import com.creang.task.RacingCardTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class RacingCardJob implements Runnable {

    private final Logger logger = loggerUtil.getLogger();
    private final AtomicBoolean lock = new AtomicBoolean();
    private final RacingCardTask racingCardTask = new RacingCardTask();

    @Override
    public void run() {

        Main.activeJobsCount.incrementAndGet();

        if (lock.compareAndSet(false, true)) {

            racingCardTask.run();
            Main.activeJobsCount.decrementAndGet();
            lock.compareAndSet(true, false);

        } else {
            logger.info("Dry run of RacingCardJob");
            Main.activeJobsCount.decrementAndGet();
        }
    }
}
