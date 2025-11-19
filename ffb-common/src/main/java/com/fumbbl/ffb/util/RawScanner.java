package com.fumbbl.ffb.util;

import io.github.classgraph.ClassInfo;

import java.util.HashSet;
import java.util.Set;

public class RawScanner<T2> {

	private Class<T2> persistentClass;

	public RawScanner(Class<T2> cls) {
		persistentClass = cls;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<T2>> getSubclasses() {
		Set<Class<T2>> result = new HashSet<>();
		for (ClassInfo classInfo : ScannerSingleton.getInstance().scanResult.getSubclasses(persistentClass.getName())) {
			if (!classInfo.isAbstract()) {
				try {
					result.add((Class<T2>) Class.forName(classInfo.getName()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<T2>> getClassesImplementing() {
		Set<Class<T2>> result = new HashSet<>();
		for (ClassInfo classInfo : ScannerSingleton.getInstance().scanResult.getClassesImplementing(persistentClass.getName())) {
			if (!classInfo.isAbstract()) {
				try {
					result.add((Class<T2>) Class.forName(classInfo.getName()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return result;

	}
}
