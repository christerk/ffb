package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogReRollParameter implements IDialogParameter {

	private String fPlayerId;
	private ReRolledAction fReRolledAction;
	private int fMinimumRoll;
	private boolean fTeamReRollOption;
	private boolean fProReRollOption;
	private boolean fFumble;

	public DialogReRollParameter() {
		super();
	}

	public DialogReRollParameter(String pPlayerId, ReRolledAction pReRolledAction, int pMinimumRoll,
			boolean pTeamReRollOption, boolean pProReRollOption, boolean pFumble) {
		fPlayerId = pPlayerId;
		fReRolledAction = pReRolledAction;
		fMinimumRoll = pMinimumRoll;
		fTeamReRollOption = pTeamReRollOption;
		fProReRollOption = pProReRollOption;
		fFumble = pFumble;
	}

	public DialogId getId() {
		return DialogId.RE_ROLL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	public boolean isTeamReRollOption() {
		return fTeamReRollOption;
	}

	public boolean isProReRollOption() {
		return fProReRollOption;
	}

	public boolean isFumble() {
		return fFumble;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(), isTeamReRollOption(),
				isProReRollOption(), isFumble());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
		IJsonOption.FUMBLE.addTo(jsonObject, fFumble);
		return jsonObject;
	}

	public DialogReRollParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		fFumble = IJsonOption.FUMBLE.getFrom(game, jsonObject);
		return this;
	}

}
