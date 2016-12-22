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

}
