import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

class UtcUtilsTest {

    String inputValue = "2022-10-15T09:45:00.000Z";
    static UtcUtils utcUtils = null;


    @BeforeAll
    static void setup() {
        utcUtils = new UtcUtils();
    }

    /**
     * convert UTC to local time when default zoneid is used in this case [Europe/Dublin]
     */
    @Test
    void convertToLocalTime_default() {
        ZoneId zoneid = ZoneId.systemDefault();

//        will need to change zoneid if not Europe/Dublin
        String expectedValue = "2022-10-15T10:45+01:00[Europe/Dublin]";
        String actualValue = utcUtils.convertInstantToZDT(inputValue, zoneid);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    void createLocalDateTime_default() {
        //        will need to change zoneid if not Europe/Dublin
        String dateSubstring = "2022-10-15 09:45";
        LocalDateTime ldt = utcUtils.createLocalDateTime(dateSubstring);
        String actualValue = ldt.toString();
        String expectedValue = "2022-10-15T09:45";
        assertEquals(expectedValue, actualValue);
    }

    /**
     * convert localDateTime to UTC
     * 1 create a ldt using local time
     * 2 call second function to convert the ldt to UTC
     */
    @Test
    void createLocalDateTime_UTC() {
        String dateSubstring = "2022-10-15 09:45";
        LocalDateTime ldt = utcUtils.createLocalDateTime(dateSubstring);
        String actualValue = String.valueOf(utcUtils.convertLDTtoZDT_UTC(ldt));
        String expectedValue = "08:45";
        assertEquals(expectedValue, actualValue);
    }


    /**
     * convert UTC to local time where default zoneid is used
     */
    @Test
    void convertToLocalTime_default_offsetformat() {
        ZoneId zoneid = ZoneId.systemDefault();

//        will need to change zoneid if not Europe/Dublin
        String expectedValue = "2022-10-17T13:30+01:00[Europe/Dublin]";
        String actualValue = utcUtils.convertInstantToZDT("2022-10-17T12:30:00.000+00:00", zoneid);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * convert UTC to local time where Europe/Berlin zoneid is used
     */
    @Test
    void convertToLocalTime_Europe_Berlin() {
        ZoneId zoneid = ZoneId.of("Europe/Berlin");
        String expectedValue = "2022-10-15T11:45+02:00[Europe/Berlin]";
        String actualValue = utcUtils.convertInstantToZDT(inputValue, zoneid);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * convert UTC to local time where Pacific/Kiritimati zoneid is used
     */
    @Test
    void convertToLocalTime_Pacific_Kiritimati() {
        try {
            ZoneId zoneid = ZoneId.of("Pacific/Kiritimati");
            String expectedValue = "2022-10-15T23:45+14:00[Pacific/Kiritimati]";
            String actualValue = utcUtils.convertInstantToZDT(inputValue, zoneid);
            assertEquals(expectedValue, actualValue);
        } catch (ZoneRulesException zre) {
            System.out.println("ZoneRulesException: " + zre.getMessage());
        }
    }

    /**
     * convert calendar time to UTC
     */
    @Test
    void getDateISO8601() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(2022, Calendar.OCTOBER, 15, 9, 15, 0);

        Date date = calendar.getTime();

        String expectedValue = "2022-10-15T08:15:00.000Z";
        String actualValue = utcUtils.getDateISO8601(date);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * display all zoneids
     */
    @Test
    void getTimeZoneIds() {
        String[] arrayTimeZones = utcUtils.getTimeZoneIds();

        int expectedValue = 628;  // at time of testing October 2022
        int actualValue = arrayTimeZones.length;
        assertEquals(expectedValue, actualValue);

//        comment in or out as reqd to see list of available id's
        for (String zoneId : arrayTimeZones) {
            System.out.println(zoneId);
        }
    }

    /**
     * display all java zoneids and time offsets
     */
    @Test
    void getTimeZoneIds_offsets() {
        String[] arrayTimeZones = utcUtils.getTimeZoneIds();
//        comment in or out as required
        int j = 0;
        for (String zoneIdString : arrayTimeZones) {
            try {
                ZoneId zoneid = ZoneId.of(zoneIdString);
                String actualValue = utcUtils.convertInstantToZDT(inputValue, zoneid);
                System.out.printf("#: %3d %58s %s\n", ++j, actualValue, utcUtils.getRegex(actualValue));

            } catch (ZoneRulesException zre) {
                System.out.println("ZoneRulesException for " + zoneIdString + " , " + zre.getMessage());
            }
        }
    }

    /**
     * display all java zoneids and time offsets
     */
    @Test
    void getTimeZoneIds_offsets_JSON() {
        String[] arrayTimeZones = utcUtils.getTimeZoneIds();
//        comment in or out as required
        int j = 0;
        for (String zoneIdString : arrayTimeZones) {
            try {
                ZoneId zoneid = ZoneId.of(zoneIdString);
                String actualValue = utcUtils.convertInstantToZDT(inputValue, zoneid);

                String jsonString = utcUtils.getRegex_json(actualValue);
                var utcJSON = new JSONObject(jsonString);

                String name = (String) utcJSON.get("name");
                String offset = (String) utcJSON.get("offset");
                Boolean isZuluTime = (Boolean) utcJSON.get("Zulu");

                System.out.printf("JSON #: %3d %35s %s    Zulu %b\n", ++j, name, offset, isZuluTime);

            } catch (ZoneRulesException zre) {
                System.out.println("ZoneRulesException for " + zoneIdString + " , " + zre.getMessage());
            }
        }
    }

    @Test
    void getUtcInstant_now() {
        System.out.println(" now " + utcUtils.getInstant_now());
    }

    /**
     * get an instant parsing a string for date
     */
    @Test
    void getUtcInstant_set() {
        System.out.println(" instant set " + utcUtils.getInstant_set(inputValue));
        String actualValue = utcUtils.getInstant_set(inputValue);
        String expectedValue = "2022-10-15T09:45:00Z";
        assertEquals(expectedValue, actualValue);
    }

    /**
     * get an instant parsing a string for date
     * with 60 days prior to
     */
    @Test
    void getUtcInstant_set_day_difference_minus_60() {
        System.out.println(" instant set " + utcUtils.getInstant_set(inputValue));
        String actualValue = utcUtils.getInstant_set_plusdays(inputValue, -60);
        String expectedValue = "2022-08-16T09:45:00Z";
        assertEquals(expectedValue, actualValue);
    }

    /**
     * get an instant parsing a string for date
     * with 60 days after date
     */
    @Test
    void getUtcInstant_set_day_difference_plus_60() {
        System.out.println(" instant set " + utcUtils.getInstant_set(inputValue));
        String actualValue = utcUtils.getInstant_set_plusdays(inputValue, 60);
        String expectedValue = "2022-12-14T09:45:00Z";
        assertEquals(expectedValue, actualValue);
    }

    /**
     * force DateTimeParseException error
     */
    @Test
    void getUtcInstant_error_parse() {
        String actualValue = utcUtils.getInstant_set("sd45323");
        assertEquals("DateTimeParseException: sd45323", actualValue);
    }

    @Test
    void get_DateTimeFormatter_default_zone() {

        ZoneId zoneid = ZoneId.systemDefault();
        String actualValue = utcUtils.getInstant_DateTimeFormatter(inputValue, zoneid);
        String expectedValue = "15.10.2022 10:45";
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void get_DateTimeFormatter_default_zone_parse_error() {

        ZoneId zoneid = ZoneId.systemDefault();
        String actualValue = utcUtils.getInstant_DateTimeFormatter("error string", zoneid);
        String expectedValue = "DateTimeParseException: error string";
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void get_DateTimeFormatter_Australia_Sydney() {
        ZoneId zoneid = ZoneId.of("Australia/Sydney");
        String actualValue = utcUtils.getInstant_DateTimeFormatter(inputValue, zoneid);
        String expectedValue = "15.10.2022 20:45";
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void get_Instant_milli() {
        String inputValueVov = "2022-10-15 09:45";
        ZoneId zoneid = ZoneId.of("Australia/Sydney");
        String actualValue = utcUtils.getInstant_Milli(inputValueVov, zoneid);
        String expectedValue = "2022-10-14T22:45:00Z";
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void get_Instant_milli_parse_error() {
        String inputValueVov = "2022-10-15 xxxxxx";
        ZoneId zoneid = ZoneId.of("Australia/Sydney");
        String actualValue = utcUtils.getInstant_Milli(inputValueVov, zoneid);
        String expectedValue = "ParseException: 2022-10-15 xxxxxx";
        assertEquals(expectedValue, actualValue);
    }

    /**
     * get zoned date time parsing string for date and using default timezoneid
     */
    @Test
    void get_zonedDateTime() {
        String inputValue = "2022-10-15 09:45";
        ZoneId zoneid = ZoneId.systemDefault();
        String actualValue = utcUtils.get_ZonedDateTime_default(inputValue, zoneid);
        String expectedValue = "2022-09-15T08:45:00Z";
        assertEquals(expectedValue, actualValue);
    }
}