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
 * @author Kalimar
 */
public class DialogArgueTheCallParameter implements IDialogParameter {

	private String fTeamId;
	private final List<String> fPlayerIds;
	private boolean stayOnPitch, friendsWithTheRef;

	private int biasedRefs;

	public DialogArgueTheCallParameter() {
		fPlayerIds = new ArrayList<>();
	}

	public DialogArgueTheCallParameter(String teamId, boolean stayOnPitch, boolean friendsWithTheRef, int biasedRefs) {
		this();
		setTeamId(teamId);
		this.stayOnPitch = stayOnPitch;
		this.friendsWithTheRef = friendsWithTheRef;
		this.biasedRefs = biasedRefs;
	}

	public DialogId getId() {
		return DialogId.ARGUE_THE_CALL;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public void setTeamId(String teamId) {
		fTeamId = teamId;
	}

	public boolean isStayOnPitch() {
		return stayOnPitch;
	}

	public boolean isFriendsWithTheRef() {
		return friendsWithTheRef;
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
		return fPlayerIds.toArray(new String[0]);
	}

	public int getBiasedRefs() {
		return biasedRefs;
	}
// transformation

	public IDialogParameter transform() {
		DialogArgueTheCallParameter transformedParameter = new DialogArgueTheCallParameter(getTeamId(), stayOnPitch, friendsWithTheRef, biasedRefs);
		transformedParameter.addPlayerIds(getPlayerIds());
		return transformedParameter;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, getTeamId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, getPlayerIds());
		IJsonOption.STAYS_ON_PITCH.addTo(jsonObject, stayOnPitch);
		IJsonOption.FRIENDS_WITH_REF.addTo(jsonObject, friendsWithTheRef);
		IJsonOption.BIASED_REFS.addTo(jsonObject, biasedRefs);
		return jsonObject;
	}

	public DialogArgueTheCallParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		setTeamId(IJsonOption.TEAM_ID.getFrom(source, jsonObject));
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		Boolean stayOnPitchValue = IJsonOption.STAYS_ON_PITCH.getFrom(source, jsonObject);
		stayOnPitch = stayOnPitchValue != null && stayOnPitchValue;
		Boolean friendsWithTheRefValue = IJsonOption.FRIENDS_WITH_REF.getFrom(source, jsonObject);
		friendsWithTheRef = friendsWithTheRefValue != null && friendsWithTheRefValue;
		biasedRefs = IJsonOption.BIASED_REFS.getFrom(source, jsonObject);
		return this;
	}

}
