package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class DialogBribesParameter implements IDialogParameter {

	private String fTeamId;
	private int fMaxNrOfBribes;
	private final List<String> fPlayerIds;

	public DialogBribesParameter() {
		fPlayerIds = new ArrayList<>();
	}

	public DialogBribesParameter(String pTeamId, int pMaxNrOfBribes) {
		this();
		fTeamId = pTeamId;
		fMaxNrOfBribes = pMaxNrOfBribes;
	}

	public DialogId getId() {
		return DialogId.BRIBES;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getMaxNrOfBribes() {
		return fMaxNrOfBribes;
	}

	public void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIds.add(pPlayerId);
		}
	}

	public void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerId(playerId);
			}
		}
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[fPlayerIds.size()]);
	}

	// transformation

	public IDialogParameter transform() {
		DialogBribesParameter transformedParameter = new DialogBribesParameter(getTeamId(), getMaxNrOfBribes());
		transformedParameter.addPlayerIds(getPlayerIds());
		return transformedParameter;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.MAX_NR_OF_BRIBES.addTo(jsonObject, fMaxNrOfBribes);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, getPlayerIds());
		return jsonObject;
	}

	public DialogBribesParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fMaxNrOfBribes = IJsonOption.MAX_NR_OF_BRIBES.getFrom(source, jsonObject);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		return this;
	}

}
