package com.pros.timezone.javatimezone;

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
		StringBuilder sb = new StringBuilder();
		sb.append("Raw: ")
				.append(rawOffset)
				.append(", DST: ")
				.append(dayLightSavingOffset)
				.append(" ")
				.append(tz.getID());

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
