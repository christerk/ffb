package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandUploadGame extends InternalServerCommand {

	private String fConcedingTeamId;
	
  public InternalServerCommandUploadGame(long pGameId) {
    this(pGameId, null);
  }

  public InternalServerCommandUploadGame(long pGameId, String pConcedingTeamId) {
    super(pGameId);
    fConcedingTeamId = pConcedingTeamId;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_UPLOAD_GAME;
  }
  
  public String getConcedingTeamId() {
	  return fConcedingTeamId;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addString(fConcedingTeamId);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fConcedingTeamId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.CONCEDING_TEAM_ID.addTo(jsonObject, fConcedingTeamId);
    return jsonObject;
  }

  public InternalServerCommandUploadGame initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fConcedingTeamId = IJsonOption.CONCEDING_TEAM_ID.getFrom(jsonObject);
    return this;
  }
  
}
