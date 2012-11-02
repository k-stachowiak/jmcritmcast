package impossible.pivot.aggregators;

import static org.junit.Assert.*;
import impossible.pivot.aggregators.Aggregator;
import impossible.pivot.aggregators.SumAggregator;

import org.junit.Test;

public class SumAggregatorTest {

	@Test
	public void testGet() {
		
		// Assumptions.
		final double ARBITRARY_DOUBLE_1 = 1.0;
		final double ARBITRARY_DOUBLE_2 = 2.0;
		final double ARBITRARY_DOUBLE_3 = 3.0;

		final double expectedSum = ARBITRARY_DOUBLE_1
				+ ARBITRARY_DOUBLE_2 + ARBITRARY_DOUBLE_3;
		
		// Exercise SUT
		Aggregator sut = new SumAggregator();
		
		sut.put(ARBITRARY_DOUBLE_1);
		sut.put(ARBITRARY_DOUBLE_2);
		sut.put(ARBITRARY_DOUBLE_3);
		
		final double actualSum = sut.get();
		
		// Assertions.
		assertEquals(expectedSum, actualSum, 0.01);
	}

}
