package com.balancedbytes.games.ffb.util;

import java.util.HashSet;
import java.util.Set;

import io.github.classgraph.ClassInfo;

public class RawScanner<T2> {
	
	private Class<T2> persistentClass;
	
	public RawScanner(Class<T2> cls) {
		persistentClass = cls;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Class<T2>> getSubclasses() {
		Set<Class<T2>> result = new HashSet<>();
		for (ClassInfo classInfo : ScannerSingleton.getInstance().scanResult.getSubclasses(persistentClass.getName())) {
			result.add((Class<T2>) classInfo.loadClass());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<T2>> getClassesImplementing() {
		Set<Class<T2>> result = new HashSet<Class<T2>>();
		for (ClassInfo classInfo : ScannerSingleton.getInstance().scanResult.getClassesImplementing(persistentClass.getName())) {
			result.add((Class<T2>) classInfo.loadClass());
		}
		return result;
		
	}
}