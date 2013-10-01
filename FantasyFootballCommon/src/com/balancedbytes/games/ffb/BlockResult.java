package com.balancedbytes.games.ffb;

public enum BlockResult {
  
  SKULL(1, "SKULL"),
  BOTH_DOWN(2, "BOTH DOWN"),
  PUSHBACK(3, "PUSHBACK"),
  POW_PUSHBACK(4, "POW/PUSH"),
  POW(5, "POW");
  
  private int fId;
  private String fName;
  
  private BlockResult(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public static BlockResult fromId(int pId) {
    for (BlockResult result : values()) {
      if (result.getId() == pId) {
        return result;
      }
    }
    return null;
  }

  public static BlockResult fromName(String pName) {
    for (BlockResult result : values()) {
      if (result.getName().equalsIgnoreCase(pName)) {
        return result;
      }
    }
    return null;
  }
  
  public static BlockResult fromRoll(int pRoll) {
    switch (pRoll) {
      case 1:
        return BlockResult.SKULL;
      case 2:
        return BlockResult.BOTH_DOWN;
      case 5:
        return BlockResult.POW_PUSHBACK;
      case 6:
        return BlockResult.POW;
      default:  // 3 and 4
        return BlockResult.PUSHBACK;
    }
  }

}
