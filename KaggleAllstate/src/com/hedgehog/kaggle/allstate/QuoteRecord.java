package com.hedgehog.kaggle.allstate;

import java.util.List;

import com.google.common.collect.Lists;
import com.hedgehog.csv.CSVRecord;

public class QuoteRecord {
	
	private CSVRecord record;		
	
	public QuoteRecord(CSVRecord record) {
		this.record = record;
	}
	
	public int getShoppingPoint() {
		return record.getInt("shopping_pt");
	}
	
	public String getCustomerID() {
		return record.get("customer_ID");
	}
	
	public boolean isPurchase() {
		return record.get("record_type").equals("1");
	}
	
	public String getState() {
		return record.get("state");
	}
	
	public String getLocation() {
		return record.get("location");
	}
	
	public int getGroupSize() {
		return record.getInt("group_size");
	}
	
	public List<Integer> getPlanOptionValues() {
		List<Integer> values = Lists.newArrayList();
		values.add(record.getInt("A"));
		values.add(record.getInt("B"));
		values.add(record.getInt("C"));
		values.add(record.getInt("D"));
		values.add(record.getInt("E"));
		values.add(record.getInt("F"));
		values.add(record.getInt("G"));		
		return values;
	}
	
	public int getPlanOptionValue(String optionName) {
		return record.getInt(optionName);
	}
}

/*
# [1] "customer_ID"       "shopping_pt"       "record_type"      
# [4] "day"               "time"              "state"            
# [7] "location"          "group_size"        "homeowner"        
#[10] "car_age"           "car_value"         "risk_factor"      
#[13] "age_oldest"        "age_youngest"      "married_couple"   
#[16] "C_previous"        "duration_previous" "A"                
#[19] "B"                 "C"                 "D"                
#[22] "E"                 "F"                 "G"                
#[25] "cost"  
*/