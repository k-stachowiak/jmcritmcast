package impossible.stat;

import java.util.List;

public interface StatUtil {

	Interval confidenceInterval(List<Double> sample,
			double significance);
	
	double mean(List<Double> sample); 
}