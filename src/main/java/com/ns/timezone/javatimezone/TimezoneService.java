package com.ns.timezone.javatimezone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author nseo
 */
@Service
public class TimezoneService
{
    private static Logger log = LoggerFactory.getLogger(TimezoneService.class);

    private Map<String, TimezoneDisplayInfo> displayInfoMap;

    public Flux<TimezoneDisplayInfo> getAllTimezoneDisplay()
    {
        if (displayInfoMap == null)
        {
            String[] ids = TimeZone.getAvailableIDs();
            displayInfoMap = Arrays.stream(ids).map(id -> createTimezoneDisplayInfo(TimeZone.getTimeZone(id)))
                    .collect(Collectors.toMap(TimezoneDisplayInfo::getZoneId, item -> item));

            displayInfoMap.values().stream()
                    .forEach(System.out::println);
        }

        return Flux.fromIterable(getOrBuildTimezoneMap().values());
    }

    public Flux<String> getAllTimezoneIds()
    {
        return Flux.fromIterable(getOrBuildTimezoneMap().keySet());
    }

    private Map<String, TimezoneDisplayInfo> getOrBuildTimezoneMap()
    {
        if (displayInfoMap == null)
        {
            String[] ids = TimeZone.getAvailableIDs();
            displayInfoMap = Arrays.stream(ids).map(id -> createTimezoneDisplayInfo(TimeZone.getTimeZone(id)))
                    .collect(Collectors.toMap(TimezoneDisplayInfo::getZoneId, item -> item));
        }

        return displayInfoMap;
    }

    private static class DaylightSavingItem
    {
        private boolean begin;
        private String date;

        public DaylightSavingItem(boolean begin, String date)
        {
            this.begin = begin;
            this.date = date;
        }

        public boolean isBegin()
        {
            return begin;
        }

        public String getDate()
        {
            return date;
        }

        @Override
        public String toString()
        {
            String str = begin ? "starting" : "ending";
            return date + " [" + str + "]";
        }
    }

    private static class DaylightSavingRange
    {
        private DaylightSavingItem first;
        private DaylightSavingItem second;

        public DaylightSavingRange(DaylightSavingItem first, DaylightSavingItem second)
        {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString()
        {
            return first.toString() + " - " + second.toString();
        }

        public DaylightSavingItem getFirst()
        {
            return first;
        }

        public DaylightSavingItem getSecond()
        {
            return second;
        }
    }

    public static class TimezoneId
    {
        private String timezoneId;

        public TimezoneId(String timezoneId) {
            this.timezoneId = timezoneId;
        }

        public String getTimezoneId() {
            return timezoneId;
        }

        public void setTimezoneId(String timezoneId) {
            this.timezoneId = timezoneId;
        }
    }

    public static class TimezoneDisplayInfo
    {
        private String zoneId;
        private String GMT;
        private String DST;
        private DaylightSavingRange daylightSavingRange;

        public TimezoneDisplayInfo(String zoneId, String GMT, String DST, DaylightSavingRange daylightSavingRange)
        {
            this.zoneId = zoneId;
            this.GMT = GMT;
            this.DST = DST;
            this.daylightSavingRange = daylightSavingRange;
        }

        public String getZoneId()
        {
            return zoneId;
        }

        public String getGMT()
        {
            return GMT;
        }

        public String getDST()
        {
            return DST;
        }

        public DaylightSavingRange getDaylightSavingRange()
        {
            return daylightSavingRange;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(zoneId)
                    .append(" - ")
                    .append(GMT)
                    .append(", ")
                    .append(DST);

            if (daylightSavingRange != null)
            {
                String str = daylightSavingRange.toString();
                if (!str.isEmpty())
                {
                    sb.append(" (")
                            .append(str)
                            .append(")");
                }
            }
            return sb.toString();
        }
    }

    private static TimezoneDisplayInfo createTimezoneDisplayInfo(TimeZone tz)
    {
        String gmt = generateGMTString(tz.getRawOffset());
        String dst = generateGMTString(tz.getRawOffset() + tz.getDSTSavings());

        DaylightSavingRange daylightSavingRange = createDaylightSavingRange(tz.getID()).orElse(null);
        return new TimezoneDisplayInfo(tz.getID(), gmt, dst, daylightSavingRange);
    }


    private static String generateGMTString(int offsetInMilli)
    {
        long hours = TimeUnit.MILLISECONDS.toHours(offsetInMilli);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(offsetInMilli)
                - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);

        if (hours > 0)
        {
            return String.format("GMT+%d:%02d", hours, minutes);
        }
        else
        {
            return String.format("GMT%d:%02d", hours, minutes);
        }
    }

    private static Optional<DaylightSavingRange> createDaylightSavingRange(String tzId)
    {
        try
        {
            DateTimeZone zone = DateTimeZone.forID(tzId);
            DateTimeFormatter format = DateTimeFormat.mediumDateTime().withZone(zone);

            long current = System.currentTimeMillis();
            long next = zone.nextTransition(current);
            if (current != next)
            {
                DaylightSavingItem first = new DaylightSavingItem(!zone.isStandardOffset(next), format.print(next));

                current = next;
                next = zone.nextTransition(current);
                DaylightSavingItem second = new DaylightSavingItem(!zone.isStandardOffset(next), format.print(next));

                return Optional.of(new DaylightSavingRange(first, second));
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }

        return Optional.empty();
    }
}
