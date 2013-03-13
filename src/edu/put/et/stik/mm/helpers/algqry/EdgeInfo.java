package edu.put.et.stik.mm.helpers.algqry;

public class EdgeInfo {
	
	private final String fromId;
	private final String toId;

	public EdgeInfo(String fromId, String toId) {
		this.fromId = fromId;
		this.toId = toId;
	}

	public String getFromId() {
		return fromId;
	}

	public String getToId() {
		return toId;
	}

}
