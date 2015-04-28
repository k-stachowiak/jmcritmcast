package util;

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
		return String.format("%1$d:%2$d:%3$d.%4$d", hours, minutes,
				seconds, milliseconds);
	}
}
