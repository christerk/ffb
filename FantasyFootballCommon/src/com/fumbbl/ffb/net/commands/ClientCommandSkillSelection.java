package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSkillSelection extends ClientCommand {
	private String playerId;
	private Skill skill;

	public ClientCommandSkillSelection() {
	}

	public ClientCommandSkillSelection(String playerId, Skill skill) {
		this.playerId = playerId;
		this.skill = skill;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Skill getSkill() {
		return skill;
	}

	@Override
	public NetCommandId getId() {
		// The class was renamed but this id needs to stay the same in order for replays to work
		return NetCommandId.CLIENT_PRAYER_SELECTION;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.SKILL.addTo(jsonObject, skill);
		return jsonObject;
	}

	public ClientCommandSkillSelection initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}
