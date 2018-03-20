package com.creang.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SimpleLogFormatter extends SimpleFormatter {

    @Override
    public String format(LogRecord record) {
        SimpleDateFormat logTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(record.getMillis());
        return logTime.format(cal.getTime()) + " || " + record.getMessage() + "\n";
    }
}
