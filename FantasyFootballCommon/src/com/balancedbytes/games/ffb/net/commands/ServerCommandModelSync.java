package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.SoundFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.model.change.old.ModelChangeListOld;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.report.ReportList;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandModelSync extends ServerCommand {

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

  // ByteArray serialization

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    
    // TODO: this needs to init the OLD ModelChangeList and convert it

    ModelChangeListOld changeListOld = new ModelChangeListOld();
    changeListOld.initFrom(pByteArray);
    fModelChanges = changeListOld.convert();
    
    fReportList.initFrom(pByteArray);

    if (pByteArray.getBoolean()) {
      fAnimation = new Animation();
      fAnimation.initFrom(pByteArray);
    }

    fSound = new SoundFactory().forId(pByteArray.getByte());
    fGameTime = pByteArray.getLong();
    fTurnTime = pByteArray.getLong();

    return byteArraySerializationVersion;

  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    if (fModelChanges != null) {
      IJsonOption.MODEL_CHANGE_LIST.addTo(jsonObject, fModelChanges.toJsonValue());
    }
    if (fReportList != null) {
      IJsonOption.REPORT_LIST.addTo(jsonObject, fReportList.toJsonValue());
    }
    if (fAnimation != null) {
      IJsonOption.ANIMATION.addTo(jsonObject, fAnimation.toJsonValue());
    }
    IJsonOption.SOUND.addTo(jsonObject, fSound);
    IJsonOption.GAME_TIME.addTo(jsonObject, fGameTime);
    IJsonOption.TURN_TIME.addTo(jsonObject, fTurnTime);
    return jsonObject;
  }
  
  public ServerCommandModelSync initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    JsonObject modelChangeListObject = IJsonOption.MODEL_CHANGE_LIST.getFrom(jsonObject);
    fModelChanges = new ModelChangeList();
    if (modelChangeListObject != null) {
      fModelChanges.initFrom(modelChangeListObject);
    }
    fReportList = new ReportList();
    JsonObject reportListObject = IJsonOption.REPORT_LIST.getFrom(jsonObject);
    if (reportListObject != null) {
      fReportList.initFrom(reportListObject);
    }
    fAnimation = null;
    JsonObject animationObject = IJsonOption.ANIMATION.getFrom(jsonObject);
    if (animationObject != null) {
      fAnimation = new Animation().initFrom(animationObject);
    }
    fSound = (Sound) IJsonOption.SOUND.getFrom(jsonObject);
    fGameTime = IJsonOption.GAME_TIME.getFrom(jsonObject);
    fTurnTime = IJsonOption.TURN_TIME.getFrom(jsonObject);
    return this;
  }

}
