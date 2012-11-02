package impossible.pivot.aggregators;

import static org.junit.Assert.*;
import impossible.pivot.aggregators.Aggregator;
import impossible.pivot.aggregators.MeanAggregator;

import org.junit.Test;

public class MeanAggregatorTest {

	@Test
	public void testGet() {
		// Assumptions.
		final double ARBITRARY_DOUBLE_1 = 1.0;
		final double ARBITRARY_DOUBLE_2 = 2.0;
		final double ARBITRARY_DOUBLE_3 = 3.0;

		final double expectedMean = (ARBITRARY_DOUBLE_1 + ARBITRARY_DOUBLE_2
				+ ARBITRARY_DOUBLE_3) / 3.0;

		// Exercise SUT
		Aggregator sut = new MeanAggregator();

		sut.put(ARBITRARY_DOUBLE_1);
		sut.put(ARBITRARY_DOUBLE_2);
		sut.put(ARBITRARY_DOUBLE_3);

		final double actualMean = sut.get();

		// Assertions.
		assertEquals(expectedMean, actualMean, 0.01);
	}

}
