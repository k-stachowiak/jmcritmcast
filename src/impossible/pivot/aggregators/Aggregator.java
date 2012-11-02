package impossible.pivot.aggregators;

public interface Aggregator {

	void put(double value);
	
	double get();
	
	String getName();
	
}
