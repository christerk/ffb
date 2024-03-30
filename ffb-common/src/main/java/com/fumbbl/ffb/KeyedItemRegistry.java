package com.fumbbl.ffb;

import java.util.HashMap;
import java.util.Map;

public class KeyedItemRegistry<T extends IKeyedItem> {
	private Map<String, T> itemsByKey = new HashMap<>();
	private Map<Class, T> itemsByClass = new HashMap<>();
	
	public KeyedItemRegistry() {
	}
	
	public boolean add(T item) {
		if (itemsByKey.containsKey(item.getKey())) {
			return false;
		}
		
		itemsByKey.put(item.getKey().toLowerCase(), item);
		itemsByClass.put(item.getClass(), item);
		
		return true;
	}
	
	public T forClass(Class cls) {
		return itemsByClass.get(cls);
	}
	
	public T forKey(String key) {
		return itemsByKey.get(key.toLowerCase());
	}
}
