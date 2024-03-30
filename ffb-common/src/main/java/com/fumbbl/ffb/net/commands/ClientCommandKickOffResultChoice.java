package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.kickoff.bb2020.KickoffResult;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandKickOffResultChoice extends ClientCommand {

	private KickoffResult kickoffResult;

	public ClientCommandKickOffResultChoice() {
	}

	public ClientCommandKickOffResultChoice(KickoffResult kickoffResult) {
		this.kickoffResult = kickoffResult;
	}

	public KickoffResult getKickoffResult() {
		return kickoffResult;
	}

	public void setKickoffResult(KickoffResult kickoffResult) {
		this.kickoffResult = kickoffResult;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.KICKOFF_RESULT.addTo(jsonObject, kickoffResult);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		kickoffResult = (KickoffResult) IJsonOption.KICKOFF_RESULT.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_KICK_OFF_RESULT_CHOICE;
	}
}
