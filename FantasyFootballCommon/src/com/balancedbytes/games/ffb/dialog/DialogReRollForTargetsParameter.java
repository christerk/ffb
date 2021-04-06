package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogReRollForTargetsParameter implements IDialogParameter {

	private List<String> targetIds = new ArrayList<>();
	private ReRolledAction reRolledAction;
	private boolean teamReRollAvailable;
	private boolean proReRollAvailable;

	public DialogReRollForTargetsParameter() {
		super();
	}

	public DialogReRollForTargetsParameter(List<String> targetIds, ReRolledAction reRolledAction,
	                                       boolean teamReRollAvailable, boolean proReRollAvailable) {
		this.targetIds = targetIds;
		this.reRolledAction = reRolledAction;
		this.teamReRollAvailable = teamReRollAvailable;
		this.proReRollAvailable = proReRollAvailable;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS;
	}

	public List<String> getTargetIds() {
		return targetIds;
	}

	public boolean isTeamReRollAvailable() {
		return teamReRollAvailable;
	}

	public boolean isProReRollAvailable() {
		return proReRollAvailable;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollForTargetsParameter(getTargetIds(), getReRolledAction(), isTeamReRollAvailable(),
				isProReRollAvailable());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targetIds);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		return jsonObject;
	}

	public DialogReRollForTargetsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		targetIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(game, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		return this;
	}

}
