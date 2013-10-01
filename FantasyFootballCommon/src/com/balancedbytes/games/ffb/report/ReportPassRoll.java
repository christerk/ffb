package com.balancedbytes.games.ffb.report;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportPassRoll extends ReportSkillRoll {
	
  private static final String _XML_ATTRIBUTE_PASSING_DISTANCE = "passingDistance";  
  private static final String _XML_ATTRIBUTE_FUMBLE = "fumble";  
  private static final String _XML_ATTRIBUTE_SAFE_THROW_HOLD = "safeThrowHold";
  private static final String _XML_ATTRIBUTE_HAIL_MARY_PASS = "hailMaryPass";
  private static final String _XML_ATTRIBUTE_BOMB = "bomb";

  private PassingDistance fPassingDistance;
  private boolean fFumble;
  private boolean fSafeThrowHold;
  private boolean fHailMaryPass;
  private boolean fBomb;
  
  public ReportPassRoll() {    
    super(ReportId.PASS_ROLL);
  }

  public ReportPassRoll(String pPlayerId, boolean pFumble, int pRoll, boolean pReRolled, boolean pBomb) {
    super(ReportId.PASS_ROLL, pPlayerId, !pFumble, pRoll, 2, pReRolled);
    fPassingDistance = null;
    fFumble = pFumble;
    fSafeThrowHold = false;
    fHailMaryPass = true;
    fBomb = pBomb;
  }

  public ReportPassRoll(String pPlayerId, boolean pSuccessful, boolean pFumble, int pRoll, int pMinimumRoll, PassingDistance pPassingDistance, PassModifier[] pPassModifiers, boolean pReRolled, boolean pSafeThrowHold, boolean pBomb) {
    super(ReportId.PASS_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pPassModifiers, pReRolled);
    fPassingDistance = pPassingDistance;
    fFumble = pFumble;
    fSafeThrowHold = pSafeThrowHold;
    fHailMaryPass = false;
    fBomb = pBomb;
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
  
  // XML serialization
  
  protected void addXmlAttributes(AttributesImpl pAttributes) {
    super.addXmlAttributes(pAttributes);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_PASSING_DISTANCE, (getPassingDistance() != null) ? getPassingDistance().getName() : null);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_FUMBLE, isFumble());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_SAFE_THROW_HOLD, isHeldBySafeThrow());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_HAIL_MARY_PASS, isHailMaryPass());
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_BOMB, isBomb());
  }
  
  // ByteArray serialization
  
  @Override
  public int getByteArraySerializationVersion() {
    return 3;
  }

  @Override
  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getPassingDistance() != null) ? getPassingDistance().getId() : 0));
    pByteList.addBoolean(isFumble());
    pByteList.addBoolean(isHeldBySafeThrow());
    pByteList.addBoolean(isHailMaryPass());
    pByteList.addBoolean(isBomb());
  }

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
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
