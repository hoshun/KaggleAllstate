package com.hedgehog.io;

import com.google.common.base.Preconditions;

public class Field {

	private final String name;
	private final Type type;

	public static enum Type {
		STRING("string"), INT("int"), DOUBLE("double"), NOMINAL("nominal");
		private String val;

		private Type(String val) {
			this.val = val;
		}

		public static Type parseType(String data) {
			Type parsedType = null;
			for (Type type : Type.values()) {
				if (type.val.equalsIgnoreCase(data)) {
					parsedType = type;
				}
			}
			Preconditions.checkNotNull(parsedType, data + " is not a Type.");
			return parsedType;
		}
	}

	/**
	 * Construct Field with name:type.
	 * 
	 * @param data
	 */
	public static Field parseField(String data) {
		String[] values = data.split(":", -1);
		Preconditions.checkArgument(values.length == 2);

		return new Field(values[0].trim(), Type.parseType(values[1].trim()));
	}

	public Field(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return name + ":" + type.val;
	}

}
