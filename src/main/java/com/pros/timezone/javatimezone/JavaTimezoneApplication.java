package com.pros.timezone.javatimezone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class JavaTimezoneApplication {

	public static void main(String[] args) {

		String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			System.out.println(displayTimeZone(TimeZone.getTimeZone(id)));
		}

		System.out.println("\nTotal TimeZone ID " + ids.length);

	}

	private static String displayTimeZone(TimeZone tz) {

		TimeOffset rawOffset = buildOffset(tz.getRawOffset());
		TimeOffset dayLightSavingOffset = buildOffset(tz.getRawOffset() + tz.getDSTSavings());

		String range = daylightSavingRange(tz.getID());

		StringBuilder sb = new StringBuilder();
		sb.append("Raw: ")
				.append(rawOffset)
				.append(", DST: ")
				.append(dayLightSavingOffset)
				.append(" ")
				.append(range)
				.append(" ")
				.append(tz.getID());

		return sb.toString();
	}

	private static String daylightSavingRange(String tzId)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			DateTimeZone zone = DateTimeZone.forID(tzId);
			DateTimeFormatter format = DateTimeFormat.mediumDateTime();

			long current = System.currentTimeMillis();
			for (int i = 0; i < 2; i++)
			{
				long next = zone.nextTransition(current);
				if (current == next)
				{
					break;
				}
				String str = !zone.isStandardOffset(next) ? " [starting] " : " [ended] ";
				sb.append(format.print(next) + str + " - ");
				current = next;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
		}
		return sb.toString();
	}


	private static TimeOffset buildOffset(int offsetInMilli)
	{
		return new TimeOffset(offsetInMilli);
	}

	private static class TimeOffset
	{
		private long hours;
		private long minutes;

		public TimeOffset(int offsetInMilli)
		{
			hours = TimeUnit.MILLISECONDS.toHours(offsetInMilli);
			minutes = TimeUnit.MILLISECONDS.toMinutes(offsetInMilli)
					- TimeUnit.HOURS.toMinutes(hours);
			// avoid -4:-30 issue
			minutes = Math.abs(minutes);
		}

		@Override
		public String toString()
		{
			if (hours > 0)
			{
				return String.format("GMT+%d:%02d", hours, minutes);
			}
			else
			{
				return String.format("GMT%d:%02d", hours, minutes);
			}
		}
	}
}
