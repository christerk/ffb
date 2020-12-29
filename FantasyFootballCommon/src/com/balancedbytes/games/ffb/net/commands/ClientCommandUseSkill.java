package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
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
	private String playerId;

	public ClientCommandUseSkill() {
		super();
	}

	public ClientCommandUseSkill(Skill pSkill, boolean pSkillUsed, String playerId) {
		fSkill = pSkill;
		fSkillUsed = pSkillUsed;
		this.playerId = playerId;
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

	public String getPlayerId() {
		return playerId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.SKILL_USED.addTo(jsonObject, fSkillUsed);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	public ClientCommandUseSkill initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		fSkillUsed = IJsonOption.SKILL_USED.getFrom(game, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}
}
