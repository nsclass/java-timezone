package com.pros.timezone.javatimezone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
public class JavaTimezoneApplication {

	private static Logger log = LoggerFactory.getLogger(JavaTimezoneApplication.class);

	public static void main(String[] args) {
		buildTimezoneDisplayInfoList()
				.forEach(System.out::println);
	}

	private static List<TimezoneDisplayInfo> buildTimezoneDisplayInfoList()
	{
		String[] ids = TimeZone.getAvailableIDs();
		return Arrays.stream(ids).map(id -> createTimezoneDisplayInfo(TimeZone.getTimeZone(id)))
				.collect(Collectors.toList());
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

		@Override
		public String toString()
		{
			String str = begin ? "starting" : "ending";
			return date + "[" + str + "]";
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
	}

	private static class TimezoneDisplayInfo
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
			DateTimeFormatter format = DateTimeFormat.mediumDateTime();

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
