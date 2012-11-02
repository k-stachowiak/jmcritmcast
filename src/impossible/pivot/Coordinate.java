package impossible.pivot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinate {

	private final Map<String, String> values;

	public static Coordinate createFromDefAndRow(List<String> definitions,
			List<String> columns, List<String> row) {

		Map<String, String> newValues = new HashMap<>();

		for (String definition : definitions) {
			int index = columns.indexOf(definition);
			newValues.put(definition, row.get(index));
		}

		return new Coordinate(newValues);
	}

	Coordinate(Map<String, String> values) {
		this.values = values;
	}
	
	public String getValuesString() {
		StringBuilder stringBuilder = new StringBuilder();
		for(Map.Entry<String, String> entry : values.entrySet()) {
			stringBuilder.append(entry.getValue() + " ");
		}
		return stringBuilder.toString();
	}

	public boolean matches(List<String> columns, List<String> row) {
		for (Map.Entry<String, String> value : values.entrySet()) {
			int index = columns.indexOf(value.getKey());
			if (!value.getValue().equals(row.get(index))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		Coordinate other = (Coordinate) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

}
