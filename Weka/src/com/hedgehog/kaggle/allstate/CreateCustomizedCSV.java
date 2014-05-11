package com.hedgehog.kaggle.allstate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.hedgehog.io.CSVRecord;
import com.hedgehog.io.FileSystemUtil;

public class CreateCustomizedCSV {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {		
		

		File inputFile = new File("/Users/jackie/kaggle/AllState/typed_train.csv");
		List<CSVRecord> origRecords = FileSystemUtil.readCSVFile(inputFile);				
		System.out.println("Finish read.");		
		
		DataTransformer transformer = new DataTransformer();				
		List<CSVRecord> transformedRecords = transformer.transform(origRecords);		
		System.out.println("Finish transform");
		
		File outputFile = new File("/Users/jackie/kaggle/AllState/transformed_train.csv");				
		FileSystemUtil.writeFile(transformedRecords, outputFile);
		System.out.println("Finish write");
		
		File inputTestFile = new File("/Users/jackie/kaggle/AllState/typed_test.csv");
		List<CSVRecord> origTestRecords = FileSystemUtil.readCSVFile(inputTestFile);				
		System.out.println("Finish read.");
		
		List<CSVRecord> transformedTestRecords = transformer.transformTest(origTestRecords);		
		System.out.println("Finish transform");
		
		File outputTestFile = new File("/Users/jackie/kaggle/AllState/transformed_test.csv");				
		FileSystemUtil.writeFile(transformedTestRecords, outputTestFile);
		System.out.println("Finish write");	
	}

}
