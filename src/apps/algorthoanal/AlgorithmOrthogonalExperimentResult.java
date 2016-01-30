package apps.algorthoanal;

import dto.GroupDTO;
import dto.SubgraphDTO;

public class AlgorithmOrthogonalExperimentResult {
	private Integer id;
	private final int caseId;
	private final SubgraphDTO treeDTO;
	private final GroupDTO groupDTO;
	private final double seconds;

	public AlgorithmOrthogonalExperimentResult(int caseId, SubgraphDTO treeDTO, GroupDTO groupDTO,
			double seconds) {
		super();
		this.caseId = caseId;
		this.treeDTO = treeDTO;
		this.groupDTO = groupDTO;
		this.seconds = seconds;
		id = null;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getCaseId() {
		return caseId;
	}

	public SubgraphDTO getTreeDTO() {
		return treeDTO;
	}

	public GroupDTO getGroupDTO() {
		return groupDTO;
	}

	public double getSeconds() {
		return seconds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + caseId;
		result = prime * result + ((groupDTO == null) ? 0 : groupDTO.hashCode());
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(seconds);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((treeDTO == null) ? 0 : treeDTO.hashCode());
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
		AlgorithmOrthogonalExperimentResult other = (AlgorithmOrthogonalExperimentResult) obj;
		if (caseId != other.caseId)
			return false;
		if (groupDTO == null) {
			if (other.groupDTO != null)
				return false;
		} else if (!groupDTO.equals(other.groupDTO))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(seconds) != Double.doubleToLongBits(other.seconds))
			return false;
		if (treeDTO == null) {
			if (other.treeDTO != null)
				return false;
		} else if (!treeDTO.equals(other.treeDTO))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlgorithmOrthogonalExperimentResult [id=" + id + ", caseId=" + caseId + ", treeDTO=" + treeDTO
				+ ", groupDTO=" + groupDTO + ", seconds=" + seconds + "]";
	}

}
