package com.creang.task;

import com.creang.common.Util;
import com.creang.model.Leg;
import com.creang.model.LegParticipant;
import com.creang.model.RaceCard;
import com.creang.service.db.ActivateRaceCardService;
import com.creang.service.db.FetchInactiveRaceCardsService;
import com.creang.service.db.FetchLegService;
import com.creang.service.db.FetchParticipantIdService;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class ActivateRaceCardTask {

    private final Logger logger = loggerUtil.getLogger();
    private final FetchInactiveRaceCardsService fetchInactiveRaceCardsService = new FetchInactiveRaceCardsService();
    private final FetchLegService fetchLegService = new FetchLegService();
    private final FetchParticipantIdService fetchParticipantIdService = new FetchParticipantIdService();
    private final ActivateRaceCardService activateRaceCardService = new ActivateRaceCardService();

    public void run() {

        logger.info("Start: ActivateRaceCardTask");
        List<RaceCard> raceCards = fetchInactiveRaceCardsService.fetch(LocalDate.now());

        for (RaceCard raceCard : raceCards) {

            List<Leg> legs = fetchLegService.fetch(raceCard.getId());

            if (legs.size() == Util.getNumberOfLegs(raceCard.getBetType())) {

                for (Leg leg : legs) {

                    raceCard.getLegs().add(leg);
                    List<Integer> participantIds = fetchParticipantIdService.fetch(leg.getRaceId());

                    for (Integer participantId : participantIds) {
                        LegParticipant lp = new LegParticipant();
                        lp.setLegId(leg.getId());
                        lp.setParticipantId(participantId);
                        leg.getLegParticipants().add(lp);
                    }
                }

                if (raceCard.getLegs().stream().allMatch(leg -> leg.getLegParticipants().size() > 0)) {
                    raceCard.setActivated(true);
                }
            }
        }

        activateRaceCardService.update(raceCards);
        logger.info("Done: ActivateRaceCardTask");
    }
}
