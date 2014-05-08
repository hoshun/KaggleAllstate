package com.hedgehog.kaggle.allstate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AllstateClassifier implements Classifier {

	/**
	 * index 0 -> A index 1 -> B ...
	 */
	private Map<String, Classifier> purchasedOptionNameToClassifier;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public AllstateClassifier() {
		this.purchasedOptionNameToClassifier = Maps.newHashMap();
	}

	@Override
	public void buildClassifier(Instances train) throws Exception {
		for (String purchasedOptionName : Plan.getPurchasedOptionNames()) {
			Instances newTrain = new Instances(train);

			for (String labelName : Plan.getPurchasedOptionNames()) {
				if (labelName.equals(purchasedOptionName)) {
					newTrain.setClass(newTrain.attribute(labelName));
				} else {
					newTrain.deleteAttributeAt(WekaUtil.getAttributePosition(newTrain, labelName));
				}
			}

			NaiveBayes classifier = new NaiveBayes();
			classifier.buildClassifier(newTrain);

			purchasedOptionNameToClassifier.put(purchasedOptionName, classifier);
		}
	}

	private static double doubleArrayToDouble(double[] array, int magicNumber) {
		double num = magicNumber;
		for (int i = 0; i < array.length; i++) {
			num = num * 10 + array[i];
		}
		return num;
	}

	private static int indexOfMaxValue(double[] array) {
		int maxIndex = 0;
		double maxValue = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	// NOT FINISHED: Please see the comment below
	@Override
	public double classifyInstance(Instance instance) throws Exception {

		// Remove labels
		// WekaUtil.deleteAttributes(instance, Plan.getPurchasedOptionNames());
		// WekaUtil.deleteAttributes(instance, Plan.getViewedOptionNames());

		Plan.Builder builder = new Plan.Builder();
		for (String purchasedOptionName : Plan.getPurchasedOptionNames()) {
			Classifier classifier = purchasedOptionNameToClassifier.get(purchasedOptionName);
			double[] probs = classifier.distributionForInstance(instance);

			int maxIndex = indexOfMaxValue(probs);
			if (probs[maxIndex] > 0.9) {
				// Is is assumed that the label values start from 0?
				// Otherwise, how do we map the index in probs to a label?
				// We may need to rewrite the final options so it starts from 0...
				// builder.set(purchasedOptionName, value);
				double label = classifier.classifyInstance(instance);
				builder.set(purchasedOptionName, (int) label);
			} else {
				// Let's do a stupid hack and assume purchased and viewed options share the same order.
				String viewedOptionName = Plan.getViewedOptionNames().get(
						Collections.binarySearch(Plan.getPurchasedOptionNames(), purchasedOptionName));
				double viewedOptionValue = instance.value(WekaUtil.getAttributePosition(instance, viewedOptionName));
				builder.set(purchasedOptionName, (int) viewedOptionValue);
			}
		}
		Plan plan = builder.build();
		return plan.encodeToDouble();

		// double[] answer = new double[this.labelToClassifier.size()];
		// for(int cIndex = 0; cIndex < this.labelToClassifier.size(); cIndex++) {
		// String optionName = Plan.getViewedOptionNames().get(cIndex);
		// double trainLabel = instance.value(instance.attribute(WekaUtil.getAttributePosition(instance,
		// optionName)));
		//
		// Classifier classifier = this.labelToClassifier.get(cIndex);
		// double[] prob = classifier.distributionForInstance(instance);
		// int maxIndex = indexOfMaxValue(prob);
		// if (prob[maxIndex] > 0.9) {
		// answer[cIndex] = classifier.classifyInstance(instance);
		// } else {
		// answer[cIndex] = trainLabel;
		// }
		// }
		// return doubleArrayToDouble(answer, 9);
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		return this.purchasedOptionNameToClassifier.get(0).getCapabilities();
	}

}
