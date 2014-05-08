package com.hedgehog.kaggle.allstate;

import java.util.List;

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
		Preconditions.checkArgument(position < instances.numAttributes());
		return position;
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
}
