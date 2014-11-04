package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class FieldCoordinate {
  
  public static final int FIELD_WIDTH = 26;
  public static final int FIELD_HEIGHT = 15;
  
  public static final int RSV_HOME_X = -1;
  public static final int KO_HOME_X = -2;
  public static final int BH_HOME_X = -3;
  public static final int SI_HOME_X = -4;
  public static final int RIP_HOME_X = -5;
  public static final int BAN_HOME_X = -6;
  public static final int MNG_HOME_X = -7;
  
  public static final int RSV_AWAY_X = 30;
  public static final int KO_AWAY_X = 31;
  public static final int BH_AWAY_X = 32;
  public static final int SI_AWAY_X = 33;
  public static final int RIP_AWAY_X = 34;
  public static final int BAN_AWAY_X = 35;
  public static final int MNG_AWAY_X = 36;
  
  private int fX;
  private int fY;
  
  public FieldCoordinate(int pX, int pY) {
    fX = pX;
    fY = pY;
  }
  
  public FieldCoordinate(int pNrOfSquare) {
    this(pNrOfSquare % FIELD_WIDTH, pNrOfSquare / FIELD_WIDTH);
  }

  public int getX() {
    return fX;
  }

  public int getY() {
    return fY;
  }
  
  public int getNrOfSquare() {
    return ((getY() * FIELD_WIDTH) + getX());
  }
  
  // java.lang.Object#hashCode()
  public int hashCode() {
    return getNrOfSquare();
  }

  // java.lang.Object#equals(java.lang.Object)
  public boolean equals(Object pObject) {
    if ((pObject != null) && (pObject instanceof FieldCoordinate)) {
      FieldCoordinate otherCoordinate = (FieldCoordinate) pObject;
      return ((getY() == otherCoordinate.getY()) && (getX() == otherCoordinate.getX()));
    } else {
      return super.equals(pObject);
    }
  }
  
  public FieldCoordinate add(int pDeltaX, int pDeltaY) {
    return new FieldCoordinate(getX() + pDeltaX, getY() + pDeltaY);
  }
  
  public int distanceInSteps(FieldCoordinate pOtherCoordinate) {
    int result = -1;
    if (pOtherCoordinate != null) {
      result = Math.max(Math.abs(getX() - pOtherCoordinate.getX()), Math.abs(getY() - pOtherCoordinate.getY()));
    }
    return result;
  }
  
  public boolean isAdjacent(FieldCoordinate pOtherCoordinate) {
    return (pOtherCoordinate != null) && (distanceInSteps(pOtherCoordinate) == 1);
  }
  
  public FieldCoordinate transform() {
    switch (getX()) {
      case RSV_HOME_X:
        return new FieldCoordinate(RSV_AWAY_X, getY()); 
      case KO_HOME_X:
        return new FieldCoordinate(KO_AWAY_X, getY()); 
      case BH_HOME_X:
        return new FieldCoordinate(BH_AWAY_X, getY()); 
      case SI_HOME_X:
        return new FieldCoordinate(SI_AWAY_X, getY()); 
      case RIP_HOME_X:
        return new FieldCoordinate(RIP_AWAY_X, getY()); 
      case BAN_HOME_X:
        return new FieldCoordinate(BAN_AWAY_X, getY()); 
      case MNG_HOME_X:
        return new FieldCoordinate(MNG_AWAY_X, getY()); 
      case RSV_AWAY_X:
        return new FieldCoordinate(RSV_HOME_X, getY()); 
      case KO_AWAY_X:
        return new FieldCoordinate(KO_HOME_X, getY()); 
      case BH_AWAY_X:
        return new FieldCoordinate(BH_HOME_X, getY()); 
      case SI_AWAY_X:
        return new FieldCoordinate(SI_HOME_X, getY()); 
      case RIP_AWAY_X:
        return new FieldCoordinate(RIP_HOME_X, getY()); 
      case BAN_AWAY_X:
        return new FieldCoordinate(BAN_HOME_X, getY()); 
      case MNG_AWAY_X:
        return new FieldCoordinate(MNG_HOME_X, getY()); 
      default:
        return new FieldCoordinate(FIELD_WIDTH - 1 - getX(), getY()); 
    }
  }
  
  public String toString() {
  	StringBuilder sb = new StringBuilder();
  	sb.append("(").append(getX()).append(",").append(getY()).append(")");
  	return sb.toString();
  }
  
  public int compareTo(FieldCoordinate pAnotherFc) {
  	if (pAnotherFc == null){
  		return -1;
  	}
  	if (!(pAnotherFc instanceof FieldCoordinate)) {
  		return -1;
  	}
  	if (pAnotherFc.getX() < getX()) {
  		return 1;
  	}
  	if (pAnotherFc.getX() > getX()) {
  		return -1;
  	}
  	return (getY() - pAnotherFc.getY());
  }
  
  public boolean isBoxCoordinate() {
    switch (getX()) {
      case RSV_HOME_X:
      case RSV_AWAY_X:
      case KO_HOME_X:
      case KO_AWAY_X:
      case BH_HOME_X:
      case BH_AWAY_X:
      case SI_HOME_X:
      case SI_AWAY_X:
      case RIP_HOME_X:
      case RIP_AWAY_X:
      case BAN_HOME_X:
      case BAN_AWAY_X:
      case MNG_HOME_X:
      case MNG_AWAY_X:
        return true;
      default:
        return false;
    }
  }
  
  public static FieldCoordinate transform(FieldCoordinate pFieldCoordinate) {
    return (pFieldCoordinate != null) ? pFieldCoordinate.transform() : null;
  }
  
  public static boolean equals(FieldCoordinate pCoordinate1, FieldCoordinate pCoordinate2) {
    if (pCoordinate1 != null) {
      return pCoordinate1.equals(pCoordinate2);
    } else if (pCoordinate2 != null) {
      return pCoordinate2.equals(pCoordinate1);
    } else {
      return true;
    }
  }
  
}
