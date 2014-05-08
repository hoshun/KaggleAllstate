package com.hedgehog.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class FileSystemUtil {

	private static final String COMMA = ",";

	public static Instances loadInstancesFromCSV(File inputFile) throws Exception {
		Schema schema = FileSystemUtil.readFileSchema(inputFile);
		String option = constructCSVLoaderOptions(schema);
		System.out.println(option);

		CSVLoader loader = new CSVLoader();
		loader.setOptions(option.split("\\s+"));
		loader.setSource(inputFile);
		Instances data = loader.getDataSet();

		return data;
	}

	private static String constructCSVLoaderOptions(Schema schema) {
		List<Integer> nominalIndice = Lists.newArrayList();
		List<Integer> stringIndice = Lists.newArrayList();
		for (Field field : schema) {
			switch (field.getType()) {
			case NOMINAL:
				nominalIndice.add(schema.getIndex(field) + 1);
				break;
			case STRING:
				stringIndice.add(schema.getIndex(field) + 1);
				break;
			default:
				// do nothing
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("-N ").append(Joiner.on(",").join(nominalIndice)).append(" ");
		sb.append("-S ").append(Joiner.on(",").join(stringIndice));

		return sb.toString();
	}

	public static Schema readFileSchema(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		Schema schema = null;
		if (line != null) {

			List<String> headers = Arrays.asList(line.split(COMMA));
			List<Field> fields = Lists.newArrayList();
			for (String header : headers) {
				fields.add(Field.parseField(header));
			}
			schema = new Schema(fields);

		}
		reader.close();
		return schema;
	}

	public static List<CSVRecord> readCSVFile(File file) throws IOException {
		List<CSVRecord> records = Lists.newArrayList();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		CSVParser parser = null;
		boolean isFirst = true;
		while (line != null) {
			if (isFirst) {
				List<String> headers = Arrays.asList(line.split(COMMA));
				List<Field> fields = Lists.newArrayList();
				for (String header : headers) {
					fields.add(Field.parseField(header));
				}
				Schema schema = new Schema(fields);
				parser = new CSVParser(schema);
				isFirst = false;
			} else {
				CSVRecord record = parser.parse(line);
				records.add(record);
			}
			line = reader.readLine();
		}
		reader.close();

		return records;
	}

	public static void writeFile(List<CSVRecord> records, File file) throws IOException {
		Preconditions.checkNotNull(records);
		Preconditions.checkArgument(!records.isEmpty());

		PrintWriter writer = new PrintWriter(file);
		// Write header.
		writer.println(records.get(0).getSchema());

		// Write records.
		for (CSVRecord record : records) {
			writer.println(record.toString());
		}

		writer.close();
	}

}
