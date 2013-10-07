package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.SoundFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.ModelChangeList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandModelSync extends ServerCommand {

  private static final String _XML_TAG_SOUND = "sound";
  private static final String _XML_TAG_TIME = "time";
  private static final String _XML_ATTRIBUTE_GAME = "game";
  private static final String _XML_ATTRIBUTE_TURN = "turn";

  private ModelChangeList fModelChanges;
  private ReportList fReportList;
  private Animation fAnimation;
  private Sound fSound;
  private long fGameTime;
  private long fTurnTime;

  public ServerCommandModelSync() {
    fModelChanges = new ModelChangeList();
    fReportList = new ReportList();
  }

  public ServerCommandModelSync(ModelChangeList pModelChanges, ReportList pReportList, Animation pAnimation, Sound pSound, long pGameTime, long pTurnTime) {
    this();
    fModelChanges.add(pModelChanges);
    fReportList.add(pReportList);
    fAnimation = pAnimation;
    fSound = pSound;
    fGameTime = pGameTime;
    fTurnTime = pTurnTime;
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_MODEL_SYNC;
  }

  public ModelChangeList getModelChanges() {
    return fModelChanges;
  }

  public ReportList getReportList() {
    return fReportList;
  }

  public Animation getAnimation() {
    return fAnimation;
  }

  public Sound getSound() {
    return fSound;
  }

  public long getGameTime() {
    return fGameTime;
  }

  public long getTurnTime() {
    return fTurnTime;
  }

  // transformation

  public ServerCommandModelSync transform() {
    Animation transformedAnimation = (getAnimation() != null) ? getAnimation().transform() : null;
    ServerCommandModelSync transformedCommand = new ServerCommandModelSync(
      getModelChanges().transform(),
      getReportList().transform(),
      transformedAnimation,
      getSound(),
      getGameTime(),
      getTurnTime()
    );
    transformedCommand.setCommandNr(getCommandNr());
    return transformedCommand;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    getModelChanges().addToXml(pHandler);
    getReportList().addToXml(pHandler);
    if (getAnimation() != null) {
      getAnimation().addToXml(pHandler);
    }
    if (getSound() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_SOUND, getSound().getName());
    }
    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME, getGameTime());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TURN, getTurnTime());
    UtilXml.addEmptyElement(pHandler, _XML_TAG_TIME, attributes);
    UtilXml.endElement(pHandler, getId().getName());
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
    getModelChanges().addTo(pByteList);
    getReportList().addTo(pByteList);
    pByteList.addBoolean(getAnimation() != null);
    if (getAnimation() != null) {
      getAnimation().addTo(pByteList);
    }
    pByteList.addByte((byte) ((getSound() != null) ? getSound().getId() : 0));
    pByteList.addLong(getGameTime());
    pByteList.addLong(getTurnTime());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());

    fModelChanges.initFrom(pByteArray);
    // System.out.print("ModelChanges:");
    // for (IModelChange modelChange : fModelChanges.getChanges()) {
    // System.out.print(" " + modelChange.getId().getName());
    // }
    // System.out.println();

    fReportList.initFrom(pByteArray);
    // System.out.print("     Reports:");
    // for (IReport report : fReportList.getReports()) {
    // System.out.print(" " + report.getId().getName());
    // }
    // System.out.println();

    if (pByteArray.getBoolean()) {
      fAnimation = new Animation();
      fAnimation.initFrom(pByteArray);
    }

    fSound = new SoundFactory().forId(pByteArray.getByte());
    fGameTime = pByteArray.getLong();
    fTurnTime = pByteArray.getLong();

    return byteArraySerializationVersion;

  }

}
