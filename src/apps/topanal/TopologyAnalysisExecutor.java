package apps.topanal;

import java.sql.Connection;

public interface TopologyAnalysisExecutor {

	public abstract void execute(TopologyExperimentCase xc,
			Connection connection);

}