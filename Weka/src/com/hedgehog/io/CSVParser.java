package com.hedgehog.io;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

public class CSVParser {
	
	private static final String SEP = ",";
	private Schema schema;
	
	public CSVParser(Schema schema) {
		Preconditions.checkNotNull(schema);		
		this.schema = schema;
	}

	public CSVRecord parse(String line) {
		List<String> values = Arrays.asList(line.split(SEP, -1));
		Preconditions.checkArgument(schema.size() == values.size());
		return new CSVRecord(this.schema, values);
	}
}
