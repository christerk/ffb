package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandTalk extends ServerCommand {
  
  private String fCoach;
  private List<String> fTalks;
  
  public ServerCommandTalk() {
    fTalks = new ArrayList<String>();
  }
  
  public ServerCommandTalk(String pCoach, String pTalk) {
    this();
    fCoach = pCoach;
    addTalk(pTalk);
  }
  
  public ServerCommandTalk(String pCoach, String[] pTalk) {
    this();
    fCoach = pCoach;
    addTalks(pTalk);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_TALK;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public void addTalk(String pTalk) {
    if (StringTool.isProvided(pTalk)) {
      fTalks.add(pTalk);
    }
  }
  
  public void addTalks(String[] pTalk) {
    if (ArrayTool.isProvided(pTalk)) {
      for (String talk : pTalk) {
        addTalk(talk);
      }
    }
  }

  public String[] getTalks() {
    return fTalks.toArray(new String[fTalks.size()]);
  }
  
  public boolean isReplayable() {
    return false;
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
    pByteList.addString(getCoach());
    pByteList.addStringArray(getTalks());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fCoach = pByteArray.getString();
    addTalks(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.TALKS.addTo(jsonObject, fTalks);
    return jsonObject;
  }
  
  public ServerCommandTalk initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    addTalks(IJsonOption.TALKS.getFrom(jsonObject));
    return this;
  }
    
}
