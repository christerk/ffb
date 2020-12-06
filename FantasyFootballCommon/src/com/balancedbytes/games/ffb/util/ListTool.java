package com.balancedbytes.games.ffb.util;

import java.util.List;

public class ListTool {

	public static String firstElement(List<String> list) {
		if ((list == null) || (list.size() == 0)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public static boolean addAll(List<String> list, String[] array) {
		if ((list == null) || (array == null) || (array.length == 0)) {
			return false;
		}
		for (String element : array) {
			list.add(element);
		}
		return true;
	}

	public static boolean replaceAll(List<String> list, String[] array) {
		if (list == null) {
			return false;
		}
		list.clear();
		return addAll(list, array);
	}

}
