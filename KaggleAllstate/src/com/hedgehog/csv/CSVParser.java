package com.hedgehog.csv;

import java.util.Arrays;
import java.util.List;

public class CSVParser {

	private Schema schema;
	
	public CSVParser(Schema schema) {
		this.schema = schema;
	}
	
	public CSVParser() {
		// 
	}		
	
	public CSVRecord parse(String line, String sep) {
		List<String> values = Arrays.asList(line.split(sep, -1));
		return new CSVRecord(this.schema, values);		
	}
		
}
