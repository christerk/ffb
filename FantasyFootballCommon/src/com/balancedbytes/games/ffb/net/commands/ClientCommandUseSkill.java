package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandUseSkill extends ClientCommand {
  
  private Skill fSkill;
  private boolean fSkillUsed;
  
  public ClientCommandUseSkill() {
    super();
  }

  public ClientCommandUseSkill(Skill pSkill, boolean pSkillUsed) {
    fSkill = pSkill;
    fSkillUsed = pSkillUsed;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_USE_SKILL;
  }
  
  public boolean isSkillUsed() {
    return fSkillUsed;
  }
  
  public Skill getSkill() {
    return fSkill;
  }
   
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.SKILL.addTo(jsonObject, fSkill);
    IJsonOption.SKILL_USED.addTo(jsonObject, fSkillUsed);
    return jsonObject;
  }
  
  public ClientCommandUseSkill initFrom(JsonValue jsonValue) {
    super.initFrom(jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fSkill = (Skill) IJsonOption.SKILL.getFrom(jsonObject);
    fSkillUsed = IJsonOption.SKILL_USED.getFrom(jsonObject);
    return this;
  }
    
}
