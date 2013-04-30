package pfnd.mlarac;


import java.util.List;

import model.topology.Path;


public interface PathSubstiutor {

	List<Path> substitute(Path candidate, List<Path> nonExceedingPaths,
			List<Double> constraints);

}
