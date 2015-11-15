package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;
import helpers.ExtendedStatistics;

public class TopologyDAO {

	private static final Logger logger = LogManager.getLogger(TopologyDAO.class);

	private final Connection connection;

	public TopologyDAO(Connection connection) {
		this.connection = connection;
	}

	public interface MetricGenerator {
		double getNext();
	}

	public class EdgeStatistic {
		private final ExtendedStatistics metric0;
		private final ExtendedStatistics metric1;
		private final ExtendedStatistics metric2;
		private final ExtendedStatistics metric3;
		private final ExtendedStatistics metric4;

		public EdgeStatistic(ExtendedStatistics metric0, ExtendedStatistics metric1, ExtendedStatistics metric2,
				ExtendedStatistics metric3, ExtendedStatistics metric4) {
			this.metric0 = metric0;
			this.metric1 = metric1;
			this.metric2 = metric2;
			this.metric3 = metric3;
			this.metric4 = metric4;
		}

		public ExtendedStatistics getMetric0() {
			return metric0;
		}

		public ExtendedStatistics getMetric1() {
			return metric1;
		}

		public ExtendedStatistics getMetric2() {
			return metric2;
		}

		public ExtendedStatistics getMetric3() {
			return metric3;
		}

		public ExtendedStatistics getMetric4() {
			return metric4;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((metric0 == null) ? 0 : metric0.hashCode());
			result = prime * result + ((metric1 == null) ? 0 : metric1.hashCode());
			result = prime * result + ((metric2 == null) ? 0 : metric2.hashCode());
			result = prime * result + ((metric3 == null) ? 0 : metric3.hashCode());
			result = prime * result + ((metric4 == null) ? 0 : metric4.hashCode());
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
			EdgeStatistic other = (EdgeStatistic) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (metric0 == null) {
				if (other.metric0 != null)
					return false;
			} else if (!metric0.equals(other.metric0))
				return false;
			if (metric1 == null) {
				if (other.metric1 != null)
					return false;
			} else if (!metric1.equals(other.metric1))
				return false;
			if (metric2 == null) {
				if (other.metric2 != null)
					return false;
			} else if (!metric2.equals(other.metric2))
				return false;
			if (metric3 == null) {
				if (other.metric3 != null)
					return false;
			} else if (!metric3.equals(other.metric3))
				return false;
			if (metric4 == null) {
				if (other.metric4 != null)
					return false;
			} else if (!metric4.equals(other.metric4))
				return false;
			return true;
		}

		private TopologyDAO getOuterType() {
			return TopologyDAO.this;
		}

	}

	public EdgeStatistic selectStatistics(TopologyType topologyType, int nodesCount, double bucketWidth)
			throws SQLException {

		String sql = "select edges.metric_0, edges.metric_1, edges.metric_2, edges.metric_3, edges.metric_4 "
				+ "from edges join graphs on edges.graph_id = graphs.id "
				+ "where graphs.graph_type = ? and graphs.nodes_count = ?";

		final PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setString(1, topologyType.toString());
		preparedStatement.setInt(2, nodesCount);

		logger.trace("About to execute statement: {}", preparedStatement.toString());
		final ResultSet resultSet = preparedStatement.executeQuery();

		final ExtendedStatistics metric0 = new ExtendedStatistics(bucketWidth);
		final ExtendedStatistics metric1 = new ExtendedStatistics(bucketWidth);
		final ExtendedStatistics metric2 = new ExtendedStatistics(bucketWidth);
		final ExtendedStatistics metric3 = new ExtendedStatistics(bucketWidth);
		final ExtendedStatistics metric4 = new ExtendedStatistics(bucketWidth);

		while (resultSet.next()) {
			metric0.put(resultSet.getDouble(1));
			metric1.put(resultSet.getDouble(2));
			metric2.put(resultSet.getDouble(3));
			metric3.put(resultSet.getDouble(4));
			metric4.put(resultSet.getDouble(5));
		}

		return new EdgeStatistic(metric0, metric1, metric2, metric3, metric4);
	}

