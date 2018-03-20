package com.creang;

import com.creang.common.MyProperties;
import com.creang.common.Util;
import com.creang.cron.CronExpression;
import com.creang.cron.CronThreadPoolExecutor;
import com.creang.jobs.*;

import java.util.TimeZone;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class JobScheduler {

    private final CronThreadPoolExecutor cronThreadPoolExecutor = new CronThreadPoolExecutor(4);
    private final RaceDayCalendarJob raceDayCalendarJob = new RaceDayCalendarJob();
    private final RacingCardJob racingCardJob = new RacingCardJob();
    private final ActivateRaceCardJob activateRaceCardsJob = new ActivateRaceCardJob();
    private final UpdateRaceAndRaceCardJob updateRaceAndRaceCardTodayJob = new UpdateRaceAndRaceCardJob(true);
    private final UpdateRaceAndRaceCardJob updateRaceAndRaceCardOtherJob = new UpdateRaceAndRaceCardJob(false);
    private final PurgeJob purgeJob = new PurgeJob();
    private final MyProperties properties = MyProperties.getInstance();
    private final Logger logger = loggerUtil.getLogger();

    public void run() {

        logger.info("Running JobScheduler");

        String tz = properties.getProperties().getProperty(Util.TIME_ZONE_KEY);
        TimeZone timeZone = TimeZone.getTimeZone(tz);

        try {

            CronExpression raceDayCalendarExp = new CronExpression(properties.getProperties().getProperty(Util.RACE_DAY_CALENDAR_EXP_KEY));
            raceDayCalendarExp.setTimeZone(timeZone);

            CronExpression racingCardExp = new CronExpression(properties.getProperties().getProperty(Util.RACING_CARD_EXP_KEY));
            racingCardExp.setTimeZone(timeZone);

            CronExpression activateRaceCardsExp = new CronExpression(properties.getProperties().getProperty(Util.ACTIVATE_RACECARDS_EXP_KEY));
            activateRaceCardsExp.setTimeZone(timeZone);

            CronExpression updateRaceAndRaceCardTodayExp = new CronExpression(properties.getProperties().getProperty(Util.UPDATE_RACE_AND_RACECARD_TODAY_EXP_KEY));
            updateRaceAndRaceCardTodayExp.setTimeZone(timeZone);

            CronExpression updateRaceAndRaceCardOtherExp = new CronExpression(properties.getProperties().getProperty(Util.UPDATE_RACE_AND_RACECARD_OTHER_EXP_KEY));
            updateRaceAndRaceCardOtherExp.setTimeZone(timeZone);

            CronExpression purgeExp = new CronExpression(properties.getProperties().getProperty(Util.PURGE_EXP_KEY));
            purgeExp.setTimeZone(timeZone);

            cronThreadPoolExecutor.schedule(raceDayCalendarJob, raceDayCalendarExp);
            cronThreadPoolExecutor.schedule(racingCardJob, racingCardExp);
            cronThreadPoolExecutor.schedule(activateRaceCardsJob, activateRaceCardsExp);
            cronThreadPoolExecutor.schedule(updateRaceAndRaceCardTodayJob, updateRaceAndRaceCardTodayExp);
            cronThreadPoolExecutor.schedule(updateRaceAndRaceCardOtherJob, updateRaceAndRaceCardOtherExp);
            cronThreadPoolExecutor.schedule(purgeJob, purgeExp);

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    CronThreadPoolExecutor getCronThreadPoolExecutor() {
        return cronThreadPoolExecutor;
    }
}
