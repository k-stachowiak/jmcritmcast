package impossible.pivot.aggregators;

import static org.junit.Assert.*;
import impossible.pivot.aggregators.Aggregator;
import impossible.pivot.aggregators.StdevAggregator;

import org.junit.Test;

public class StdevAggregatorTest {

	@Test
	public void testGet() {

		// Assumptions.
		final double ARBITRARY_DOUBLE_1 = 1.0;
		final double ARBITRARY_DOUBLE_2 = 2.0;
		final double ARBITRARY_DOUBLE_3 = 3.0;

		// Obtained with LibreOffice's Calc
		final double expectedStdev = 1.0;

		// Exercise SUT
		Aggregator sut = new StdevAggregator();

		sut.put(ARBITRARY_DOUBLE_1);
		sut.put(ARBITRARY_DOUBLE_2);
		sut.put(ARBITRARY_DOUBLE_3);

		final double actualStdev = sut.get();

		// Assertions.
		assertEquals(expectedStdev, actualStdev, 0.01);
	}

}
