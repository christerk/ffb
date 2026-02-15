package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PositionChoiceMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogSelectPositionParameter implements IDialogParameter {

	private final List<String> positions = new ArrayList<>();
	private PositionChoiceMode positionChoiceMode;
	private int minSelect, maxSelect;
	private String teamId;

	public DialogSelectPositionParameter() {
	}

	public DialogSelectPositionParameter(List<String> positions, PositionChoiceMode positionChoiceMode,
		int minSelect, int maxSelect, String teamId) {
		this.minSelect = minSelect;
		this.maxSelect = maxSelect;
		this.teamId = teamId;
		this.positions.addAll(positions);
		this.positionChoiceMode = positionChoiceMode;
	}

	@Override
	public DialogId getId() {
		return DialogId.SELECT_POSITION;
	}

	public List<String> getPositions() {
		return positions;
	}

	public PositionChoiceMode getPositionChoiceMode() {
		return positionChoiceMode;
	}

	public int getMinSelect() {
		return minSelect;
	}

	public int getMaxSelect() {
		return maxSelect;
	}

	public String getTeamId() {
		return teamId;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectPositionParameter( positions, positionChoiceMode, 1, 1, teamId);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		positions.addAll(Arrays.asList(IJsonOption.POSITION_IDS.getFrom(source, jsonObject)));
		positionChoiceMode = PositionChoiceMode.valueOf(IJsonOption.POSITION_CHOICE_MODE.getFrom(source, jsonObject));
		minSelect = IJsonOption.MIN_SELECTS.getFrom(source, jsonObject);
		maxSelect = IJsonOption.MAX_SELECTS.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.POSITION_IDS.addTo(jsonObject, positions);
		IJsonOption.POSITION_CHOICE_MODE.addTo(jsonObject, positionChoiceMode.name());
		IJsonOption.MIN_SELECTS.addTo(jsonObject, minSelect);
		IJsonOption.MAX_SELECTS.addTo(jsonObject, maxSelect);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}
}
