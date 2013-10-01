package com.balancedbytes.games.ffb.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hilfsklasse für String-Operationen.
 */
public class StringTool {
  
  public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
  
  private static Pattern _PATTERN_PARAMETER = Pattern.compile("[$]([0-9]+)"); //$NON-NLS-1$
  
  public static boolean isProvided(String pString) {
    return ((pString != null) && (pString.length() > 0));
  }
  
  public static String print(String pString) {
    if (isProvided(pString)) {
      return pString;
    } else {
      return ""; //$NON-NLS-1$
    }
  }
  
  public static boolean isEqual(String pString1, String pString2) {
    if (pString1 != null) {
      return pString1.equals(pString2);
    } else if (pString2 != null) {
      return false;
    } else {
      return true;
    }
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
      result.append(pTemplate.substring(startPos));
    }
    return result.toString();
  }
  
  public static String formatThousands(int pNumber) {
    StringBuilder result = new StringBuilder();
    String numberString = Integer.toString(pNumber);
    int pos = 0;
    if ((numberString.length() % 3) > 0) {
      result.append(numberString.substring(0, numberString.length() % 3));
      pos += numberString.length() % 3;
    }
    while (pos < numberString.length()) {
      if (pos > 0) {
        result.append(","); //$NON-NLS-1$
      }
      result.append(numberString.substring(pos, pos + 3));
      pos += 3;
    }
    return result.toString();
  }
  
  public static int parseInt(String pString, int pDefault) {
    int result = 0;
    if (isProvided(pString)) {
      try {
        result = Integer.parseInt(pString.trim());
      } catch (NumberFormatException pNumberFormatException) {
        result = pDefault;
      }
    }
    return result;
  }
  
  public static String removeLeadingZeroes(String pNumber) {
    StringBuilder result = new StringBuilder();
    if (isProvided(pNumber)) {
      boolean copy = false;
      for (int i = 0; i < pNumber.length(); i++) {
        if (!copy && pNumber.charAt(i) != '0') {
          copy = true;
        }
        if (copy) {
          result.append(pNumber.charAt(i));
        }
      }
    }
    return result.toString();
  }
  
  public static String shortenString(String pOriginal, int pMaxLength) {
    if (isProvided(pOriginal)) {
      if (pOriginal.length() > pMaxLength) {
        StringBuilder result = new StringBuilder();
        result.append(pOriginal.substring(0, pMaxLength).trim());
        result.append("..."); //$NON-NLS-1$
        return result.toString();
      }
    }
    return pOriginal;
  }
  
  public static String getStackTrace(Throwable aThrowable) {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    aThrowable.printStackTrace(printWriter);
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
    
}
