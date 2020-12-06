package com.balancedbytes.games.ffb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Kalimar
 */
public class StringTool {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	private static Pattern _PATTERN_PARAMETER = Pattern.compile("[$]([0-9]+)");

	public static boolean isProvided(Object pObject) {
		return ((pObject != null) && (pObject.toString().length() > 0));
	}

	public static String print(String pString) {
		if (isProvided(pString)) {
			return pString;
		} else {
			return "";
		}
	}

	public static boolean isEqual(String pString1, String pString2) {
		if (pString1 != null) {
			return pString1.equals(pString2);
		} else
			return pString2 == null;
	}

	public static String bind(String pTemplate, Object pParameter) {
		return bind(pTemplate, new Object[] { pParameter });
	}

	public static String bind(String pTemplate, Object pParameter1, Object pParameter2) {
		return bind(pTemplate, new Object[] { pParameter1, pParameter2 });
	}

	public static String bind(String pTemplate, Object pParameter1, Object pParameter2, Object pParameter3) {
		return bind(pTemplate, new Object[] { pParameter1, pParameter2, pParameter3 });
	}

	public static String bind(String pTemplate, Object[] pParameters) {
		StringBuilder result = new StringBuilder();
		if (isProvided(pTemplate) && (pParameters != null) && (pParameters.length > 0)) {
			int startPos = 0;
			Matcher matcherParameter = _PATTERN_PARAMETER.matcher(pTemplate);
			while (matcherParameter.find()) {
				result.append(pTemplate.substring(startPos, matcherParameter.start()));
				startPos = matcherParameter.end();
				int index = Integer.parseInt(matcherParameter.group(1)) - 1;
				if (index < pParameters.length) {
					result.append(pParameters[index]);
				}
			}
			result.append(pTemplate.substring(startPos, pTemplate.length()));
		}
		return result.toString();
	}

	public static String formatThousands(long pNumber) {
		StringBuilder result = new StringBuilder();
		String numberString = Long.toString(pNumber);
		int pos = 0;
		if ((numberString.length() % 3) > 0) {
			result.append(numberString.substring(0, numberString.length() % 3));
			pos += numberString.length() % 3;
		}
		while (pos < numberString.length()) {
			if (pos > 0) {
				result.append(",");
			}
			result.append(numberString.substring(pos, pos + 3));
			pos += 3;
		}
		return result.toString();
	}

	public static String buildEnumeration(String[] pItems) {
		StringBuilder enumeration = new StringBuilder();
		for (int i = 0; i < pItems.length; i++) {
			if (i > 0) {
				if (i == pItems.length - 1) {
					enumeration.append(" and ");
				} else {
					enumeration.append(", ");
				}
			}
			enumeration.append(pItems[i]);
		}
		return enumeration.toString();
	}

	public static void main(String[] args) {
		System.out.println(bind("Dies ist ein $1 Test der Bind-Methode $2", new String[] { "erster", "!" }));
		System.out.println(formatThousands(2130000));
	}

}
