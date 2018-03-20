package com.creang.common;

import aisbean.AtgDate;
import aisbean.AtgDateTime;
import aisbean.AtgTime;
import aisbean.TrackKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Util {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static final String TIME_ZONE_KEY = "TIME_ZONE";
    public static final String AIS_USERNAME_KEY = "AIS_USERNAME";
    public static final String AIS_PASSWORD_KEY = "AIS_PASSWORD";
    public static final String JDBC_USERNAME_KEY = "JDBC_USERNAME";
    public static final String JDBC_PASSWORD_KEY = "JDBC_PASSWORD";
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String CONFIG_PROPERTIES_FILENAME = "config.properties";
    public static final String JDBC_URL_WINDOWS = "jdbc:mariadb://localhost/myatg?autoReconnect=true&amp;pipe=C:\\tmp\\mysql.sock";
    public static final String JDBC_URL_LINUX = "jdbc:mariadb://localhost/myatg?autoReconnect=true&amp;localSocket=/var/run/mysqld/mysqld.sock";
    public static final String RACE_DAY_CALENDAR_EXP_KEY = "RACE_DAY_CALENDAR_EXP";
    public static final String RACING_CARD_EXP_KEY = "RACING_CARD_EXP";
    public static final String ACTIVATE_RACECARDS_EXP_KEY = "ACTIVATE_RACECARDS_EXP";
    public static final String UPDATE_RACE_AND_RACECARD_TODAY_EXP_KEY = "UPDATE_RACE_AND_RACECARD_TODAY_EXP";
    public static final String UPDATE_RACE_AND_RACECARD_OTHER_EXP_KEY = "UPDATE_RACE_AND_RACECARD_OTHER_EXP";
    public static final String PURGE_EXP_KEY = "PURGE_EXP";
    public static final String PURGE_MINUS_DAYS_KEY = "PURGE_MINUS_DAYS";
    public static final String LOGGER_NAME = "AtgImportLogger";
    public static final String LOG_FILE_NAME = "AtgImportLogFile.log";

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static LocalDate getLocalDateFromAtgDate(AtgDate raceDayDate) {
        return LocalDate.of(raceDayDate.getYear(), raceDayDate.getMonth(), raceDayDate.getDate());
    }

    public static LocalDateTime getLocalDateTimeFromAtgDateTime(AtgDateTime atgDateTime) {
        return LocalDateTime.of(getLocalDateFromAtgDate(atgDateTime.getDate()), getLocalTimeFromAtgTime(atgDateTime.getTime()));
    }

    public static LocalTime getLocalTimeFromAtgTime(AtgTime atgTime) {

        int hour = atgTime.getHour();
        int minute = atgTime.getMinute();
        int second = atgTime.getSecond();
        int nano = atgTime.getTenth() * 100000000;

        return LocalTime.of(hour, minute, second, nano);
    }

    public static int getNumberOfLegs(String code) {

        switch (code) {
            case "V3":
                return 3;
            case "V4":
                return 4;
            case "V5":
                return 5;
            case "V64":
                return 6;
            case "V65":
                return 6;
            case "V75":
                return 7;
            case "GS75":
                return 7;
            case "V86":
                return 8;
        }
        return -1;
    }

    public static AtgDate getAtgDateFromLocalDate(LocalDate raceDayDate) {

        AtgDate atgDate = new AtgDate();
        atgDate.setYear(raceDayDate.getYear());
        atgDate.setMonth(raceDayDate.getMonth().getValue());
        atgDate.setDate(raceDayDate.getDayOfMonth());

        return atgDate;
    }

    public static TrackKey getTrackKey(int trackId) {

        TrackKey trackKey = new TrackKey();
        trackKey.setTrackId(trackId);

        return trackKey;
    }

    public static String getKmTimefromAtgTime(AtgTime atgTime) {

        String minute = "" + atgTime.getMinute();
        String second = atgTime.getSecond() < 10 ? "0" + atgTime.getSecond() : "" + atgTime.getSecond();
        String tenth = "" + atgTime.getTenth();
        return minute + ":" + second + "," + tenth;
    }

    public static String getCurrentLocalDateTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getLocalDateTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
