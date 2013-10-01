package com.balancedbytes.games.ffb.server.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandJoinApproved extends InternalServerCommand {
  
  private static final String _XML_ATTRIBUTE_GAME_NAME = "gameName";
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_CLIENT_MODE = "clientMode";
	
  private String fCoach;
  private String fGameName;
  private ClientMode fClientMode;
  private String fTeamId;
  
  public InternalServerCommandJoinApproved(long pGameId, String pGameName, String pCoach, String pTeamId, ClientMode pClientMode) {
    super(pGameId);
    fGameName = pGameName;
    fCoach = pCoach;
    fTeamId = pTeamId;
    fClientMode = pClientMode;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_JOIN_APPROVED;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String getGameName() {
    return fGameName;
  }
  
  public ClientMode getClientMode() {
    return fClientMode;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getGameId() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_GAME_ID, getGameId());
    }
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_NAME, getGameName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    if (getClientMode() != null) {
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CLIENT_MODE, getClientMode().getName());
    }
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
 
}
