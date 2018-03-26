package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class DeleteRaceCardAndRaceService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void delete(LocalDate localDate) {

        String sql1 = "delete from racecard where RaceDayDate <= ?";
        String sql2 = "delete from race where RaceDayDate <= ?";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {

                    ps1.setDate(1, Date.valueOf(localDate));
                    ps2.setDate(1, Date.valueOf(localDate));
                    ps1.executeUpdate();
                    ps2.executeUpdate();
                }
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                logger.severe(e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }
}
