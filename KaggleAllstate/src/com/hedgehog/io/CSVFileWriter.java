package com.hedgehog.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Joiner;
import com.hedgehog.csv.Schema;
import com.hedgehog.csv.CSVRecord;

public class CSVFileWriter {

	public static void write(File file, List<CSVRecord> records, String sep) {
		Joiner joiner = Joiner.on(sep);

		PrintWriter writer = null;
		try {

			writer = new PrintWriter(file);
			if (records != null && !records.isEmpty()) {

				// Write header
				Schema schema = records.get(0).getSchema();
				if (schema != null) {
					writer.println(joiner.join(schema.getNames()));
				}

				// Write content
				for(CSVRecord record : records) {
					writer.println(joiner.join(record.getFields()));
				}
				
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
