package impossible.util;

import java.util.concurrent.TimeUnit;

public class TimeMeasurement {

	private long begin;
	private long duration;

	public void begin() {
		begin = System.nanoTime();
	}

	public void end() {
		duration = System.nanoTime() - begin;
	}

	public String getDurationString() {

		long tempDuration = duration;

		// Extract particular values.
		long hours = TimeUnit.HOURS.convert(tempDuration, TimeUnit.NANOSECONDS);
		long hourNanos = TimeUnit.HOURS.toNanos(hours);
		tempDuration -= hourNanos;

		long minutes = TimeUnit.MINUTES.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		long minuteNanos = TimeUnit.MINUTES.toNanos(minutes);
		tempDuration -= minuteNanos;

		long seconds = TimeUnit.SECONDS.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		long secondNanos = TimeUnit.SECONDS.toNanos(seconds);
		tempDuration -= secondNanos;

		long milliseconds = TimeUnit.MILLISECONDS.convert(tempDuration,
				TimeUnit.NANOSECONDS);
		long millisecondNanos = TimeUnit.SECONDS.toNanos(milliseconds);
		tempDuration -= millisecondNanos;

		// Format the result.
		return String.format("%1$dh %2$dm %3$ds %4$dms", hours, minutes,
				seconds, milliseconds);
	}
}
