package impossible.helpers;

public class OspfCostResourceTranslation implements CostResourceTranslation {
	
	private final double baseBandwidth;

	public OspfCostResourceTranslation(double baseBandwidth) {
		this.baseBandwidth = baseBandwidth;
	}

	@Override
	public double costToResource(double cost) {
		return baseBandwidth / cost;
	}

	@Override
	public double resourceToCost(double resource) {
		return baseBandwidth / resource;
	}

}
