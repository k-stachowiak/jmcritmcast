package impossible.tfind.generalprim;

import static org.junit.Assert.*;
import impossible.model.topology.Edge;
import impossible.model.topology.Node;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CheapestNonBreakingEdgeSelectorTest {

	private int uniqueId;

	private int getUniqueId() {
		return uniqueId++;
	}

	@Before
	public void setUp() {
		uniqueId = 1;
	}

	@Test
	public void testSelect() {

		// Prepare constants.
		// ------------------

		final double ANY_DOUBLE = 1.0;

		// Costs enforcing a certain order of the selection.
		final double CHEAPEST_COST = 1.0;
		final double MEDIUM_COST = 2.0;
		final double EXPENSIVE_COST = 3.0;

		// The longest path will take 2 hops therefore the constraint
		// is assumed to be satisfied for 1 hop and broken for 2.
		final double COMMON_CONSTRAINED = 5.0;
		final double CONSTRAINT = 1.5 * COMMON_CONSTRAINED;

		// Prepare the model.
		// ------------------

		// Create the nodes.
		Node A = new Node(getUniqueId(), ANY_DOUBLE, ANY_DOUBLE);
		Node B = new Node(getUniqueId(), ANY_DOUBLE, ANY_DOUBLE);
		Node C = new Node(getUniqueId(), ANY_DOUBLE, ANY_DOUBLE);
		Node D = new Node(getUniqueId(), ANY_DOUBLE, ANY_DOUBLE);

		// Create the edges.
		Double[] aMetrics = new Double[] { CHEAPEST_COST, COMMON_CONSTRAINED };
		Double[] bMetrics = new Double[] { MEDIUM_COST, COMMON_CONSTRAINED };
		Double[] cMetrics = new Double[] { EXPENSIVE_COST, COMMON_CONSTRAINED };
		final Edge a = new Edge(A.getId(), B.getId(), Arrays.asList(aMetrics));
		final Edge b = new Edge(A.getId(), C.getId(), Arrays.asList(bMetrics));
		final Edge c = new Edge(B.getId(), D.getId(), Arrays.asList(cMetrics));

		// Prepare the cuts.
		List<Edge> cut1 = Arrays.asList(new Edge[] { a, b });
		List<Edge> cut2 = Arrays.asList(new Edge[] { b, c });
		List<Edge> cut3 = Arrays.asList(new Edge[] { c });

		// Set the constraints.
		List<Double> constraints = Arrays.asList(new Double[] { CONSTRAINT });

		// Instantiate SUT.
		// ----------------
		EdgeSelector selector = new CheapestNonBreakingEdgeSelector(constraints);
		selector.reset();

		// Exercise SUT.
		// -------------
		assertEquals(a, selector.select(cut1));
		assertEquals(b, selector.select(cut2));
		assertNull(selector.select(cut3)); // Constraints broken here.
	}
}
