package impossible.helpers.algqry;

public class NodeInfo {

	private final String id;
	private final String label;

	public NodeInfo(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
