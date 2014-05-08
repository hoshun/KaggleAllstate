
package com.hedgehog.io;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class CSVRecord {

	private Schema schema;
	private List<String> values;

	public CSVRecord(Schema schema, List<String> values) {
		this.schema = schema;
		this.values = values;
	}

	/**
	 * Construct an empty {@link CSVRecord}.
	 * @param schema
	 */
	public CSVRecord(Schema schema) {
		this.schema = schema;
		values = Lists.newArrayList();
		for(int i=0; i < schema.size(); i++) {
			values.add("");
		}
	}
	
	public Schema getSchema() {
		return this.schema;
	}
	
	public String get(Field field) {		
		return get(field.getName());
	}
		
	
	public String get(String name) {
		Preconditions.checkArgument(schema.containsName(name));
		return values.get(schema.getIndex(name));
	}

	public void set(Field field, String value) {
		this.set(field.getName(), value);
	}
	
	public void set(String name, String value) {
		Preconditions.checkArgument(schema.containsName(name));
		values.set(schema.getIndex(name), value);
	}

	public Integer getInteger(String name) {
		if (get(name).isEmpty()) {
			return null;
		} else {
			return Integer.parseInt(get(name));
		}
	}

	public Double getDouble(String name) {
		if (get(name).isEmpty()) {
			return null;
		} else {
			return Double.parseDouble(get(name));
		}
	}
	
	public String toString() {
		return Joiner.on(",").join(values);
	}
}
