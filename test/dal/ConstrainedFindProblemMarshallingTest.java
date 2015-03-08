package dal;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dal.DTOMarshaller;
import dto.ConstrainedTreeFindProblemDTO;
import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;


public class ConstrainedFindProblemMarshallingTest {

	private static final String TEMP_DIR = "temp";

	@BeforeClass
	public static void beforeClass() {
		File tempDir = new File(TEMP_DIR);

		if (tempDir.exists()) {
			if (tempDir.isFile()) {
				tempDir.delete();
			}
		} else {
			tempDir.mkdir();
		}
	}

	@AfterClass
	public static void afterClass() {
		File tempDir = new File(TEMP_DIR);
		for (File file : tempDir.listFiles()) {
			file.delete();
		}
		tempDir.delete();
	}

	@Test
	public void test() {

		// Constants.
		final String FINDER_NAME = "Finder name";

		final Double ANY_DOUBLE = 7.0;

		final List<Double> ANY_METRICS = Arrays.asList(new Double[] {
				ANY_DOUBLE, ANY_DOUBLE, ANY_DOUBLE });

		final String TEST_FILE_NAME = TEMP_DIR
				+ "/ConstrainedFindProblemMarshallingTest.xml";

		// Elements.
		NodeDTO node1 = new NodeDTO(1, ANY_DOUBLE, ANY_DOUBLE);
		NodeDTO node2 = new NodeDTO(2, ANY_DOUBLE, ANY_DOUBLE);
		NodeDTO node3 = new NodeDTO(3, ANY_DOUBLE, ANY_DOUBLE);
		NodeDTO node4 = new NodeDTO(4, ANY_DOUBLE, ANY_DOUBLE);
		List<NodeDTO> nodes = Arrays.asList(new NodeDTO[] { node1, node2,
				node3, node4 });

		EdgeDTO edge1 = new EdgeDTO(1, 2, ANY_METRICS);
		EdgeDTO edge2 = new EdgeDTO(2, 3, ANY_METRICS);
		EdgeDTO edge3 = new EdgeDTO(3, 4, ANY_METRICS);
		EdgeDTO edge4 = new EdgeDTO(4, 1, ANY_METRICS);
		List<EdgeDTO> edges = Arrays.asList(new EdgeDTO[] { edge1, edge2,
				edge3, edge4 });

		GraphDTO graph = new GraphDTO(nodes, edges);

		List<Integer> group = Arrays.asList(new Integer[] { 1, 2, 3 });

		List<Double> constraints = Arrays.asList(new Double[] { ANY_DOUBLE,
				ANY_DOUBLE });

		// Marshalled object.
		ConstrainedTreeFindProblemDTO expectedProblem = new ConstrainedTreeFindProblemDTO(
				graph, group, constraints, FINDER_NAME);

		// Marshaller.
		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();

		// Write file.
		marshaller.writeToFile(TEST_FILE_NAME, expectedProblem);
		ConstrainedTreeFindProblemDTO actualProblem = marshaller.readFromFile(
				TEST_FILE_NAME, ConstrainedTreeFindProblemDTO.class);

		// Assertions.
		assertEquals(expectedProblem, actualProblem);
	}

}
