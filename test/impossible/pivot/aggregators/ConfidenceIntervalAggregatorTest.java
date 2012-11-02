package impossible.pivot.aggregators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfidenceIntervalAggregatorTest {

	@Test
	public void testGet() {
		
		// Assumptions.
		final double ARBITRARY_DOUBLE_1 = 1.0;
		final double ARBITRARY_DOUBLE_2 = 2.0;
		final double ARBITRARY_DOUBLE_3 = 3.0;

		// Confidence level in percents.
		final double alpha = 0.9;

		final double expectedConfidenceInterval = 1.8993133685965398;

		// Exercise SUT
		Aggregator sut = new ConfidenceIntervalAggregator(
				alpha);

		sut.put(ARBITRARY_DOUBLE_1);
		sut.put(ARBITRARY_DOUBLE_2);
		sut.put(ARBITRARY_DOUBLE_3);

		final double actualConfidenceInterval = sut.get();

		// Assertions.
		assertEquals(expectedConfidenceInterval, actualConfidenceInterval, 0.01);
	}

}
