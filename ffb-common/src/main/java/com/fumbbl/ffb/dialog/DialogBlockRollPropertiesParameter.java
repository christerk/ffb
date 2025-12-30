package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogBlockRollPropertiesParameter implements IDialogParameter, HasReRollProperties {

	private String choosingTeamId;
	private int nrOfDice;
	private int[] blockRoll;
	private final List<ReRollProperty> reRollProperties = new ArrayList<>();
	private final Map<String, String> rrActionToSource = new HashMap<>();

	@SuppressWarnings("unused")
	public DialogBlockRollPropertiesParameter() {
		super();
	}

	public DialogBlockRollPropertiesParameter(String choosingTeamId, int nrOfDice, int[] blockRoll, List<ReRollProperty> reRollProperties, Map<String, String> rrActionToSource) {
		this.choosingTeamId = choosingTeamId;
		this.nrOfDice = nrOfDice;
		this.blockRoll = blockRoll;
		if (reRollProperties != null) {
			this.reRollProperties.addAll(reRollProperties);
		}
		if (rrActionToSource != null) {
			this.rrActionToSource.putAll(rrActionToSource);
		}
	}

	public DialogId getId() {
		return DialogId.BLOCK_ROLL_PROPERTIES;
	}

	public String getChoosingTeamId() {
		return choosingTeamId;
	}

	public int getNrOfDice() {
		return nrOfDice;
	}

	public int[] getBlockRoll() {
		return blockRoll;
	}

	@Override
	public boolean hasProperty(ReRollProperty property) {
		return reRollProperties.contains(property);
	}

	public boolean hasActualReRoll() {
		return reRollProperties.stream().anyMatch(ReRollProperty::isActualReRoll) || !rrActionToSource.isEmpty();
	}

	public List<ReRollProperty> getReRollProperties() {
		return reRollProperties;
	}

	public Map<String, String> getRrActionToSource() {
		return rrActionToSource;
	}

// transformation

	public IDialogParameter transform() {
		return new DialogBlockRollPropertiesParameter(getChoosingTeamId(), getNrOfDice(), getBlockRoll(), reRollProperties,
			rrActionToSource);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, choosingTeamId);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, nrOfDice);
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, blockRoll);
		List<String> properties = reRollProperties.stream().map(ReRollProperty::getName).collect(Collectors.toList());
		IJsonOption.RE_ROLL_PROPERTIES.addTo(jsonObject, properties);
		IJsonOption.RE_ROLL_ACTION_TO_SOURCE_MAP.addTo(jsonObject, rrActionToSource);
		return jsonObject;
	}

	public DialogBlockRollPropertiesParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		choosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(source, jsonObject);
		nrOfDice = IJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		blockRoll = IJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);

		ReRollPropertyFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_PROPERTY);

		reRollProperties.addAll(
			Arrays.stream(IJsonOption.RE_ROLL_PROPERTIES.getFrom(source, jsonObject)).map(factory::forName).collect(
				Collectors.toList()));

		rrActionToSource.putAll(IJsonOption.RE_ROLL_ACTION_TO_SOURCE_MAP.getFrom(source, jsonObject));
		return this;
	}

}
