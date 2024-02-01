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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Kalimar
 */
public class DialogUseApothecaryParameter implements IDialogParameter {

	private String fPlayerId;
	private PlayerState fPlayerState;
	private SeriousInjury fSeriousInjury;
	private List<ApothecaryType> apothecaryTypes = new ArrayList<>();

	public DialogUseApothecaryParameter() {
		super();
	}

	public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
		this(pPlayerId, pPlayerState, pSeriousInjury, Collections.emptyList());
	}

	public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury, List<ApothecaryType> apothecaryTypes) {
		fPlayerId = pPlayerId;
		fPlayerState = pPlayerState;
		fSeriousInjury = pSeriousInjury;
		this.apothecaryTypes = apothecaryTypes;
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

	public List<ApothecaryType> getApothecaryTypes() {
		return apothecaryTypes;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseApothecaryParameter(getPlayerId(), getPlayerState(), getSeriousInjury(), apothecaryTypes);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		IJsonOption.APOTHECARY_TYPES.addTo(jsonObject, apothecaryTypes.stream().map(ApothecaryType::name).collect(Collectors.toList()));
		return jsonObject;
	}

	public DialogUseApothecaryParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fPlayerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		if (IJsonOption.APOTHECARY_TYPES.isDefinedIn(jsonObject)) {
			apothecaryTypes.addAll(Arrays.stream(IJsonOption.APOTHECARY_TYPES.getFrom(source, jsonObject)).map(ApothecaryType::valueOf).collect(Collectors.toList()));
		}
		return this;
	}

}
