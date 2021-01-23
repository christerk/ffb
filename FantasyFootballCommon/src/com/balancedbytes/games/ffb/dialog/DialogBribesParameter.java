package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class DialogBribesParameter implements IDialogParameter {

	private String fTeamId;
	private int fMaxNrOfBribes;
	private List<String> fPlayerIds;

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

	public DialogBribesParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fMaxNrOfBribes = IJsonOption.MAX_NR_OF_BRIBES.getFrom(game, jsonObject);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		return this;
	}

}
