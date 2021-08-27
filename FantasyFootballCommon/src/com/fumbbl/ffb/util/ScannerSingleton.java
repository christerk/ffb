package com.fumbbl.ffb.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class ScannerSingleton {
	private static ScannerSingleton instance = null;
	public ScanResult scanResult;
	
	public ScannerSingleton() {
		scanResult = new ClassGraph()
				.enableClassInfo()
				.acceptPackages("com.fumbbl.ffb")
				.enableRemoteJarScanning()
				.scan();
	}
	public static ScannerSingleton getInstance() {
		if (instance == null) {
			instance = new ScannerSingleton();
		}
		return instance;
	}
}