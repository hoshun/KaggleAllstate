package com.hedgehog.kaggle.allstate;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.hedgehog.csv.CSVRecord;
import com.hedgehog.io.CSVFileReader;

/**
 * Transform the data into one customer point per line, transforming time series
 * data into features.
 * 
 * @author hoshun
 * 
 */
public class Transformer {

	public static void main(String[] args) {

	}

	public static List<CSVRecord> transform(List<QuoteRecord> quoteRecords) {
		List<CSVRecord> outputRecords = Lists.newArrayList();
		String curCustomerID = null;
		List<QuoteRecord> customerRecords = Lists.newArrayList();
		for (QuoteRecord qRecord : quoteRecords) {
			String thisCustomerID = qRecord.getCustomerID();
			// The first quote record.
			if (curCustomerID == null) {
				curCustomerID = qRecord.getCustomerID();
				customerRecords.add(qRecord);
			} else {
				// change customer
				if (!curCustomerID.equals(qRecord.getCustomerID())) {
					CSVRecord outputRecord = transformRecord(customerRecords);
					outputRecords.add(outputRecord);
					// reset the customer records;
					customerRecords = Lists.newArrayList();
					customerRecords.add(qRecord);
				} else {
					customerRecords.add(qRecord);
				}
			}
		}

		return outputRecords;
	}

	/**
	 * Transform a sequence of customer records to a single customer record with
	 * augmented features.
	 * 
	 * @param quoteRecords
	 * @return
	 */
	public static CSVRecord transformRecord(List<QuoteRecord> quoteRecords) {
		return null;
	}
}
