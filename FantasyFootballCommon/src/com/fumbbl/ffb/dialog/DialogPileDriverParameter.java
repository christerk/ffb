package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DialogPileDriverParameter implements IDialogParameter {
	private final List<String> knockedDownPlayers = new ArrayList<>();
	private String teamId;

	public DialogPileDriverParameter() {
	}

	public DialogPileDriverParameter(String teamId, List<String> knockedDownPlayers) {
		this.teamId = teamId;
		this.knockedDownPlayers.addAll(knockedDownPlayers);
	}

	@Override
	public DialogId getId() {
		return DialogId.PILE_DRIVER;
	}

	public List<String> getKnockedDownPlayers() {
		return knockedDownPlayers;
	}

	public String getTeamId() {
		return teamId;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogPileDriverParameter(teamId, knockedDownPlayers);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		knockedDownPlayers.addAll(Arrays.stream(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject)).collect(Collectors.toList()));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, knockedDownPlayers);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}
}
