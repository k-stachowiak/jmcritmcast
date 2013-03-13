package edu.put.et.stik.mm.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "problem")
public class DrainageProblemDTO {

	@XmlElement(name = "num-nodes")
	private final int numNodes;

	@XmlElement(name = "num-criteria")
	private final int numCriteria;

	@XmlElement(name = "num-participants")
	private final int numParticipants;

	@XmlElement(name = "num-graphs")
	private final int numGraphs;

	@XmlElement(name = "finder-name")
	private final String finderName;

	public DrainageProblemDTO(int numNodes, int numCriteria,
			int numParticipants, int numGraphs, String finderName) {

		this.numNodes = numNodes;
		this.numCriteria = numCriteria;
		this.numParticipants = numParticipants;
		this.numGraphs = numGraphs;
		this.finderName = finderName;
	}

	public final int getNumNodes() {
		return numNodes;
	}

	public final int getNumCriteria() {
		return numCriteria;
	}

	public final int getNumParticipants() {
		return numParticipants;
	}

	public final int getNumGraphs() {
		return numGraphs;
	}

	public final String getFinderName() {
		return finderName;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((finderName == null) ? 0 : finderName.hashCode());
		result = prime * result + numCriteria;
		result = prime * result + numGraphs;
		result = prime * result + numNodes;
		result = prime * result + numParticipants;
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DrainageProblemDTO other = (DrainageProblemDTO) obj;
		if (finderName == null) {
			if (other.finderName != null)
				return false;
		} else if (!finderName.equals(other.finderName))
			return false;
		if (numCriteria != other.numCriteria)
			return false;
		if (numGraphs != other.numGraphs)
			return false;
		if (numNodes != other.numNodes)
			return false;
		if (numParticipants != other.numParticipants)
			return false;
		return true;
	}
}
