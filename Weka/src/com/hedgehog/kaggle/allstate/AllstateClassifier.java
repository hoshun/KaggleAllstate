package com.hedgehog.kaggle.allstate;

import java.util.Collections;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class AllstateClassifier implements Classifier {

	private Map<String, Classifier> optionNameToClassifier;
	private double minProb;

	public AllstateClassifier(double minProb) {
		this.minProb = minProb;
		this.optionNameToClassifier = Maps.newHashMap();
		for (String optionName : Plan.getOptionNames()) {
			Classifier classifier = new NaiveBayes();
			this.optionNameToClassifier.put(optionName, classifier);
		}
	}

	@Override
	public void buildClassifier(Instances train) throws Exception {
		Instances baseTrain = new Instances(train);
		int planIndex = WekaUtil.getAttributePosition(baseTrain, Plan.PLAN_NAME);
		int recordTypeIndex = WekaUtil.getAttributePosition(baseTrain, Plan.RECORD_TYPE);

		// Set final option to missing value
		for (Instance instance : baseTrain) {
			instance.setMissing(planIndex);
		}

		// Filter non-purchase records.
		RemoveWithValues filter = new RemoveWithValues();
		// WARN: option index may start from 1 instead of 0.
		String option = String.format("-C %s -L first", recordTypeIndex + 1);
		filter.setInputFormat(baseTrain);
		filter.setOptions(option.split("\\s+"));
		System.out.println("before filter size " + baseTrain.size());
		baseTrain = Filter.useFilter(baseTrain, filter);
		System.out.println("after filter size " + baseTrain.size());

		// Train sub-classifier for each option.
		for (String optionName : Plan.getOptionNames()) {
			Instances singleOptionTrain = new Instances(baseTrain);
			int targetOptionIndex = WekaUtil.getAttributePosition(singleOptionTrain, optionName);

			// Set the option as the 'class'.
			singleOptionTrain.setClassIndex(targetOptionIndex);
			NaiveBayes classifier = new NaiveBayes();
			classifier.buildClassifier(singleOptionTrain);
			optionNameToClassifier.put(optionName, classifier);
		}
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

	@Override
	public double classifyInstance(Instance instance) throws Exception {
		int planIndex = WekaUtil.getAttributePosition(instance, Plan.PLAN_NAME);
		String planValue = WekaUtil.getNominalValue(instance, Plan.PLAN_NAME);
		instance.setMissing(planIndex);

		Plan.Builder builder = new Plan.Builder();
		for (String optionName : Plan.getOptionNames()) {
			Classifier classifier = optionNameToClassifier.get(optionName);

			// WARNING: altering the class index of the instances.
			instance.dataset().setClassIndex(WekaUtil.getAttributePosition(instance, optionName));
			double[] probs = classifier.distributionForInstance(instance);

			int maxIndex = indexOfMaxValue(probs);
			if (probs[maxIndex] > this.minProb) {
				double labelIndex = classifier.classifyInstance(instance); // This is the index of the label, not the
																			// nominal value
				String label = instance.classAttribute().value((int) labelIndex);
				builder.set(optionName, Integer.parseInt(label));
			} else {
				String value = WekaUtil.getNominalValue(instance, optionName);
				builder.set(optionName, Integer.parseInt(value));
			}
		}

		// Reset the class index of its dataset.
		instance.dataset().setClassIndex(WekaUtil.getAttributePosition(instance, Plan.PLAN_NAME));

		instance.setClassValue(planValue);
		Plan plan = builder.build();
		return plan.encodeToDouble();
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		throw new IllegalAccessError("Not implemented.");
	}

	@Override
	public Capabilities getCapabilities() {
		return this.optionNameToClassifier.get(0).getCapabilities();
	}

	/**
	 * Back door method for testing.
	 * 
	 * @param minProb
	 */
	@Deprecated
	public void setMinProb(double minProb) {
		this.minProb = minProb;
	}
}
