package impossible.tfind;

import java.util.List;

public interface ConstrainedSteinerTreeFinder extends SteinerTreeFinder {
	void setConstraints(List<Double> constraints);
}
