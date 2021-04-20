package com.fumbbl.ffb.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hilfsklasse für String-Operationen.
 */
public class StringTool {

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

  public static String bind(String pTemplate, Object pParameter) {
    return bind(pTemplate, new Object[] { pParameter });
  }

  public static String bind(String pTemplate, Object[] pParameters) {
    StringBuilder result = new StringBuilder();
    if (isProvided(pTemplate) && (pParameters != null) && (pParameters.length > 0)) {
      int startPos = 0;
      Matcher matcherParameter = _PATTERN_PARAMETER.matcher(pTemplate);
      while (matcherParameter.find()) {
        result.append(pTemplate, startPos, matcherParameter.start());
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
}
