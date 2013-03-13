package edu.put.et.stik.mm.util;

import java.util.concurrent.TimeUnit;

public class TimeMeasurement {

	private long begin;
	private long duration;

	public void begin() {
		begin = System.nanoTime();
	}

	public void end() {
		final long end = System.nanoTime();
		duration = end - begin;
	}
	
	public long getNanos() {
		return duration;
	}

	public String getDurationString() {

		long tempDuration = duration;

		// Extract particular values.
		final long hours = TimeUnit.HOURS.convert(tempDuration, TimeUnit.NANOSECONDS);
		final long hourNanos = TimeUnit.HOURS.toNanos(hours);
		tempDuration -= hourNanos;

		final long minutes = TimeUnit.MINUTES.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		final long minuteNanos = TimeUnit.MINUTES.toNanos(minutes);
		tempDuration -= minuteNanos;

		final long seconds = TimeUnit.SECONDS.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		final long secondNanos = TimeUnit.SECONDS.toNanos(seconds);
		tempDuration -= secondNanos;

		final long milliseconds = TimeUnit.MILLISECONDS.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		final long millisecondNanos = TimeUnit.SECONDS.toNanos(milliseconds);
		tempDuration -= millisecondNanos;

		// Format the result.
		return String.format("%1$dh %2$dm %3$ds %4$dms", hours, minutes,
				seconds, milliseconds);
	}
}
