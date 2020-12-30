package com.balancedbytes.games.ffb;

import java.util.HashMap;
import java.util.Map;

public class KeyedItemRegistry<T extends IKeyedItem> {
	private Map<Object, T> itemsByKey = new HashMap<>();
	private Map<Class, T> itemsByClass = new HashMap<>();
	
	public KeyedItemRegistry() {
	}
	
	public boolean add(T item) {
		if (itemsByKey.containsKey(item.getKey())) {
			return false;
		}
		
		itemsByKey.put(item.getKey(), item);
		itemsByClass.put(item.getClass(), item);
		
		return true;
	}
	
	public T forClass(Class cls) {
		return itemsByClass.get(cls);
	}
	
	public T forKey(Object key) {
		return itemsByKey.get(key);
	}
}
