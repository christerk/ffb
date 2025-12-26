package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DialogReRollPropertiesParameter implements IDialogParameter, HasReRollProperties {

	private String playerId, defaultValueKey;
	private ReRolledAction reRolledAction;
	private int minimumRoll;
	private boolean fumble;
	private Skill reRollSkill, modifyingSkill;
	private final List<String> messages = new ArrayList<>();
	private final List<ReRollProperty> reRollProperties = new ArrayList<>();

	private CommonProperty menuProperty;

	public DialogReRollPropertiesParameter() {
		super();
	}

	public DialogReRollPropertiesParameter(String playerId, ReRolledAction reRolledAction, int minimumRoll,
		List<ReRollProperty> reRollProperties, boolean fumble, Skill reRollSkill, Skill modifyingSkill,
		CommonProperty menuProperty, String defaultValueKey, List<String> messages) {

		this.playerId = playerId;
		this.reRolledAction = reRolledAction;
		this.minimumRoll = minimumRoll;
		this.fumble = fumble;
		this.reRollSkill = reRollSkill;
		this.modifyingSkill = modifyingSkill;
		this.menuProperty = menuProperty;
		this.defaultValueKey = defaultValueKey;
		if (messages != null) {
			this.messages.addAll(messages);
		}
		if (reRollProperties != null) {
			this.reRollProperties.addAll(reRollProperties);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_PROPERTIES;
	}

	public String getPlayerId() {
		return playerId;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public boolean isFumble() {
		return fumble;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public Skill getModifyingSkill() {
		return modifyingSkill;
	}

	public CommonProperty getMenuProperty() {
		return menuProperty;
	}

	public String getDefaultValueKey() {
		return defaultValueKey;
	}

	public List<String> getMessages() {
		return messages;
	}

	@Override
	public boolean hasProperty(ReRollProperty property) {
		return reRollProperties.contains(property);
	}
// transformation

	public IDialogParameter transform() {
		return new DialogReRollPropertiesParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(),
			reRollProperties, isFumble(), reRollSkill, modifyingSkill, menuProperty, defaultValueKey, messages);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		List<String> properties = reRollProperties.stream().map(ReRollProperty::getName).collect(Collectors.toList());
		IJsonOption.RE_ROLL_PROPERTIES.addTo(jsonObject, properties);
		IJsonOption.FUMBLE.addTo(jsonObject, fumble);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		IJsonOption.MODIFYING_SKILL.addTo(jsonObject, modifyingSkill);
		IJsonOption.DEFAULT_VALUE_KEY.addTo(jsonObject, defaultValueKey);
		if (menuProperty != null) {
			IJsonOption.MENU_PROPERTY.addTo(jsonObject, menuProperty.getKey());
		}
		IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, messages);
		return jsonObject;
	}

	public DialogReRollPropertiesParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		minimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		fumble = IJsonOption.FUMBLE.getFrom(source, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		modifyingSkill = (Skill) IJsonOption.MODIFYING_SKILL.getFrom(source, jsonObject);
		menuProperty = CommonProperty.forKey(IJsonOption.MENU_PROPERTY.getFrom(source, jsonObject));
		defaultValueKey = IJsonOption.DEFAULT_VALUE_KEY.getFrom(source, jsonObject);
		messages.addAll(Arrays.asList(IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject)));

		ReRollPropertyFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_PROPERTY);

		reRollProperties.addAll(
			Arrays.stream(IJsonOption.RE_ROLL_PROPERTIES.getFrom(source, jsonObject)).map(factory::forName).collect(
				Collectors.toList()));
		return this;
	}

}
