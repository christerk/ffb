package com.balancedbytes.games.ffb;

public enum BlockResult implements IEnumWithName {
  
  SKULL("SKULL"),
  BOTH_DOWN("BOTH DOWN"),
  PUSHBACK("PUSHBACK"),
  POW_PUSHBACK("POW/PUSH"),
  POW("POW");
  
  private String fName;
  
  private BlockResult(String pName) {
    fName = pName;
  }
  
  public String getName() {
    return fName;
  }

}
