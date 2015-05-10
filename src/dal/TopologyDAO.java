package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;

public class TopologyDAO {

	private final Connection connection;

	public TopologyDAO(Connection connection) {
		this.connection = connection;
	}

	public GraphDTO select(TopologyType topologyType, int nodesCount, int index)
			throws SQLException {

		String sql = "SELECT id "
				+ "FROM graphs "
				+ "WHERE graph_type = ? AND nodes_count = ? AND graph_index = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setString(1, topologyType.toString());
		preparedStatement.setInt(2, nodesCount);
		preparedStatement.setInt(3, index);

		ResultSet resultSet = preparedStatement.executeQuery();

		if (!resultSet.next()) {
			return null;
		}

		int id = resultSet.getInt(1);
		return new GraphDTO(selectNodes(id), selectEdges(id));
	}

	private List<NodeDTO> selectNodes(int id) throws SQLException {

		String sql = "SELECT id, x, y FROM nodes WHERE graph_id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();

		ArrayList<NodeDTO> result = new ArrayList<>();
		while (resultSet.next()) {
			result.add(new NodeDTO(resultSet.getInt(1), resultSet.getDouble(2),
					resultSet.getDouble(3)));
		}
		return result;
	}

	private List<EdgeDTO> selectEdges(int id) throws SQLException {

		String sql = "SELECT " + "node_from, node_to, "
				+ "metric_0, metric_1, metric_2, metric_3, metric_4 "
				+ "FROM edges where graph_id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();

		ArrayList<EdgeDTO> result = new ArrayList<>();
		while (resultSet.next()) {

			Double metric0 = (Double) resultSet.getObject(3);
			Double metric1 = (Double) resultSet.getObject(4);
			Double metric2 = (Double) resultSet.getObject(5);
			Double metric3 = (Double) resultSet.getObject(6);
			Double metric4 = (Double) resultSet.getObject(7);

			ArrayList<Double> metrics = new ArrayList<>();
			if (metric0 != null) {
				metrics.add(metric0);
			}
			if (metric1 != null) {
				metrics.add(metric1);
			}
			if (metric2 != null) {
				metrics.add(metric2);
			}
			if (metric3 != null) {
				metrics.add(metric3);
			}
			if (metric4 != null) {
				metrics.add(metric4);
			}

			result.add(new EdgeDTO(resultSet.getInt(1), resultSet.getInt(2),
					metrics));
		}
		return result;
	}

	public void insert(GraphDTO graphDTO, TopologyType topologyType,
			int nodesCount, int index) throws SQLException {

		String sql = "INSERT "
				+ "INTO graphs (graph_type, nodes_count, graph_index) "
				+ "VALUES (?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);

		preparedStatement.setString(1, topologyType.toString());
		preparedStatement.setInt(2, nodesCount);
		preparedStatement.setInt(3, index);

		int affectedRows = preparedStatement.executeUpdate();

		if (affectedRows == 0) {
			throw new SQLException("Creating user failed, no rows affected.");
		}

		int graphId = -1;
		try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				graphId = generatedKeys.getInt(1);
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");
			}
		}

		for (NodeDTO nodeDTO : graphDTO.getNodes()) {
			insert(nodeDTO, graphId);
		}

		for (EdgeDTO edgeDTO : graphDTO.getEdges()) {
			insert(edgeDTO, graphId);
		}
	}

	private void insert(EdgeDTO edge, int graphId) throws SQLException {
		String sql = "INSERT INTO edges "
				+ "(graph_id, node_from, node_to, metric_0, metric_1, metric_2, metric_3, metric_4)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setInt(1, graphId);
		preparedStatement.setInt(2, edge.getNodeFrom());
		preparedStatement.setInt(3, edge.getNodeTo());

		int i = 0;
		while (i < 5 && i < edge.getMetrics().size()) {
			preparedStatement.setDouble(i + 4, edge.getMetrics().get(i++));
		}
		while (i < 5) {
			preparedStatement.setNull(i + 4, Types.DOUBLE);
			i++;
		}

		int affectedRows = preparedStatement.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Creating user failed, no rows affected.");
		}
	}

	private void insert(NodeDTO node, int graphId) throws SQLException {
		String sql = "INSERT " + "INTO nodes (graph_id, id, x, y) "
				+ "VALUES (?, ?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setInt(1, graphId);
		preparedStatement.setInt(2, node.getId());
		preparedStatement.setDouble(3, node.getX());
		preparedStatement.setDouble(4, node.getY());

		int affectedRows = preparedStatement.executeUpdate();

		if (affectedRows == 0) {
			throw new SQLException("Creating user failed, no rows affected.");
		}
	}

}
