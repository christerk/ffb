package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 *
 * @author Kalimar
 */
public class ClientCommandApothecaryChoice extends ClientCommand {

	private String playerId;
	private boolean choiceNewInjury;

	public ClientCommandApothecaryChoice() {
		super();
	}

	public ClientCommandApothecaryChoice(String playerId, boolean choiceNewInjury) {
		this.playerId = playerId;
		this.choiceNewInjury = choiceNewInjury;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_APOTHECARY_CHOICE;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isChoiceNewInjury() {
		return choiceNewInjury;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.CHOICE_NEW_INJURY.addTo(jsonObject, choiceNewInjury);
		return jsonObject;
	}

	public ClientCommandApothecaryChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		choiceNewInjury = IJsonOption.CHOICE_NEW_INJURY.getFrom(source, jsonObject);
		return this;
	}

}
