package dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dto.GraphDTO;

public class MultiBriteGraphStreamer implements InputGraphStreamer {

	private final int graphsLimit;
	private final List<File> graphsFiles;
	private int lastIndex;
	private final GraphReader graphReader;

	public MultiBriteGraphStreamer(String path, TopologyType topologyType,
			int nodesCount, int graphsLimit) {

		this.graphsLimit = graphsLimit;
		this.graphsFiles = getMatchingFiles(path, topologyType, nodesCount);
		this.lastIndex = 0;

		if (topologyType == TopologyType.Inet) {
			this.graphReader = new InetGraphReader();
		} else {
			this.graphReader = new BriteGraphReader();
		}
	}

	@Override
	public boolean hasNext() {
		return lastIndex < graphsLimit;
	}

	@Override
	public GraphDTO getNext() {

		File file = graphsFiles.get(lastIndex++);
		try {
			Scanner scanner = new Scanner(file);
			GraphDTO result = graphReader.readGraph(scanner);
			return result;

		} catch (FileNotFoundException e) {
			System.err.printf("Failed reading file %s", file.getPath());
			return null;

		}
	}

	private static List<File> getMatchingFiles(String path, TopologyType type,
			int nodesCount) {

		String topString = type.toString();
		String countString = Integer.toString(nodesCount);

		ArrayList<File> result = new ArrayList<>();

		for (File f : new File(path).listFiles()) {
			if (f.isDirectory()) {
				continue;
			}
			String fileName = f.getName();
			if (fileName.startsWith(topString + "." + countString + ".")) {
				result.add(f);
			}
		}

		return result;
	}

}
