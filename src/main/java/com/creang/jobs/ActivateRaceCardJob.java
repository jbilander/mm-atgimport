package com.creang.jobs;

import com.creang.Main;
import com.creang.task.ActivateRaceCardTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class ActivateRaceCardJob implements Runnable {

    private final Logger logger = loggerUtil.getLogger();
    private final AtomicBoolean lock = new AtomicBoolean();
    private final ActivateRaceCardTask activateRaceCardTask = new ActivateRaceCardTask();

    @Override
    public void run() {

        Main.activeJobsCount.incrementAndGet();

        if (lock.compareAndSet(false, true)) {

            activateRaceCardTask.run();
            Main.activeJobsCount.decrementAndGet();
            lock.compareAndSet(true, false);

        } else {
            logger.info("Dry run of ActivateRaceCardJob");
            Main.activeJobsCount.decrementAndGet();
        }
    }
}
