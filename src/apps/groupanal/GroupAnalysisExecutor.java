package apps.groupanal;

import java.sql.Connection;

public interface GroupAnalysisExecutor {

	public abstract void execute(GroupExperimentCase xc, Connection connection);

}