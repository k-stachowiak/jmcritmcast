package impossible.pivot;

import impossible.pivot.aggregators.AggrName;
import impossible.pivot.aggregators.Aggregator;
import impossible.pivot.aggregators.AggregatorFactory;
import impossible.pivot.report.ReportPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	private final AggregatorFactory aggregatorFactory;
	private final List<String> pageFields;
	private final List<String> columnFields;
	private final List<String> rowFields;

	private final Map<String, String> aggregatorDefinitions;

	private Map<Position, Map<String, Aggregator>> aggregators;

	Table(AggregatorFactory aggregatorFactory, List<String> pageFields,
			List<String> columnFields, List<String> rowFields,
			Map<String, String> aggregatorDefinitions) {
		this.aggregatorFactory = aggregatorFactory;
		this.pageFields = pageFields;
		this.columnFields = columnFields;
		this.rowFields = rowFields;
		this.aggregatorDefinitions = aggregatorDefinitions;
	}

	public void consumeRow(List<String> columns, List<String> row) {
		Position position = determineRowsPosition(columns, row);
		initializeAggregatorsIfNeeded(position);
		Map<String, Aggregator> aggregatorMap = aggregators.get(position);
		for (Map.Entry<String, Aggregator> entry : aggregatorMap.entrySet()) {
			int index = columns.indexOf(entry.getKey());
			entry.getValue().put(Double.parseDouble(row.get(index)));
		}
	}

	public Map<String, ReportPage> getReport() {

		Map<String, ReportPage> result = new HashMap<>();

		for (Map.Entry<Position, Map<String, Aggregator>> entry : aggregators
				.entrySet()) {

			Position position = entry.getKey();

			Coordinate page = position.getPage();
			Coordinate row = position.getRow();
			Coordinate column = position.getColumn();

			// Determine page.
			String pageString = page.getValuesString();
			if (!result.containsKey(pageString)) {
				result.put(pageString, new ReportPage());
			}
			ReportPage reportPage = result.get(pageString);

			for (Aggregator aggregator : entry.getValue().values()) {

				// Determine column.
				String columnString = String.format("%s(%s)",
						aggregator.getName(), column.getValuesString());
				reportPage.addColumnIfNotExists(columnString);

				// Determine row.
				String rowString = row.getValuesString();

				// Get the aggregated value.
				double value = aggregator.get();

				// Register value.
				reportPage.registerValue(rowString, columnString, value);
			}
		}

		return result;
	}

	private void initializeAggregatorsIfNeeded(Position position) {

		if (aggregators.containsKey(position)) {
			return;
		}

		Map<String, Aggregator> newAggregators = new HashMap<>();
		for (Map.Entry<String, String> entry : aggregatorDefinitions.entrySet()) {
			String key = entry.getKey();
			Aggregator aggregator = aggregatorFactory.createFromName(AggrName
					.fromString(entry.getValue()));
			newAggregators.put(key, aggregator);
		}

		aggregators.put(position, newAggregators);
	}

	private Position determineRowsPosition(List<String> columns,
			List<String> row) {

		Coordinate p = Coordinate.createFromDefAndRow(pageFields, columns, row);
		Coordinate c = Coordinate.createFromDefAndRow(columnFields, columns,
				row);
		Coordinate r = Coordinate.createFromDefAndRow(rowFields, columns, row);

		return new Position(p, r, c);
	}
}
