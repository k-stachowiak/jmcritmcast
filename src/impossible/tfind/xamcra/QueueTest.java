package impossible.tfind.xamcra;

import static org.junit.Assert.*;

import java.util.TreeMap;

import impossible.model.topology.Node;

import org.junit.Test;

public class QueueTest {

	private final int ANY_INT = -1;
	private final double ANY_DOUBLE = -1.0;
	private final PathNode ANY_PATH_NODE = new PathNode(null, ANY_INT, null);

	@Test
	public void testQueueEmpty() {

		// Instantiate SUT.
		final Queue sut = new Queue();

		// Exercise SUT.
		final boolean expectedBeforePush = true;
		final boolean actualBeforePush = sut.isEmpty();

		sut.push(ANY_DOUBLE, ANY_PATH_NODE);

		final boolean expectedAfterPush = false;
		final boolean actualAfterPush = sut.isEmpty();

		sut.pop();

		final boolean expectedAfterPop = true;
		final boolean actualAfterPop = sut.isEmpty();

		// Perform assertions.
		assertEquals(expectedBeforePush, actualBeforePush);
		assertEquals(expectedAfterPush, actualAfterPush);
		assertEquals(expectedAfterPop, actualAfterPop);
	}

	@Test
	public void testPop() {

		// Constants.
		final double cheapLabel = 1.0;
		final double expensiveLabel = 10.0;

		final int someInt = 1;
		final int anotherInt = 2;

		// Just make sure these fail when compared for equality.
		final PathNode someNode = new PathNode(null, someInt, null);
		final PathNode anotherNode = new PathNode(null, anotherInt, null);

		// Base case
		// ---------

		// Instantiate SUT.
		Queue sut = new Queue();

		// Exercise SUT.
		sut.push(cheapLabel, someNode);
		sut.push(expensiveLabel, anotherNode);

		PathNode expectedPopped = someNode;
		PathNode actualPopped = sut.pop();

		// Assertions.
		assertEquals(expectedPopped, actualPopped);

		// Reverse case
		// ------------

		// Instantiate SUT.
		sut = new Queue();

		// Exercise SUT.
		sut.push(expensiveLabel, anotherNode);
		sut.push(cheapLabel, someNode);

		expectedPopped = someNode;
		actualPopped = sut.pop();

		// Assertions.
		assertEquals(expectedPopped, actualPopped);
	}

	@Test
	public void testFindMaxTo() {

		// Constants.
		final int nodeId1 = 1;
		final int nodeId2 = 2;

		// Prepare model.
		final Node targetNode = new Node(nodeId1, ANY_DOUBLE, ANY_DOUBLE);
		final Node nonTargetNode = new Node(nodeId2, ANY_DOUBLE, ANY_DOUBLE);

		final double smallLabel = 1.0;
		final double mediumLabel = 10.0;
		final double bigLabel = 100.0;

		final PathNode deceivingPath = new PathNode(nonTargetNode, ANY_INT,
				null);
		
		final PathNode expensiveCorrectPath = new PathNode(targetNode, ANY_INT,
				null);
		
		final PathNode cheapCorrectPath = new PathNode(targetNode, ANY_INT,
				null);

		// Instantiate SUT.
		Queue sut = new Queue();

		// Exercise SUT.
		sut.push(smallLabel, cheapCorrectPath);
		sut.push(mediumLabel, expensiveCorrectPath);
		sut.push(bigLabel, deceivingPath);

		PathNode expected = expensiveCorrectPath;
		PathNode actual = sut.findMaxTo(targetNode);

		// Perform assertions.
		assertEquals(expected, actual);
	}

	@Test
	public void testQueueReplace() {
		
		int someK = 1;
		int anotherK = 2;
		int yetAnotherK = 3;
		
		double someLabel = 1.0;
		double anotherLabel = 2.0;
		
		PathNode path1 = new PathNode(null, someK, null);
		PathNode path2 = new PathNode(null, anotherK, null);
		PathNode path3 = new PathNode(null, yetAnotherK, null);
		
		TreeMap<Double, PathNode> storage = new TreeMap<>();
		
		// Instantiate SUT. Notice the injection constructor chosen.
		Queue queue = new Queue(storage);
		
		// Exercise SUT.
		queue.push(someLabel, path1);
		queue.push(anotherLabel, path2);
		
		queue.replace(path2, path3);
		
		// Perform assertions.
		// Note that we operate on the previously injected storage
		// object.		
		assertTrue(storage.containsValue(path1));
		assertTrue(storage.containsValue(path3));
		assertFalse(storage.containsValue(path2));
	}

}
