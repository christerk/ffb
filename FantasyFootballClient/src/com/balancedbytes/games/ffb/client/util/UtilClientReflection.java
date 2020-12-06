package com.balancedbytes.games.ffb.client.util;

import java.lang.reflect.Method;

import javax.swing.JTable;

public class UtilClientReflection {

	public enum OS {
		Windows, Linux, OSX, Other
	};

	private static int javaVersionMajor = -1;
	private static int javaVersionMinor = -1;

	public static boolean compliesTo(int major, int minor) {
		if (javaVersionMajor < 0) {
			String version = System.getProperty("java.specification.version");
			String[] list = version.split("\\.");
			if (list.length >= 2) {
				javaVersionMajor = Integer.parseInt(list[0]);
				javaVersionMinor = Integer.parseInt(list[1]);
			}
		}

		return (javaVersionMajor > major) || (javaVersionMajor == major && javaVersionMinor >= minor);
	}

	public static OS getOS() {
		String os = System.getProperty("os.name");

		if (os.contains("Windows"))
			return OS.Windows;

		if (os.contains("Linux"))
			return OS.Linux;

		if (os.contains("Mac OS X"))
			return OS.OSX;

		return OS.Other;
	}

	private static void callMethodBoolToVoid(Object o, String method, boolean flag) {
		try {
			Method m;
			m = o.getClass().getMethod(method, boolean.class);
			m.invoke(o, flag);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private static int callMethodIntToInt(Object o, String method, int value) {
		Method m;
		int result = 0;
		try {
			m = o.getClass().getMethod(method, int.class);
			result = (Integer) m.invoke(o, value);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	public static void setFillsViewportHeight(JTable table, boolean flag) {
		if (compliesTo(1, 6))
			callMethodBoolToVoid(table, "setFillsViewportHeight", flag);
	}

	public static void setAutoCreateRowSorter(JTable table, boolean flag) {
		if (compliesTo(1, 6))
			callMethodBoolToVoid(table, "setAutoCreateRowSorter", flag);
	}

	public static int convertRowIndexToModel(JTable table, int row) {
		if (compliesTo(1, 6))
			return callMethodIntToInt(table, "convertRowIndexToModel", row);
		else
			return table.getSelectedRow();
	}

}
