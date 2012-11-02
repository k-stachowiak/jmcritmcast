package impossible.pivot.aggregators;

public enum AggrName {
	CONF_INT("confidence"),
	COUNT("count"),
	MEAN("mean"),
	STDEV("stdev"),
	SUM("sum");
	
	private String name;
	
	AggrName(String name) {
		this.name = name;
	}
	
	public static AggrName fromString(String name) {
		for(AggrName aggrName : values()) {
			if(aggrName.name.equals(name)) {
				return aggrName;
			}
		}
		throw new RuntimeException("Unhandled enum name requested");
	}
	
	@Override
	public String toString() {
		return name;
	}
}
