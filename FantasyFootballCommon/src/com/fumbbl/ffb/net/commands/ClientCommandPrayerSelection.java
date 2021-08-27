package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandPrayerSelection extends ClientCommand {
	private String playerId;
	private Skill skill;

	public ClientCommandPrayerSelection() {
	}

	public ClientCommandPrayerSelection(String playerId, Skill skill) {
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
		return NetCommandId.CLIENT_PRAYER_SELECTION;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.SKILL.addTo(jsonObject, skill);
		return jsonObject;
	}

	public ClientCommandPrayerSelection initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		skill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		return this;
	}

}
