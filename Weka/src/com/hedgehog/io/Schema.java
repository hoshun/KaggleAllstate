package com.hedgehog.io;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.hedgehog.io.Field.Type;

public class Schema implements Iterable<Field>{

	private Map<String, Integer> nameToIndex;
	private List<Field> fields;

	public static void main(String[] args) {
		Schema schema = Schema.of(new Field("abc", Type.STRING), new Field("xyz", Type.INT));
	}

	public Schema(List<Field> fields) {
		this.fields = fields;
		nameToIndex = Maps.newTreeMap();
		int index = 0;
		for (Field field : fields) {
			nameToIndex.put(field.getName(), index);
			index++;
		}
	}

	public static Schema of(Field... fields) {
		return new Schema(Arrays.asList(fields));
	}	
	
	public boolean containsName(String name) {
		return nameToIndex.containsKey(name);
	}

	public Field getField(String name) {
		Preconditions.checkArgument(containsName(name));
		return fields.get(getIndex(name));
	}

	public int getIndex(String name) {
		Preconditions.checkArgument(containsName(name));
		return nameToIndex.get(name);
	}

	public int getIndex(Field field) {
		return getIndex(field.getName());
	}

	public int size() {
		return fields.size();
	}
	
	public String toString() {
		return Joiner.on(",").join(fields);
	}

	@Override
	public Iterator<Field> iterator() {		
		return fields.iterator();
	}
}
