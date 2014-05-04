package com.hedgehog.csv;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Schema {
	
	private Map<String, Integer> nameToIndex;
	
	public Schema(List<String> names) {
		nameToIndex = Maps.newTreeMap();
		Maps.newLinkedHashMap();
		int index = 0;
		for(String name : names) {
			nameToIndex.put(name, index);
			index++;
		}
	}
	
	public boolean containsName(String name) {
		return nameToIndex.containsKey(name);
	}
	
	public int getIndexByName(String name) {
		return nameToIndex.get(name);
	}
	
	public List<String> getNames() {
		List<String> names = Lists.newArrayList(); 
		names.addAll(nameToIndex.keySet());
		return names;
	}
	
}
