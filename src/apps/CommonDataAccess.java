package apps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import apps.alganal.AlgorithmExperimentCase;
import apps.alganal.AlgorithmExperimentValues;
import apps.constranal.ConstraintExperimentCase;
import apps.constranal.ConstraintExperimentValues;
import apps.groupanal.GroupExperimentCase;
import apps.groupanal.GroupExperimentValues;
import apps.topanal.TopologyExperimentCase;
import apps.topanal.TopologyExperimentValues;
import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;
import tfind.TreeFinderType;

public class CommonDataAccess {

	private static TopologyExperimentValues topologyResultValuesFromAnyResultSet(ResultSet rs, int offset)
			throws SQLException {
		return new TopologyExperimentValues((Double) rs.getObject(offset + 1), (Double) rs.getObject(offset + 2),
				(Double) rs.getObject(offset + 3), (Double) rs.getObject(offset + 4));
	}

	public static TopologyExperimentValues topologyResultValuesFromPartialResultSet(ResultSet rs) throws SQLException {
		return topologyResultValuesFromAnyResultSet(rs, 0);
	}

	public static TopologyExperimentValues topologyResultValuesFromFullResultSet(ResultSet rs) throws SQLException {
		return topologyResultValuesFromAnyResultSet(rs, 3);
	}

	public static TopologyExperimentCase topologyResultCaseFromResultSet(ResultSet rs) throws SQLException {
		return new TopologyExperimentCase(TopologyType.valueOf(rs.getString(1)), rs.getInt(2), rs.getInt(3));
	}

	private static GroupExperimentValues groupResultValuesFromAnyResultSet(ResultSet rs, int offset)
			throws SQLException {
		return new GroupExperimentValues(rs.getDouble(offset + 1), rs.getDouble(offset + 2), rs.getDouble(offset + 3),
				rs.getDouble(offset + 4), rs.getDouble(offset + 5));
	}

	public static GroupExperimentValues groupResultValuesFromPartialResultSet(ResultSet rs) throws SQLException {
		return groupResultValuesFromAnyResultSet(rs, 0);
	}

	public static GroupExperimentValues groupResultValuesFromFullResultSet(ResultSet rs) throws SQLException {
		return groupResultValuesFromAnyResultSet(rs, 5);
	}

	public static GroupExperimentCase groupResultCaseFromResultSet(ResultSet rs) throws SQLException {
		return new GroupExperimentCase(TopologyType.valueOf(rs.getString(1)), rs.getInt(2), rs.getInt(3),
				NodeGroupperType.valueOf(rs.getString(4)), rs.getInt(5));
	}

	public static List<Double> costListFromStr(String string) {
		ArrayList<Double> values = new ArrayList<>();
		if (string.isEmpty()) {
			return null;
		} else {
			String[] valueStrings = string.split(";");
			for (String valueString : valueStrings) {
				values.add(Double.parseDouble(valueString));
			}
		}
		return values;
	}

	public static String costListToString(List<Double> costs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < costs.size(); ++i) {
			sb.append(costs.get(i));
			if (i < (costs.size() - 1)) {
				sb.append(';');
			}
		}
		return sb.toString();
	}

	public static AlgorithmExperimentCase algorithmResultCaseFromResultSet(ResultSet rs) throws SQLException {
		return new AlgorithmExperimentCase(
				TopologyType.valueOf(rs.getString(1)),
				rs.getInt(2),
				rs.getInt(3),
				NodeGroupperType.valueOf(rs.getString(4)),
				rs.getInt(5),
				rs.getDouble(6),
				rs.getDouble(7),
				TreeFinderType.valueOf(rs.getString(8)));
	}

	private static AlgorithmExperimentValues algorithmResultValuesFromAnyResultSet(ResultSet rs, int offset)
			throws SQLException {
		return new AlgorithmExperimentValues(costListFromStr(rs.getString(offset + 1)), rs.getInt(offset + 2));
	}

	public static AlgorithmExperimentValues algorithmResultValuesFromPartialResultSet(ResultSet rs)
			throws SQLException {
		return algorithmResultValuesFromAnyResultSet(rs, 0);
	}

	public static AlgorithmExperimentValues algorithmResultValuesFromFullResultSet(ResultSet rs) throws SQLException {
		return algorithmResultValuesFromAnyResultSet(rs, 8);
	}

	private static ConstraintExperimentValues constraintResultValuesFromAnyResultSet(ResultSet rs, int offset)
			throws SQLException {
		return new ConstraintExperimentValues(
				new ConstraintExperimentValues.Range(rs.getDouble(offset + 1), rs.getDouble(offset + 2)),
				new ConstraintExperimentValues.Range(rs.getDouble(offset + 3), rs.getDouble(offset + 4)));
	}

	public static ConstraintExperimentValues constraintResultValuesFromPartialResultSet(ResultSet rs)
			throws SQLException {
		return constraintResultValuesFromAnyResultSet(rs, 0);
	}

	public static ConstraintExperimentValues constraintResultValuesFromFullResultSet(ResultSet rs) throws SQLException {
		return constraintResultValuesFromAnyResultSet(rs, 5);
	}

	public static ConstraintExperimentCase constraintResultCaseFromResultSet(ResultSet rs) throws SQLException {
		return new ConstraintExperimentCase(TopologyType.valueOf(rs.getString(1)), rs.getInt(2), rs.getInt(3),
				NodeGroupperType.valueOf(rs.getString(4)), rs.getInt(5));
	}

}