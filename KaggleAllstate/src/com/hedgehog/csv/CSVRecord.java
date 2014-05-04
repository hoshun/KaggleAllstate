package com.hedgehog.csv;

import java.util.List;

public class CSVRecord {
	private Schema schema;
	private List<String> values;

	public CSVRecord(Schema schema, List<String> values) {
		this.schema = schema;
		this.values = values;
	}

	public Schema getSchema() {
		return schema;
	}

	public boolean isValueMissing(String name) {
		String value = this.get(name);
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public String get(String name) {
		if (schema.containsName(name)) {
			return values.get(schema.getIndexByName(name));
		} else {
			throw new IllegalArgumentException(name + " does not exist.");
		}
	}	
	
	public int getInt(String name) {
		return Integer.parseInt(this.get(name));
	}

	public double getDouble(String name) {
		return Double.parseDouble(this.get(name));
	}
	
	public List<String> getFields() {
		return values;
	}	

}
