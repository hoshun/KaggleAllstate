package com.hedgehog.kaggle.allstate;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hedgehog.io.CSVRecord;
import com.hedgehog.io.Field;
import com.hedgehog.io.Field.Type;
import com.hedgehog.io.Schema;

public class DataTransformer {
	private static final String MISSING_VALUE = "?";
	private final int EXPECTED_RECORDS_SIZE = 100000;
	
	private static final List<Field> newFields;
	private Schema outputSchema;
	private Schema origSchema;
	
	static {
		newFields = Lists.newArrayList();
		newFields.add(new Field("finalOptions", Type.NOMINAL));
	}

	public DataTransformer() {
		// 
	}
	
	public List<CSVRecord> transformCustomerRecords(List<CSVRecord> customerRecords) {
		if (outputSchema == null) {
			// Construct output schema
			Schema origSchema = customerRecords.get(0).getSchema();
			List<Field> outputFields = Lists.newArrayList();
			for (Field field : origSchema) {
				outputFields.add(field);
			}
			outputFields.addAll(newFields);
			outputSchema = new Schema(outputFields);
		}

		if (origSchema == null) {
			origSchema = customerRecords.get(0).getSchema();
		}

		List<CSVRecord> outputRecords = Lists.newArrayList();
		CSVRecord purchaseRecord = customerRecords.get(customerRecords.size() - 1);
		Preconditions.checkArgument(purchaseRecord.getInteger("record_type") == 1);

		for (CSVRecord origRecord : customerRecords) {
			CSVRecord outputRecord = new CSVRecord(outputSchema);
			outputRecords.add(outputRecord);

			// Populate the output records with original fields.
			for (Field origField : origSchema) {
				outputRecord.set(origField.getName(), origRecord.get(origField.getName()));
			}

			// Populate the output records with new fields.
			Plan.Builder builder = new Plan.Builder();
			builder.set("A:nominal", purchaseRecord.getInteger("A"));
			builder.set("B:nominal", purchaseRecord.getInteger("B"));
			builder.set("C:nominal", purchaseRecord.getInteger("C"));
			builder.set("D:nominal", purchaseRecord.getInteger("D"));
			builder.set("E:nominal", purchaseRecord.getInteger("E"));
			builder.set("F:nominal", purchaseRecord.getInteger("F"));
			builder.set("G:nominal", purchaseRecord.getInteger("G"));
			Plan plan = builder.build();
			outputRecord.set("finalOptions", String.valueOf(plan.encodeToDouble()));
			
			unifyMissingValues(outputRecord, MISSING_VALUE);
		}
		
		return outputRecords;
	}
	
	
	
	public List<CSVRecord> transformTestCustomerRecords(List<CSVRecord> customerRecords) {
		if (outputSchema == null) {
			// Construct output schema
			Schema origSchema = customerRecords.get(0).getSchema();
			List<Field> outputFields = Lists.newArrayList();
			for (Field field : origSchema) {
				outputFields.add(field);
			}
			outputFields.addAll(newFields);
			outputSchema = new Schema(outputFields);
		}

		if (origSchema == null) {
			origSchema = customerRecords.get(0).getSchema();
		}

		List<CSVRecord> outputRecords = Lists.newArrayList();

		for (CSVRecord origRecord : customerRecords) {
			CSVRecord outputRecord = new CSVRecord(outputSchema);
			outputRecords.add(outputRecord);

			// Populate the output records with original fields.
			for (Field origField : origSchema) {
				outputRecord.set(origField.getName(), origRecord.get(origField.getName()));
			}

			// Populate the output records with new fields.
			Plan.Builder builder = new Plan.Builder();
			builder.set("finalA:nominal", 0);
			builder.set("finalB:nominal", 0);
			builder.set("finalC:nominal", 0);
			builder.set("finalD:nominal", 0);
			builder.set("finalE:nominal", 0);
			builder.set("finalF:nominal", 0);
			builder.set("finalG:nominal", 0);
			Plan plan = builder.build();
			outputRecord.set("finalOptions", String.valueOf(plan.encodeToDouble()));
			
			unifyMissingValues(outputRecord, MISSING_VALUE);
		}
		
		return outputRecords;
	}
	
	

	public List<CSVRecord> transform(List<CSVRecord> origRecords) {

		List<CSVRecord> outputRecords = Lists.newArrayList();
		Map<String, List<CSVRecord>> idToOrigRecords = groupRecordsByCustomerID(origRecords);
		for (List<CSVRecord> customerRecords : idToOrigRecords.values()) {
			outputRecords.addAll(transformCustomerRecords(customerRecords));
		}

		return outputRecords;
	}

	
	public List<CSVRecord> transformTest(List<CSVRecord> origRecords) {

		List<CSVRecord> outputRecords = Lists.newArrayList();
		Map<String, List<CSVRecord>> idToOrigRecords = groupRecordsByCustomerID(origRecords);
		for (List<CSVRecord> customerRecords : idToOrigRecords.values()) {
			outputRecords.addAll(transformTestCustomerRecords(customerRecords));
		}

		return outputRecords;
	}
	
	public static boolean isMissingValue(String value) {
		return value == null || value.isEmpty() || value.equals("NA");
	}

	public void unifyMissingValues(CSVRecord origRecord, String unifiedMissingValue) {
		for(Field field : origRecord.getSchema()) {
			if(isMissingValue(origRecord.get(field))) {
				origRecord.set(field, unifiedMissingValue);
			}
		}
	}
	
	private Map<String, List<CSVRecord>> groupRecordsByCustomerID(List<CSVRecord> records) {
		Map<String, List<CSVRecord>> idToRecords = Maps.newHashMapWithExpectedSize(EXPECTED_RECORDS_SIZE);
		for (CSVRecord record : records) {
			String id = record.get("customer_ID");
			List<CSVRecord> customerRecords = null;
			if (idToRecords.containsKey(id)) {
				customerRecords = idToRecords.get(id);
			} else {
				customerRecords = Lists.newArrayList();
				idToRecords.put(id, customerRecords);
			}
			customerRecords.add(record);
		}
		return idToRecords;
	}

}
