package impossible.pfnd.mlarac;

import impossible.model.Path;

import java.util.List;

public interface PathSubstiutor {

	List<Path> substitute(Path candidate, List<Path> nonExceedingPaths,
			List<Double> constraints);

}
