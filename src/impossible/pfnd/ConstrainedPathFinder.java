package impossible.pfnd;

import java.util.List;

public interface ConstrainedPathFinder extends PathFinder {
	void setConstraints(List<Double> constraints);
}
