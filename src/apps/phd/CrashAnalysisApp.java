package apps.phd;

import java.io.File;

import dal.DTOMarshaller;
import dto.ConstrainedTreeFindProblemDTO;

public class CrashAnalysisApp {

	public static void main(String[] args) {

		File problemFile = new File("debug_data/current_problem.xml");
		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();
		ConstrainedTreeFindProblemDTO problem = marshaller.readFromFile(
				problemFile.getPath(), ConstrainedTreeFindProblemDTO.class);
		new ConstrainedTreeFindProblemSolver().solve(problem);
	}
}
