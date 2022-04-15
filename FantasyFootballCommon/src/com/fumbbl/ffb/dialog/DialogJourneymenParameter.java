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
public class DialogJourneymenParameter implements IDialogParameter {

	private String fTeamId;
	private int fNrOfSlots;
	private final List<String> fPositionIds;

	public DialogJourneymenParameter() {
		fPositionIds = new ArrayList<>();
	}

	public DialogJourneymenParameter(String pTeamId, int pNrOfSlots, String[] pPositionIds) {
		this();
		fTeamId = pTeamId;
		fNrOfSlots = pNrOfSlots;
		addPositionIds(pPositionIds);
	}

	public DialogId getId() {
		return DialogId.JOURNEYMEN;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getNrOfSlots() {
		return fNrOfSlots;
	}

	private void addPositionId(String pPositionId) {
		if (StringTool.isProvided(pPositionId)) {
			fPositionIds.add(pPositionId);
		}
	}

	private void addPositionIds(String[] pPositionIds) {
		if (ArrayTool.isProvided(pPositionIds)) {
			for (String positionId : pPositionIds) {
				addPositionId(positionId);
			}
		}
	}

	public String[] getPositionIds() {
		return fPositionIds.toArray(new String[fPositionIds.size()]);
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogJourneymenParameter(getTeamId(), getNrOfSlots(), getPositionIds());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.NR_OF_SLOTS.addTo(jsonObject, fNrOfSlots);
		IJsonOption.POSITION_IDS.addTo(jsonObject, getPositionIds());
		return jsonObject;
	}

	public DialogJourneymenParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fNrOfSlots = IJsonOption.NR_OF_SLOTS.getFrom(source, jsonObject);
		addPositionIds(IJsonOption.POSITION_IDS.getFrom(source, jsonObject));
		return this;
	}

}
