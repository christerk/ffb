package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PickupModifier;
import com.balancedbytes.games.ffb.RightStuffModifier;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportPassRoll implements IReport {
	
  private PassingDistance fPassingDistance;
  private boolean fFumble;
  private boolean fSafeThrowHold;
  private boolean fHailMaryPass;
  private boolean fBomb;
  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private int fMinimumRoll;
  private List<PassModifier> fModifiers;
  private boolean fReRolled;
  
  public ReportPassRoll() {    
    fModifiers = new ArrayList<PassModifier>();
  }

  public ReportPassRoll(String pPlayerId, boolean pFumble, int pRoll, boolean pReRolled, boolean pBomb) {
    this();
    fPlayerId = pPlayerId;
    fSuccessful = !pFumble;
    fFumble = pFumble;
    fRoll = pRoll;
    fMinimumRoll = 2;
    fReRolled = pReRolled;
    fPassingDistance = null;
    fFumble = pFumble;
    fSafeThrowHold = false;
    fHailMaryPass = true;
    fBomb = pBomb;
  }

  public ReportPassRoll(String pPlayerId, boolean pSuccessful, boolean pFumble, int pRoll, int pMinimumRoll, PassingDistance pPassingDistance, PassModifier[] pModifiers, boolean pReRolled, boolean pSafeThrowHold, boolean pBomb) {
    this();
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fFumble = pFumble;
    fRoll = pRoll;
    fMinimumRoll = pMinimumRoll;
    add(pModifiers);
    fReRolled = pReRolled;
    fPassingDistance = pPassingDistance;
    fFumble = pFumble;
    fSafeThrowHold = pSafeThrowHold;
    fHailMaryPass = false;
    fBomb = pBomb;
  }
  
  public ReportId getId() {
    return ReportId.PASS_ROLL;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isSuccessful() {
    return fSuccessful;
  }

  public int getRoll() {
    return fRoll;
  }

  public int getMinimumRoll() {
    return fMinimumRoll;
  }

  public PassModifier[] getModifiers() {
    return fModifiers.toArray(new PassModifier[fModifiers.size()]);
  }

  private void add(PassModifier pModifier) {
    if (pModifier != null) {
      fModifiers.add(pModifier);
    }
  }

  private void add(PassModifier[] pModifiers) {
    if (ArrayTool.isProvided(pModifiers)) {
      for (PassModifier modifier : pModifiers) {
        add(modifier);
      }
    }
  }

  public boolean hasModifier(PassModifier pModifier) {
    return fModifiers.contains(pModifier);
  }

  public boolean isReRolled() {
    return fReRolled;
  }

  public PassingDistance getPassingDistance() {
    return fPassingDistance;
  }
  
  public boolean isFumble() {
    return fFumble;
  }
  
  public boolean isHeldBySafeThrow() {
  	return fSafeThrowHold;
  }
  
  public boolean isHailMaryPass() {
		return fHailMaryPass;
	}
  
  public boolean isBomb() {
		return fBomb;
	}
  
  // transformation
  
  public IReport transform() {
  	if (isHailMaryPass()) {
  		return new ReportPassRoll(getPlayerId(), isFumble(), getRoll(), isReRolled(), isBomb());
  	} else {
	    PassModifier[] transformedPassModifiers;
	    IRollModifier[] rollModifiers = getModifiers();
	    if (ArrayTool.isProvided(rollModifiers)) {
	      transformedPassModifiers = new PassModifier[rollModifiers.length];
	      for (int i = 0; i < transformedPassModifiers.length; i++) {
	        transformedPassModifiers[i] = (PassModifier) rollModifiers[i];
	      }
	    } else {
	      transformedPassModifiers = new PassModifier[0];
	    }
	    return new ReportPassRoll(getPlayerId(), isSuccessful(), isFumble(), getRoll(), getMinimumRoll(), getPassingDistance(), transformedPassModifiers, isReRolled(), isHeldBySafeThrow(), isBomb());
  	}
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 3;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getMinimumRoll());
    PassModifier[] modifiers = getModifiers();
    pByteList.addByte((byte) modifiers.length);
    if (ArrayTool.isProvided(modifiers)) {
      for (PassModifier modifier : modifiers) {
        pByteList.addByte((byte) modifier.getId()); 
      }
    }
    pByteList.addBoolean(isReRolled());
    pByteList.addByte((byte) ((getPassingDistance() != null) ? getPassingDistance().getId() : 0));
    pByteList.addBoolean(isFumble());
    pByteList.addBoolean(isHeldBySafeThrow());
    pByteList.addBoolean(isHailMaryPass());
    pByteList.addBoolean(isBomb());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion1 = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fMinimumRoll = pByteArray.getByte();
    int nrOfModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfModifiers; i++) {
      add(PassModifier.fromId(pByteArray.getByte()));
    }
    fReRolled = pByteArray.getBoolean();
    int byteArraySerializationVersion = byteArraySerializationVersion1;
    fPassingDistance = PassingDistance.fromId(pByteArray.getByte());
    fFumble = pByteArray.getBoolean();
    fSafeThrowHold = pByteArray.getBoolean();
    if (byteArraySerializationVersion > 1) {
    	fHailMaryPass = pByteArray.getBoolean();
    }
    if (byteArraySerializationVersion > 2) {
    	fBomb = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }
    
}
