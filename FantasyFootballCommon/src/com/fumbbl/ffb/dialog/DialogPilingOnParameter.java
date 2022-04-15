package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogPilingOnParameter implements IDialogParameter {

	private String fPlayerId;
	private boolean fReRollInjury;
	private boolean fUsesATeamReroll;

	public DialogPilingOnParameter() {
		super();
	}

	public DialogPilingOnParameter(String playerId, boolean reRollInjury, boolean usesATeamReroll) {
		fPlayerId = playerId;
		fReRollInjury = reRollInjury;
		fUsesATeamReroll = usesATeamReroll;
	}

	public DialogId getId() {
		return DialogId.PILING_ON;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isReRollInjury() {
		return fReRollInjury;
	}

	public boolean isUsesATeamReroll() {
		return fUsesATeamReroll;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogPilingOnParameter(getPlayerId(), isReRollInjury(), isUsesATeamReroll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.RE_ROLL_INJURY.addTo(jsonObject, fReRollInjury);
		IJsonOption.USES_A_TEAM_REROLL.addTo(jsonObject, fUsesATeamReroll);
		return jsonObject;
	}

	public DialogPilingOnParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fReRollInjury = IJsonOption.RE_ROLL_INJURY.getFrom(source, jsonObject);
		Boolean usesATeamReroll = IJsonOption.USES_A_TEAM_REROLL.getFrom(source, jsonObject);
		fUsesATeamReroll = (usesATeamReroll != null) ? usesATeamReroll : false;
		return this;
	}

}
