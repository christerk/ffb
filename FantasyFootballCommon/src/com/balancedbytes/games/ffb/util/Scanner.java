package com.balancedbytes.games.ffb.util;

import java.util.HashSet;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class Scanner<T> {
	private Class<T> persistentClass;
	private ScanResult scanResult;

	public Scanner(Class<T> cls) {
		persistentClass = cls;
		scanResult = new ClassGraph()
				.enableClassInfo()
				.acceptPackages("com.balancedbytes.games.ffb")
				.scan();
	}

	@SuppressWarnings("unchecked")
	public Set<Class<T>> getSubclasses() {
		Set<Class<T>> result = new HashSet<Class<T>>();
		for (ClassInfo classInfo : scanResult.getSubclasses(persistentClass.getName())) {
			result.add((Class<T>) classInfo.loadClass());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Class<T>> getClassesImplementing() {
		Set<Class<T>> result = new HashSet<Class<T>>();
		for (ClassInfo classInfo : scanResult.getClassesImplementing(persistentClass.getName())) {
			result.add((Class<T>) classInfo.loadClass());
		}
		return result;
		
	}
}
