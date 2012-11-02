package impossible.pivot.aggregators;

import static org.junit.Assert.*;
import impossible.pivot.aggregators.Aggregator;
import impossible.pivot.aggregators.CountAggregator;

import org.junit.Test;

public class CountAggregatorTest {

	@Test
	public void testGet() {
		
		// Assumptions.
		final double ANY_DOUBLE = 1.0;
		final double expectedCount = 3.0;

		// Exercise SUT
		Aggregator sut = new CountAggregator();

		for(int i = 0; i < (int)expectedCount; ++i) {
			sut.put(ANY_DOUBLE);
		}

		final double actualCount = sut.get();

		// Assertions.
		assertEquals(expectedCount, actualCount, 0.0);
	}

}
