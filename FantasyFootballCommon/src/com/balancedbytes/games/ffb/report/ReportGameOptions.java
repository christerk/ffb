package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Georg Seipler
 */
public class ReportGameOptions implements IReport {

  private static final String _XML_TAG_OVERTIME = "overtime";
  private static final String _XML_TAG_TURNTIME = "turntime";
  private static final String _XML_TAG_SNEAKY_GIT_AS_FOUL_GUARD = "sneakyGitAsFoulGuard";
  private static final String _XML_TAG_FOUL_BONUS_OUTSIDE_TACKLEZONE = "foulBonusOutsideTacklezone";
  private static final String _XML_TAG_RIGHT_STUFF_CANCELS_TACKLE = "rightStuffCancelsTackle";
  private static final String _XML_TAG_PILING_ON_WITHOUT_MODIFIER = "pilingOnWithoutModifier";

  private boolean fOvertime;
  private int fTurntime;
  // Sneaky Git works as it does now and like Guard for fouling assists
  private boolean fSneakyGitAsFoulGuard;
  // +1 to the AV for a foul if the fouler is not in an opposing tackle zone
  private boolean fFoulBonusOutsideTacklezone;
  // Right Stuff prevents Tackle from negating Dodge for Pow!
  private boolean fRightStuffCancelsTackle;
  // A player cannot use his skills that modify the Armour or Injury roll when
  // using a Piling On re-roll
  private boolean fPilingOnWithoutModifier;

  public ReportGameOptions() {
    super();
  }

  public void init(ReportGameOptions pReportGameOptions) {
    if (pReportGameOptions != null) {
      fOvertime = pReportGameOptions.isOvertime();
      fTurntime = pReportGameOptions.getTurntime();
      fSneakyGitAsFoulGuard = pReportGameOptions.isSneakyGitAsFoulGuard();
      fFoulBonusOutsideTacklezone = pReportGameOptions.isFoulBonusOutsideTacklezone();
      fRightStuffCancelsTackle = pReportGameOptions.isRightStuffCancelsTackle();
      fPilingOnWithoutModifier = pReportGameOptions.isPilingOnWithoutModifier();
    }
  }

  public ReportId getId() {
    return ReportId.GAME_OPTIONS;
  }

  public boolean isOvertime() {
    return fOvertime;
  }

  public int getTurntime() {
    return fTurntime;
  }

  public boolean isSneakyGitAsFoulGuard() {
    return fSneakyGitAsFoulGuard;
  }

  public boolean isFoulBonusOutsideTacklezone() {
    return fFoulBonusOutsideTacklezone;
  }

  public boolean isRightStuffCancelsTackle() {
    return fRightStuffCancelsTackle;
  }

  public boolean isPilingOnWithoutModifier() {
    return fPilingOnWithoutModifier;
  }

  // transformation

  public IReport transform() {
    ReportGameOptions transformedReport = new ReportGameOptions();
    transformedReport.init(this);
    return transformedReport;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    UtilXml.addValueElement(pHandler, _XML_TAG_OVERTIME, isOvertime());
    UtilXml.addValueElement(pHandler, _XML_TAG_TURNTIME, getTurntime());
    UtilXml.addValueElement(pHandler, _XML_TAG_SNEAKY_GIT_AS_FOUL_GUARD, isSneakyGitAsFoulGuard());
    UtilXml.addValueElement(pHandler, _XML_TAG_FOUL_BONUS_OUTSIDE_TACKLEZONE, isFoulBonusOutsideTacklezone());
    UtilXml.addValueElement(pHandler, _XML_TAG_RIGHT_STUFF_CANCELS_TACKLE, isRightStuffCancelsTackle());
    UtilXml.addValueElement(pHandler, _XML_TAG_PILING_ON_WITHOUT_MODIFIER, isPilingOnWithoutModifier());
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addBoolean(isOvertime());
    pByteList.addInt(getTurntime());
    pByteList.addBoolean(isSneakyGitAsFoulGuard());
    pByteList.addBoolean(isFoulBonusOutsideTacklezone());
    pByteList.addBoolean(isRightStuffCancelsTackle());
    pByteList.addBoolean(isPilingOnWithoutModifier());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fOvertime = pByteArray.getBoolean();
    fTurntime = pByteArray.getInt();
    fSneakyGitAsFoulGuard = pByteArray.getBoolean();
    fFoulBonusOutsideTacklezone = pByteArray.getBoolean();
    fRightStuffCancelsTackle = pByteArray.getBoolean();
    fPilingOnWithoutModifier = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

}