	public boolean needToUpdateMetric(TopologyType topologyType, int nodesCount, int index) throws SQLException {

		if (index == 0) {
			logger.fatal("You don't want to change this metric at all!!!");
			return false;
		} else if (index == 1 && topologyType != TopologyType.Inet) {
			logger.fatal("You don't want to change this metric for this topology type!!!");
			return false;
		} else if (index > 4) {
			logger.fatal("This metric doesn't exist!!!");
			return false;
		}

		String sql = "SELECT EXISTS (SELECT * FROM graphs JOIN edges ON edges.graph_id = graphs.id "
				+ "WHERE graphs.graph_type = ? AND graphs.nodes_count = ? AND edges.metric_" + Integer.toString(index)
				+ " IS NULL)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, topologyType.toString());
			preparedStatement.setInt(2, nodesCount);
			
			logger.trace("About to execute statement: {}", preparedStatement.toString());
			final ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			return resultSet.getBoolean(1);
			
		}
	}

	public void updateMetric(TopologyType topologyType, int nodesCount, int index, MetricGenerator metricGenerator)
			throws SQLException {

		if (index == 0) {
			logger.fatal("You don't want to change this metric at all!!!");
			return;
		} else if (index == 1 && topologyType != TopologyType.Inet) {
			logger.fatal("You don't want to change this metric for this topology type!!!");
			return;
		} else if (index > 4) {
			logger.fatal("This metric doesn't exist!!!");
			return;
		}

		String sql = "select edges.graph_id, edges.node_from, edges.node_to "
				+ "from edges join graphs on edges.graph_id = graphs.id "
				+ "where graphs.graph_type = ? and graphs.nodes_count = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {

			preparedStatement.setString(1, topologyType.toString());
			preparedStatement.setInt(2, nodesCount);

			logger.trace("About to execute statement: {}", preparedStatement.toString());
			final ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				int graphId = resultSet.getInt(1);
				int nodeFrom = resultSet.getInt(2);
				int nodeTo = resultSet.getInt(3);

				double metric = metricGenerator.getNext();

				String innerSql = "UPDATE edges SET metric_" + Integer.toString(index) + " = ? "
						+ "WHERE graph_id = ? AND node_from = ? AND node_to = ?";

				try (PreparedStatement innerStatement = connection.prepareStatement(innerSql,
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

					innerStatement.setDouble(1, metric);
					innerStatement.setInt(2, graphId);
					innerStatement.setInt(3, nodeFrom);
					innerStatement.setInt(4, nodeTo);

					logger.trace("About to execute statement: {}", innerStatement.toString());
					innerStatement.executeUpdate();
				}

			}

		}
	}

	public GraphDTO select(TopologyType topologyType, int nodesCount, int index) throws SQLException {

		String sql = "SELECT id " + "FROM graphs " + "WHERE graph_type = ? AND nodes_count = ? AND graph_index = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setString(1, topologyType.toString());
		preparedStatement.setInt(2, nodesCount);
		preparedStatement.setInt(3, index);

		logger.trace("About to execute statement: {}", preparedStatement.toString());
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
		logger.trace("About to execute statement: {}", preparedStatement.toString());
		ResultSet resultSet = preparedStatement.executeQuery();

		ArrayList<NodeDTO> result = new ArrayList<>();
		while (resultSet.next()) {
			result.add(new NodeDTO(resultSet.getInt(1), resultSet.getDouble(2), resultSet.getDouble(3)));
		}
		return result;
	}

	private List<EdgeDTO> selectEdges(int id) throws SQLException {

		String sql = "SELECT " + "node_from, node_to, " + "metric_0, metric_1, metric_2, metric_3, metric_4 "
				+ "FROM edges where graph_id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		logger.trace("About to execute statement: {}", preparedStatement.toString());
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

			result.add(new EdgeDTO(resultSet.getInt(1), resultSet.getInt(2), metrics));
		}
		return result;
	}

	public void insert(GraphDTO graphDTO, TopologyType topologyType, int nodesCount, int index) throws SQLException {

		String sql = "INSERT " + "INTO graphs (graph_type, nodes_count, graph_index) " + "VALUES (?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		preparedStatement.setString(1, topologyType.toString());
		preparedStatement.setInt(2, nodesCount);
		preparedStatement.setInt(3, index);

		logger.trace("About to execute statement: {}", preparedStatement.toString());
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

		logger.trace("About to execute statement: {}", preparedStatement.toString());
		int affectedRows = preparedStatement.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException("Creating user failed, no rows affected.");
		}
	}

	private void insert(NodeDTO node, int graphId) throws SQLException {
		String sql = "INSERT " + "INTO nodes (graph_id, id, x, y) " + "VALUES (?, ?, ?, ?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		preparedStatement.setInt(1, graphId);
		preparedStatement.setInt(2, node.getId());
		preparedStatement.setDouble(3, node.getX());
		preparedStatement.setDouble(4, node.getY());

		logger.trace("About to execute statement: {}", preparedStatement.toString());
		int affectedRows = preparedStatement.executeUpdate();

		if (affectedRows == 0) {
			throw new SQLException("Creating user failed, no rows affected.");
		}
	}

}
