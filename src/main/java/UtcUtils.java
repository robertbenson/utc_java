import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class UtcUtils {

    public UtcUtils() {
    }


    /**
     * returns a string , containing a UTC time, from a zonedDateTime, with offset and default locale using an ISO 8601 formatted input string
     * <p>
     * 2022-10-15T09:45:00.000Z
     * YYYY-MM-DDThh:mm:ss.SSSZ
     * <p>
     * YYYY = four-digit year
     * MM   = two-digit month (01=January, etc.)
     * DD   = two-digit day of month (01 through 31)
     * T    = indicates that the time value is the time in Greenwich, England, or UTC, time.
     * hh   = two digits of hour (00 through 23) (am/pm NOT allowed)
     * mm   = two digits of minute (00 through 59)
     * ss   = two digits of second (00 through 59)
     * SSS  = one or more digits representing a decimal fraction of a second
     * TZD  = time zone designator (Z or +hh:mm or -hh:mm)
     *
     * @param iso8601String e.g. "2022-10-15T09:45:00.000Z"
     * @param zoneid
     * @return string utc time, offset (in this case +01:00, and locale)  "2022-10-15T10:45+01:00[Europe/Dublin]"
     */
    public static String convertInstantToZDT(String iso8601String, ZoneId zoneid) {
        Instant instant = Instant.parse(iso8601String);
        ZonedDateTime defaultTime = instant.atZone(zoneid);

        return String.valueOf(defaultTime);
    }

    public static LocalDateTime createLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return localDateTime;
    }


    /**
     * convert LocalDateTime to ZonedDateTime
     *
     * @param localDateTime
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
     */
    public static LocalTime convertLDTtoZDT_UTC(LocalDateTime localDateTime) {
        ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        return utcZoned.toLocalTime();
    }


    /**
     * Using date, return an ISO8601 formatted string
     *
     * @param date
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
     */
    public static String getDateISO8601(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    /**
     * Using the TimeZone getAvailableId's method, a string array of available zone ids is returned
     *
     * @return String array of available time zones
     */
    public static String[] getTimeZoneIds() {
        return TimeZone.getAvailableIDs();
    }

    /**
     * @return String representation of UTC time
     */
    public String getInstant_now() {
        return Instant.now().toString();
    }

    /**
     * @param date as string
     * @return String representation of UTC time
     */
    public String getInstant_set(String date) {
        try {
            Instant instant
                    = Instant.parse(date);
            return instant.toString();
        } catch (DateTimeParseException dtpe) {
            return "DateTimeParseException: " + date;
        }
    }

    /**
     * @param date as string
     * @param daysNumber integer, will contain a positive or negative value
     * @return String representation of UTC time
     */
    public String getInstant_set_plusdays(String date, int daysNumber) {
        try {
            Instant instant
                    = Instant.parse(date);

//            instant is immutable, therefore make another occurrence of instant with the addition, that will be either a positive or negative value
            Instant valueDiff
                    = instant.plus(daysNumber, ChronoUnit.DAYS);
            return valueDiff.toString();
        } catch (DateTimeParseException dtpe) {
            return "DateTimeParseException: " + date;
        }
    }

    /**
     * @return String
     */
    public String getInstant_DateTimeFormatter(String dateTime, ZoneId zoneid) {
        final String PATTERN_FORMAT = "dd.MM.yyyy HH:mm";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(zoneid);

        try {
            Instant instant = Instant.parse(dateTime);
            return formatter.format(instant);
        } catch (DateTimeParseException dtpe) {
            return "DateTimeParseException: " + dateTime;
        }
    }

    /**
     * pass in a date time string formatted as yyyy-MM-dd HH:mm, zoneid and return a string
     *
     * @param dateTime string formatted as  e.g. 2022-10-15 09:45
     * @param zoneid
     * @return String formatted as 2022-10-14T22:45:00Z
     */

    public String getInstant_Milli(String dateTime, ZoneId zoneid) {
        final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone(zoneid));

        Date date = null;
        try {
            date = formatter.parse(dateTime);
            String formattedDateString = formatter.format(date);
            Instant instant = Instant.ofEpochMilli(date.getTime());
            return instant.toString();
        } catch (ParseException e) {
            return "ParseException: " + dateTime;
        }
    }

    public String get_ZonedDateTime_default(String datetime, ZoneId zoneId) {
        ZonedDateTime zdt =
                ZonedDateTime.of(2022, 9, 15, 9, 45, 0, 0, zoneId);
        String zstTime = zdt.format(DateTimeFormatter.ISO_INSTANT);
        return zstTime;
    }

    /**
     * take a date time string ,
     * separate name and offset using regex pattern,
     * reformat and return string
     *
     * @param searchString  e.g. 2022-10-15T09:45Z[Africa/Abidjan]
     * @return String by name and offset or Zulu          [Africa/Abidjan] 09:45Z
     */
    public String getRegex(String searchString) {
//  regex match +00:00, -00:00 or 00:00Z
        Pattern p = Pattern.compile("(([-+]\\d{2}:\\d{2}|\\d{2}:\\d{2}Z))(\\[.*\\])");
        Matcher matcher = p.matcher(searchString);

        if (matcher.find()) {
            String g0 = matcher.group(0);
            String g1 = matcher.group(1);
            String g2 = matcher.group(2);
            String g3 = matcher.group(3);

            return format(" %35s %s", g3, g2);
        }
        return "no regex match: " + searchString;
    }

    public String getRegex_json(String searchString) {
//  regex match +00:00, -00:00 or 00:00Z
        Pattern p = Pattern.compile("(([-+]\\d{2}:\\d{2}|\\d{2}:\\d{2}Z))(\\[.*\\])");
        Matcher matcher = p.matcher(searchString);

        if (matcher.find()) {
            String g0 = matcher.group(0);
            String g1 = matcher.group(1);
            String g2 = matcher.group(2);
            String g3 = matcher.group(3);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", g3);
            jsonObject.put("offset", g2);

            if (g2.contains("Z")) {
                jsonObject.put("Zulu", true);
            } else {
                jsonObject.put("Zulu", false);
            }

            return jsonObject.toString();
        }
        return "no regex match: " + searchString;
    }
}
