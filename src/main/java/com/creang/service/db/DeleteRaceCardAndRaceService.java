package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.MiniConnectionPoolManager;

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
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void delete(LocalDate localDate) {

        String prepStmnt1 = "delete from racecard where RaceDayDate <= ?";
        String prepStmnt2 = "delete from race where RaceDayDate <= ?";
        Connection conn = null;

        try {

            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(prepStmnt2)) {

                    stmt1.setDate(1, Date.valueOf(localDate));
                    stmt2.setDate(1, Date.valueOf(localDate));
                    stmt1.executeUpdate();
                    stmt2.executeUpdate();
                    conn.commit();
                }

            } catch (SQLException ex) {
                conn.rollback();
                logger.severe(ex.getMessage());
            }

            conn.setAutoCommit(true);

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            poolManager.release(conn);
        }
    }
}
