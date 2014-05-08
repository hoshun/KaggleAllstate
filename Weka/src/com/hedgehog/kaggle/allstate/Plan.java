package com.hedgehog.kaggle.allstate;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hedgehog.io.CSVRecord;
import com.hedgehog.io.Field;
import com.hedgehog.io.Field.Type;
import com.hedgehog.io.Schema;

public class Plan {

	private static final int magicStartingNum = 9;
	private CSVRecord options;

	private static final List<String> PURCHASED_OPTION_NAMES = Arrays.asList("finalA:nominal", "finalB:nominal",
			"finalC:nominal", "finalD:nominal", "finalE:nominal", "finalF:nominal", "finalG:nominal");
	private static final List<String> VIEWED_OPTION_NAMES = Arrays.asList("A:nominal", "B:nominal", "C:nominal",
			"D:nominal", "E:nominal", "F:nominal", "G:nominal");

	/**
	 * Can only be constructed through builder.
	 */
	private Plan() {
		List<Field> fields = Lists.newArrayList();
		for (String name : PURCHASED_OPTION_NAMES) {
			fields.add(new Field(name, Type.STRING));
		}
		options = new CSVRecord(new Schema(fields));
	}

	public static List<String> getPurchasedOptionNames() {
		return PURCHASED_OPTION_NAMES;
	}

	public static List<String> getViewedOptionNames() {
		return VIEWED_OPTION_NAMES;
	}

	public int get(String optionName) {
		return options.getInteger(optionName);
	}

	private void set(String optionName, int value) {
		options.set(optionName, String.valueOf(value));
	}

	public double encodeToDouble() {
		double val = 0;
		double scale = 1.0;
		for (String name : Lists.reverse(PURCHASED_OPTION_NAMES)) {
			val += scale * options.getInteger(name);
			scale = scale * 10.0;
		}

		val += magicStartingNum * scale;
		return val;
	}

	public static Plan decode(double data) {
		Plan plan = new Plan();
		// Remove magic number digit
		data = data / 10;
		for (String name : PURCHASED_OPTION_NAMES) {
			plan.options.set(name, String.valueOf(data % 10));
			data = data / 10;
		}

		return plan;
	}

	public static class Builder {

		private Plan plan;

		public Builder() {
			plan = new Plan();
		}

		public Plan build() {
			for (String name : PURCHASED_OPTION_NAMES) {
				Preconditions.checkArgument(plan.get(name) >= 0);
			}
			return plan;
		}

		public void set(String optionName, int value) {
			plan.set(optionName, value);
		}
	}

}
