package apps;

import helpers.nodegrp.NodeGroupperType;

import java.sql.ResultSet;
import java.sql.SQLException;

import dal.TopologyType;
import apps.groupanal.GroupExperimentCase;
import apps.groupanal.GroupExperimentValues;
import apps.topanal.TopologyExperimentCase;
import apps.topanal.TopologyExperimentValues;

public class CommonDataAccess {

	private static TopologyExperimentValues topologyResultValuesFromAnyResultSet(
			ResultSet rs, int offset) throws SQLException {
		return new TopologyExperimentValues(rs.getInt(offset + 1),
				(Double) rs.getObject(offset + 2),
				(Double) rs.getObject(offset + 3),
				(Double) rs.getObject(offset + 4),
				(Double) rs.getObject(offset + 5));
	}

	public static TopologyExperimentValues topologyResultValuesFromPartialResultSet(
			ResultSet rs) throws SQLException {
		return topologyResultValuesFromAnyResultSet(rs, 0);
	}

	public static TopologyExperimentValues topologyResultValuesFromFullResultSet(
			ResultSet rs) throws SQLException {
		return topologyResultValuesFromAnyResultSet(rs, 2);
	}

	public static TopologyExperimentCase topologyResultCaseFromResultSet(
			ResultSet rs) throws SQLException {
		return new TopologyExperimentCase(
				TopologyType.valueOf(rs.getString(1)), rs.getInt(2));
	}

	private static GroupExperimentValues groupResultValuesFromAnyResultSet(
			ResultSet rs, int offset) throws SQLException {
		return new GroupExperimentValues(rs.getInt(offset + 1),
				rs.getDouble(offset + 2), rs.getDouble(offset + 3),
				rs.getDouble(offset + 4), rs.getDouble(offset + 5),
				rs.getDouble(offset + 6));
	}

	public static GroupExperimentValues groupResultValuesFromPartialResultSet(
			ResultSet rs) throws SQLException {
		return groupResultValuesFromAnyResultSet(rs, 0);
	}

	public static GroupExperimentValues groupResultValuesFromFullResultSet(
			ResultSet rs) throws SQLException {
		return groupResultValuesFromAnyResultSet(rs, 4);
	}

	public static GroupExperimentCase groupResultCaseFromResultSet(ResultSet rs)
			throws SQLException {
		return new GroupExperimentCase(TopologyType.valueOf(rs.getString(1)),
				rs.getInt(2), rs.getInt(3), NodeGroupperType.valueOf(rs
						.getString(4)));
	}
}