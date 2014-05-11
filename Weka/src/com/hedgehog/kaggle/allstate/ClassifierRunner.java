package com.hedgehog.kaggle.allstate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;
import weka.filters.unsupervised.instance.SubsetByExpression;

import com.hedgehog.io.FileSystemUtil;

public class ClassifierRunner {

	private static List<String> UNUSED_ATTRIBUTE_NAME = Arrays.asList(
			"customer_ID:string", 			
			"time:string",
			"location:string");
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File inputFile = new File("/Users/jackie/kaggle/AllState/transformed_train.csv");		
		Instances data = FileSystemUtil.loadInstancesFromCSV(inputFile);
				
		System.out.println("data read.");
		WekaUtil.deleteAttributes(data, UNUSED_ATTRIBUTE_NAME);	
		
		data.setClassIndex(data.numAttributes() - 1);
		AllstateClassifier classifier = new AllstateClassifier(0.6);
		classifier.buildClassifier(data);
		System.out.println("built model.");	
		
		//Weka Evaluation doesn't work here.
		//Evaluation ev = new Evaluation(data);
		//ev.evaluateModelOnce(classifier, data.get(0));
		//ev.evaluateModel(classifier, data);
		//System.out.println(ev.toSummaryString());
				
		Instances evalData = new Instances(data);
		int shoppingPtIndex = WekaUtil.getAttributePosition(evalData, Plan.SHOPPING_PT);
		for(int i = evalData.size()-1; i >=0; i--) {
			Instance instance = evalData.get(i);
			if(instance.value(shoppingPtIndex) != 2) {
				evalData.remove(i);
			}
		}
		System.out.println("filtered data size " + evalData.size());
				
		for (int i = 7; i <= 10; i++) {
			double minProb = (double) i / 10;
			AllstateEvaluation ev = new AllstateEvaluation(evalData, classifier, minProb);
			System.out.println("overall accuracy for minProb: " + minProb + " is " + String.format("%1.2f",ev.getOverallAccuracy()));		
		}
				
//		File inputTestFile = new File("/Users/jackie/kaggle/AllState/transformed_test.csv");
//		Instances testData = FileSystemUtil.loadInstancesFromCSV(inputTestFile);
//		
//		testData = removeDuplicateID(testData);
//		System.out.println(testData.toString());
//		WekaUtil.deleteAttributes(testData, UNUSED_ATTRIBUTE_NAME);
						
		//testData.setClassIndex(data.numAttributes() - 1);
		//writeTestResultFile(testData, classifier, "/Users/jackie/kaggle/AllState/results/nb_result.csv");
	}
	
	public static Instances removeDuplicateID(Instances data) {
		Instances finalData = new Instances(data);
		Instance instance = data.instance(data.size() - 1);
		String ID = WekaUtil.getNominalValue (instance, Plan.CUSTOMER_ID_NAME);
		String currentID = ID;

		for (int i = data.size() - 2; i >= 0; i--) {
			instance = finalData.instance(i);
			ID = WekaUtil.getNominalValue(instance, Plan.CUSTOMER_ID_NAME);
			if (ID.equals(currentID)) {
				finalData.remove(i);
			} else {
				currentID = ID;
			}
		}
		return finalData;
	}
	
	
	// Write the A,B,C,D,E,F,G option to result file
	public static void writeTestResultFile (Instances testData, AllstateClassifier classifier, 
			String resultFilename) throws Exception {
		try {
			File resultFile = new File(resultFilename);
			if (!resultFile.exists()) {
				resultFile.createNewFile();	
			}
			FileWriter fw = new FileWriter(resultFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			String newLine = System.getProperty("line.separator");
			for (int i = 0; i < testData.size(); i++) {
				classifier.setMinProb(0.6);
				double label = classifier.classifyInstance(testData.get(i));
				Plan plan = Plan.decode(label);
				String line = "";
				String sep = "";
				for (String purchasedOptionName : Plan.getOptionNames()) {
					line += sep + plan.get(purchasedOptionName);
				}
				line += newLine;
				bw.write(line);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
