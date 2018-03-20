package com.creang.task;

import com.creang.common.MyProperties;
import com.creang.common.Util;
import com.creang.service.db.DeleteRaceCardAndRaceService;

import java.time.LocalDate;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class PurgeTask {

    private final Logger logger = loggerUtil.getLogger();
    private final DeleteRaceCardAndRaceService deleteRaceCardAndRaceService = new DeleteRaceCardAndRaceService();

    public void run() {

        MyProperties properties = MyProperties.getInstance();
        String minusDays = properties.getProperties().getProperty(Util.PURGE_MINUS_DAYS_KEY);

        logger.info("Start: purgeTask");
        deleteRaceCardAndRaceService.delete(LocalDate.now().minusDays(Integer.parseInt(minusDays)));
        logger.info("Done: purgeTask");
    }
}
