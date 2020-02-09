package net.devaction.transfersservice.api.util.timestamp;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class TimestampFormatter {

    private TimestampFormatter() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
            "EEEE dd-MMM-yyyy HH:mm:ss.SSSZ", new Locale("en"));

    public static String getTimestampString(long epochMilli) {

        ZonedDateTime dateTime = Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault());

        return epochMilli + " (" + FORMATTER.format(dateTime) + ")";
    }
}
