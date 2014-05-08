package com.hedgehog.kaggle.allstate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import weka.core.Instances;

import com.hedgehog.io.FileSystemUtil;

public class ClassifierRunner {

	private static List<String> UNUSED_ATTRIBUTE_NAME = Arrays.asList(
			"customer_ID:string", 
			"record_type:int", 
			"time:string",
			"location:string", 
			"finalOptions:nominal");
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File inputFile = new File("/Users/jackie/kaggle/AllState/transformed_train.csv");
		Instances data = FileSystemUtil.loadInstancesFromCSV(inputFile);
		WekaUtil.deleteAttributes(data, UNUSED_ATTRIBUTE_NAME);		
		
		System.out.println(data.numAttributes());		
		AllstateClassifier classifier = new AllstateClassifier();
		classifier.buildClassifier(data);
						
		Instances test = new Instances(data);
		//WekaUtil.deleteAttributes(test, Plan.getPurchasedOptionNames());		

		// Remove the string and other unnecessary columns
		/*
		for (String attributeToRemove : UNUSED_ATTRIBUTE_NAME) {
			test.deleteAttributeAt(WekaUtil.getAttributePosition(test, attributeToRemove));
		}
		*/
		System.out.println(test.numAttributes());
		
		for (int i = 0; i < test.size(); i++) {
			System.out.println(classifier.classifyInstance(test.get(i)));
		}
	}

}
