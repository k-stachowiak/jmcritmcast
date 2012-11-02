package impossible.pivot.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPage {

	private final List<String> columns;
	private final Map<String, Map<String, Double>> values;

	public ReportPage() {
		this.columns = new ArrayList<>();
		this.values = new HashMap<>();
		
		throw new RuntimeException("Make sure the columns are in the same order!");
	}

	public void addColumnIfNotExists(String column) {
		if (!columns.contains(column)) {
			columns.add(column);
		}
	}

	public void registerValue(String rowString, String columnString,
			double value) {

		values.put(rowString, new HashMap<String, Double>());
		values.get(rowString).put(columnString, value);
	}
}
