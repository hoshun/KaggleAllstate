package com.hedgehog.kaggle.allstate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class AllstateEvalution {

	private double[] accuracies;
	private double overallAccuracy;
	private List<String> trainClassLabelNames = Arrays.asList(
			"finalA:nominal", 
			"finalB:nominal", 
			"finalC:nominal", 
			"finalD:nominal", 
			"finalE:nominal", 
			"finalF:nominal", 
			"finalG:nominal");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public AllstateEvalution() {
		accuracies = new double[trainClassLabelNames.size()];
	}
	
	public void evaluate (AllstateClassifier classifier, Instances test) {
		List<Double[]> predictedAnswers = new ArrayList<Double[]>();
		for (int i = 0;0)
	}		
	
}
