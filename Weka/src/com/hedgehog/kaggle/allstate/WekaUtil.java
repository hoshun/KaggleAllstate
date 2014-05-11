package com.hedgehog.kaggle.allstate;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.base.Preconditions;

// Note if the attributeName is not in the instance, it will return position as the size of the instance.
public class WekaUtil {
	public static int getAttributePosition(Instance instance, String attributeName) {
		int position = 0;
		for (int i = 0; i < instance.numAttributes(); i++) {
			if (instance.attribute(i).name().equals(attributeName)) {
				return position;
			}
			position++;
		}
		Preconditions.checkArgument(position < instance.numAttributes());
		return position;
	}

	public static int getAttributePosition(Instances instances, String attributeName) {
		int position = 0;
		for (int i = 0; i < instances.numAttributes(); i++) {
			if (instances.attribute(i).name().equals(attributeName)) {
				return position;
			}
			position++;
		}

		throw new IllegalArgumentException("Cannot find attribute name in instances." + attributeName);
	}

	public static int[] getAttributePositions(Instances instances, List<String> attributeNames) {
		int[] indices = new int[attributeNames.size()];
		int i = 0;
		for (String attributeName : attributeNames) {
			indices[i] = getAttributePosition(instances, attributeName);
			i++;
		}
		return indices;
	}

	public static void deleteAttributes(Instances instances, List<String> attrNames) {
		for (String attrName : attrNames) {
			instances.deleteAttributeAt(WekaUtil.getAttributePosition(instances, attrName));
		}
	}

	public static void deleteAttributes(Instance instance, List<String> attrNames) {
		for (String attrName : attrNames) {
			instance.deleteAttributeAt(WekaUtil.getAttributePosition(instance, attrName));
		}
	}

	public static String getNominalValue(Instance instance, String AttributeName) {
		int indexOfAttribute = getAttributePosition(instance, AttributeName);
		Attribute attribute = instance.attribute(indexOfAttribute);
		double IDIndex = instance.value(attribute);
		String value = attribute.value((int) IDIndex);
		return value;
	}
}
