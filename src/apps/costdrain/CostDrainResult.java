package apps.costdrain;

import java.util.List;

public class CostDrainResult {
	private final int successCount;
	private final List<Double> firstCosts;

	public CostDrainResult(int successCount, List<Double> firstCosts) {
		super();
		this.successCount = successCount;
		this.firstCosts = firstCosts;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public List<Double> getFirstCosts() {
		return firstCosts;
	}
}
