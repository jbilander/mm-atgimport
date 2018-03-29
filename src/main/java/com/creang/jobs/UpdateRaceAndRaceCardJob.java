package com.creang.jobs;

import com.creang.Main;
import com.creang.common.MyProperties;
import com.creang.common.Util;
import com.creang.task.UpdateRaceAndRaceCardTask;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class UpdateRaceAndRaceCardJob implements Runnable {

    private final Logger logger = loggerUtil.getLogger();
    private final AtomicBoolean lock = new AtomicBoolean();
    private final UpdateRaceAndRaceCardTask updateRaceAndRaceCardTask = UpdateRaceAndRaceCardTask.getInstance();
    private final boolean todayJob;

    public UpdateRaceAndRaceCardJob(boolean todayJob) {
        this.todayJob = todayJob;
    }

    @Override
    public void run() {

        Main.activeJobsCount.incrementAndGet();

        if (lock.compareAndSet(false, true)) {

            MyProperties properties = MyProperties.getInstance();
            LocalDate date = LocalDate.now(ZoneId.of(properties.getProperties().getProperty(Util.TIME_ZONE_KEY)));

            if (todayJob) {
                updateRaceAndRaceCardTask.run(date, date);
            } else {
                updateRaceAndRaceCardTask.run(date.plusDays(1), date.plusDays(7));
            }
            Main.activeJobsCount.decrementAndGet();
            lock.compareAndSet(true, false);

        } else {
            logger.info("Dry run of UpdateRaceAndRaceCardJob");
            Main.activeJobsCount.decrementAndGet();
        }
    }
}
