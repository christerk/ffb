package com.balancedbytes.games.ffb.report;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ReportCatchRoll extends ReportSkillRoll {

  private static final String _XML_ATTRIBUTE_BOMB = "bomb";

  private boolean fBomb;

  public ReportCatchRoll() {
    super(ReportId.CATCH_ROLL);
  }

  public ReportCatchRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, IRollModifier[] pRollModifiers, boolean pReRolled, boolean pBomb) {
    super(ReportId.CATCH_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pRollModifiers, pReRolled);
    fBomb = pBomb;
  }

  public boolean isBomb() {
    return fBomb;
  }

  // transformation

  public IReport transform() {
    return new ReportCatchRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), getModifiers(), isReRolled(), isBomb());
  }

  // XML serialization

  protected void addXmlAttributes(AttributesImpl pAttributes) {
    super.addXmlAttributes(pAttributes);
    UtilXml.addAttribute(pAttributes, _XML_ATTRIBUTE_BOMB, isBomb());
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    addCommonPartTo(pByteList, getByteArraySerializationVersion());
    pByteList.addBoolean(isBomb());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = initCommonPartFrom(pByteArray);
    if (byteArraySerializationVersion > 1) {
      fBomb = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }

}
