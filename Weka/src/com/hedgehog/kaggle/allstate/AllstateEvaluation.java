package com.hedgehog.kaggle.allstate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class AllstateEvaluation {

	// May not need to write down the accuracies for each options
	private Map<String, Double> accuracies;
	private double overallAccuracy;	
	
	public AllstateEvaluation(Instances data, AllstateClassifier classifier, double minProb) throws Exception {
		int count = 0;
		for (int i = 0; i < data.size(); i++) {
			Instance instance = data.instance(i);

			// classLabelIndex is the  index of the label, classLabel is the nominal label itself.
			Attribute classAttr = instance.attribute(WekaUtil.getAttributePosition(instance, Plan.PLAN_NAME));
			double classLabelIndex = instance.value(classAttr);
			int classLabel = (int) Double.parseDouble(classAttr.value((int)classLabelIndex));

			classifier.setMinProb(minProb);
			int predictedLabel = (int) classifier.classifyInstance(instance);
			if (classLabel == predictedLabel) {
				count++;
			}
			
			// int shoppingPoint = (int) instance.value(WekaUtil.getAttributePosition(instance,"shopping_pt:int"));
			// System.out.println(shoppingPoint + "," + classLabel + "," + predictedLabel + "," + count);
		}
		this.overallAccuracy = (double) count / data.size();
	}
	
	public void evaluate (AllstateClassifier classifier, Instances test) {
		List<Double[]> predictedAnswers = new ArrayList<Double[]>();		
	}
	
	public double getOverallAccuracy() {
		return this.overallAccuracy;
	}
	
}
