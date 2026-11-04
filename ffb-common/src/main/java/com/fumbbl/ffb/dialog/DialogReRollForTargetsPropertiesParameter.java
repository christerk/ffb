package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogReRollForTargetsPropertiesParameter implements IDialogParameter, HasReRollProperties {

	private String playerId;
	private List<String> targetIds = new ArrayList<>();
	private Map<String, Integer> minimumRolls = new HashMap<>();
	private ReRolledAction reRolledAction;
	private List<String> reRollAvailableAgainst = new ArrayList<>();
	private boolean proReRollAvailable, teamReRollAvailable, consummateAvailable;
	private Skill reRollSkill;
	private ReRollSource singleUseReRollSource;
	private List<ReRollProperty> reRollProperties;

	public DialogReRollForTargetsPropertiesParameter() {
		super();
	}

	public DialogReRollForTargetsPropertiesParameter(String playerId, List<String> targetIds,
		ReRolledAction reRolledAction, Map<String, Integer> minimumRolls, List<String> reRollAvailableAgainst,
		boolean proReRollAvailable, boolean teamReRollAvailable, Skill reRollSkill,
		ReRollSource singleUseReRollSource, boolean consummateAvailable, List<ReRollProperty> reRollProperties) {
		this.playerId = playerId;
		this.targetIds = targetIds;
		this.reRolledAction = reRolledAction;
		this.minimumRolls = minimumRolls;
		this.reRollAvailableAgainst = reRollAvailableAgainst;
		this.proReRollAvailable = proReRollAvailable;
		this.teamReRollAvailable = teamReRollAvailable;
		this.reRollSkill = reRollSkill;
		this.singleUseReRollSource = singleUseReRollSource;
		this.consummateAvailable = consummateAvailable;
		this.reRollProperties = reRollProperties;
	}

	@Override
	public DialogId getId() {
		return DialogId.RE_ROLL_FOR_TARGETS_PROPERTIES;
	}

	public List<String> getTargetIds() {
		return targetIds;
	}

	public List<String> getReRollAvailableAgainst() {
		return reRollAvailableAgainst;
	}

	public boolean isProReRollAvailable() {
		return proReRollAvailable;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	public Map<String, Integer> getMinimumRolls() {
		return minimumRolls;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isTeamReRollAvailable() {
		return teamReRollAvailable;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public ReRollSource getSingleUseReRollSource() {
		return singleUseReRollSource;
	}

	public boolean isConsummateAvailable() {
		return consummateAvailable;
	}

	@Override
	public boolean hasProperty(ReRollProperty property) {
		return reRollProperties != null && reRollProperties.contains(property);
	}

	@Override
	public IDialogParameter transform() {
		return new DialogReRollForTargetsPropertiesParameter(getPlayerId(), getTargetIds(), getReRolledAction(),
			getMinimumRolls(), getReRollAvailableAgainst(), isProReRollAvailable(), teamReRollAvailable, reRollSkill,
			singleUseReRollSource, consummateAvailable, reRollProperties);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targetIds);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.MINIMUM_ROLLS.addTo(jsonObject, minimumRolls);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.CONSUMMATE_OPTION.addTo(jsonObject, consummateAvailable);
		if (reRollProperties != null) {
			List<String> properties = reRollProperties.stream().map(ReRollProperty::getName).collect(Collectors.toList());
			IJsonOption.RE_ROLL_PROPERTIES.addTo(jsonObject, properties);
		}
		return jsonObject;
	}

	@Override
	public DialogReRollForTargetsPropertiesParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		targetIds = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(source, jsonObject));
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(source, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(source, jsonObject);
		minimumRolls = IJsonOption.MINIMUM_ROLLS.getFrom(source, jsonObject);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.getFrom(source, jsonObject);
		if (IJsonOption.CONSUMMATE_OPTION.isDefinedIn(jsonObject)) {
			consummateAvailable = IJsonOption.CONSUMMATE_OPTION.getFrom(source, jsonObject);
		}
		if (IJsonOption.RE_ROLL_PROPERTIES.isDefinedIn(jsonObject)) {
			ReRollPropertyFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_PROPERTY);
			reRollProperties = Arrays.stream(IJsonOption.RE_ROLL_PROPERTIES.getFrom(source, jsonObject))
				.map(factory::forName).collect(Collectors.toList());
		}
		return this;
	}
}
