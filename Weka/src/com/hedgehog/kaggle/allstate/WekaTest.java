package com.hedgehog.kaggle.allstate;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;
import weka.filters.unsupervised.instance.RemoveWithValues;

@SuppressWarnings("deprecation")
public class WekaTest {

	public static void main(String[] args) throws Exception{
		
		String filename = "src/train.arff";	//"/Users/jackie/lib/javalib/weka-3-7-10/data/iris.arff";
		String preResultFilename = "src/preTrainResult.csv";
		String resultFilename = "src/trainResult.csv";

		BufferedReader br = new BufferedReader(new FileReader(filename));
		Instances train = new Instances(br);
		br.close();
		
		
		//filter to remove the last visit
		RemoveWithValues filter = new RemoveWithValues();
		filter.setInputFormat(train);
		String options = "-C 3 -L last";
		filter.setOptions(options.split("\\s+"));		
		train = Filter.useFilter(train, filter);
		
		train.setClassIndex(5);
		train.deleteAttributeAt(2);
		train.deleteAttributeAt(0);		
		
		
		
		NaiveBayes classifier = new NaiveBayes();
		classifier.buildClassifier(train);
		
		// debug
		System.out.println("**");
		System.out.println(classifier.classifyInstance(train.firstInstance()));
		
		System.out.println(Arrays.toString(classifier.distributionForInstance(train.firstInstance())));
		
		Evaluation ev = new Evaluation(train);
		
		//ev.crossValidateModel(nb, train, 10, new Random(1));
		ev.evaluateModel(classifier, train);
		System.out.println(ev.toSummaryString());

		// Get the confusion matrix
		double[][] cmMatrix = ev.confusionMatrix();
		for(int row_i=0; row_i<cmMatrix.length; row_i++){
			for(int col_i=0; col_i<cmMatrix.length; col_i++){
				System.out.print(cmMatrix[row_i][col_i]);
				System.out.print("|");
			}
			System.out.println();
		}
		
		fillTestResult(train, preResultFilename, resultFilename, 0, classifier, ev);
	}
	
	
	/**
	 * Write a ResultFile, so each each line is ",,,,,".
	 * @param test
	 * @param testResultFilename
	 * @param numOfClasses
	 */
	private static void makePreResultFile(Instances test, String preTestResultFilename, int numOfClasses) {
		try {
			File resultFile = new File(preTestResultFilename);
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}
			FileWriter fw = new FileWriter(resultFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String newLine = System.getProperty("line.separator");
			
			String eachLine = " ";
			for (int i = 0; i < numOfClasses - 1; i++) {
				eachLine += "," + " ";
			}
			eachLine += newLine;
			for (int i = 0; i < test.size(); i++) {
				bw.write(eachLine);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void fillTestResult(Instances train, String preResultFilename, String resultFilename,
			int nthClass, NaiveBayes nb, Evaluation ev) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(preResultFilename));
			
			File resultFile = new File(resultFilename);
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
			
			for (int instanceID = 0; instanceID < train.size(); instanceID++) {
				Instance in = train.instance(instanceID);
				//System.out.print(in.value(in.classIndex()) + ", ");
				int predictedLabelAsInteger = (int) ev.evaluateModelOnce(nb, in);
				
				String line = br.readLine();
				String[] tokens = line.split(",");
				tokens[nthClass] = "" + predictedLabelAsInteger;
				
				String newLine = System.getProperty("line.separator");
				String sep = "";
				String toWrite = "";
				for (int j = 0; j < tokens.length; j++) {
					if (tokens[j].equals("")) {
						tokens[j] = " ";
					}
					toWrite += sep + tokens[j];
					sep = ",";
				}
				bw.write(toWrite + newLine);
			}
			br.close();	
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Make the resultFile rename to preResultFile
		File oldFile = new File(resultFilename);
		File newFile = new File(preResultFilename);
		boolean success = oldFile.renameTo(newFile);
	    if (!success) {
	       System.out.println("File is not succeesfully renamed.");
	    }
	}
}

