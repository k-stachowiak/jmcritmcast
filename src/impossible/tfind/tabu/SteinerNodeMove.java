package impossible.tfind.tabu;

import impossible.model.topology.Node;

import java.util.Map;

public class SteinerNodeMove implements Move {
	private final Map<Node, Boolean> code;

	public SteinerNodeMove(Map<Node, Boolean> code) {
		super();
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SteinerNodeMove other = (SteinerNodeMove) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
}
