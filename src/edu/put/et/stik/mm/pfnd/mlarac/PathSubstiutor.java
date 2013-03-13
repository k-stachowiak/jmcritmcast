package edu.put.et.stik.mm.pfnd.mlarac;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Path;

public interface PathSubstiutor {

	List<Path> substitute(Path candidate, List<Path> nonExceedingPaths,
			List<Double> constraints);

}
