package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.balancedbytes.games.ffb.server.admin.IAdminGameIdListener;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandFumbblTeamLoaded extends InternalServerCommand {
  
  private String fCoach;
  private boolean fHomeTeam;
  private transient IAdminGameIdListener fAdminGameIdListener;

  public InternalServerCommandFumbblTeamLoaded(long pGameId, String pCoach, boolean pHomeTeam) {
    super(pGameId);
    fCoach = pCoach;
    fHomeTeam = pHomeTeam;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_FUMBBL_TEAM_LOADED;
  }
  
  public String getCoach() {
    return fCoach;
  }
    
  public boolean isHomeTeam() {
    return fHomeTeam;
  }
  
  public void setAdminGameIdListener(IAdminGameIdListener pAdminGameIdListener) {
	  fAdminGameIdListener = pAdminGameIdListener;
  }
 
  public IAdminGameIdListener getAdminGameIdListener() {
	  return fAdminGameIdListener;
  }
  
  // ByteArray serialization
  
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addString(fCoach);
    pByteList.addBoolean(fHomeTeam);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fCoach = pByteArray.getString();
    fHomeTeam = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
    return jsonObject;
  }

  public InternalServerCommandFumbblTeamLoaded initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fHomeTeam = IJsonOption.HOME_TEAM.getFrom(jsonObject);
    return this;
  }
  
}
