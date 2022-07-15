package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogUseApothecaryParameter implements IDialogParameter {

	private String fPlayerId;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury;
	private ApothecaryType apothecaryType;

	public DialogUseApothecaryParameter() {
		super();
	}

	public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury, ApothecaryType apothecaryType) {
		fPlayerId = pPlayerId;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
		this.apothecaryType = apothecaryType;
	}

	public DialogId getId() {
		return DialogId.USE_APOTHECARY;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public PlayerState getPlayerState() {
		return fPlayerState;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	public ApothecaryType getApothecaryType() {
		return apothecaryType;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseApothecaryParameter(getPlayerId(), getPlayerState(), getSeriousInjury(), apothecaryType);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		if (apothecaryType != null) {
			IJsonOption.APOTHECARY_TYPE.addTo(jsonObject, apothecaryType.name());
		}
		return jsonObject;
	}

	public DialogUseApothecaryParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		if (IJsonOption.APOTHECARY_TYPE.isDefinedIn(jsonObject)) {
			apothecaryType = ApothecaryType.valueOf(IJsonOption.APOTHECARY_TYPE.getFrom(source, jsonObject));
		}
		return this;
	}

}
