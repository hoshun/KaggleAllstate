package com.hedgehog.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.hedgehog.csv.Schema;
import com.hedgehog.csv.CSVParser;
import com.hedgehog.csv.CSVRecord;

public class CSVFileReader {

	public static void main(String[] args) {
		File trainFile = new File(
				"/Users/hoshun/project/kaggle-allstate/train.csv");
		List<CSVRecord> records = read(trainFile, ",", true);
		System.out.println(records.size());

	}

	public static List<CSVRecord> read(File file, String sep, boolean hasHeader) {
		List<CSVRecord> records = Lists.newArrayList();
		CSVParser parser = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			boolean firstLine = true;
			String line;
			while ((line = reader.readLine()) != null) {
				if (firstLine && hasHeader) {
					Schema schema = new Schema(Arrays.asList(line
							.split(sep, -1)));
					parser = new CSVParser(schema);
					firstLine = false;
				} else {
					records.add(parser.parse(line, sep));
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return records;
	}
}
