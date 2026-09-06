package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReRollPropertyFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Asks the coach whether they want to spend optional roll modifiers or a re-roll to rescue a failed roll.
 */
public class DialogReRollModifierChoiceParameter implements IDialogParameter, HasReRollProperties {

	private String playerId, defaultValueKey;
	private ReRolledAction reRolledAction;
	private int minimumRoll;
	private int roll;
	private boolean fumble;
	private Skill reRollSkill;
	private CommonProperty menuProperty;
	private final List<String> messages = new ArrayList<>();
	private final List<ReRollProperty> reRollProperties = new ArrayList<>();
	private final List<ModifierChoiceOption> modifierOptions = new ArrayList<>();

	public DialogReRollModifierChoiceParameter() {
		super();
	}

	public DialogReRollModifierChoiceParameter(String playerId, ReRolledAction reRolledAction, int minimumRoll,
																						int roll, List<ModifierChoiceOption> modifierOptions,
																						List<ReRollProperty> reRollProperties, boolean fumble, Skill reRollSkill,
																						CommonProperty menuProperty, String defaultValueKey, List<String> messages) {
		this.playerId = playerId;
		this.reRolledAction = reRolledAction;
		this.minimumRoll = minimumRoll;
		this.roll = roll;
		this.fumble = fumble;
		this.reRollSkill = reRollSkill;
		this.menuProperty = menuProperty;
		this.defaultValueKey = defaultValueKey;
		if (modifierOptions != null) {
			this.modifierOptions.addAll(modifierOptions);
		}
		if (reRollProperties != null) {
			this.reRollProperties.addAll(reRollProperties);
		}
		if (messages != null) {
			this.messages.addAll(messages);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_MODIFIER_CHOICE;
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

	public int getRoll() {
		return roll;
	}

	public boolean isFumble() {
		return fumble;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
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

	public List<ModifierChoiceOption> getModifierOptions() {
		return modifierOptions;
	}

	@Override
	public boolean hasProperty(ReRollProperty property) {
		return reRollProperties.contains(property);
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogReRollModifierChoiceParameter(playerId, reRolledAction, minimumRoll, roll, modifierOptions,
			reRollProperties, fumble, reRollSkill, menuProperty, defaultValueKey, messages);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, reRolledAction);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, minimumRoll);
		IJsonOption.ROLL.addTo(jsonObject, roll);
		List<String> properties = reRollProperties.stream().map(ReRollProperty::getName).collect(Collectors.toList());
		IJsonOption.RE_ROLL_PROPERTIES.addTo(jsonObject, properties);
		IJsonOption.FUMBLE.addTo(jsonObject, fumble);
		IJsonOption.SKILL.addTo(jsonObject, reRollSkill);
		IJsonOption.DEFAULT_VALUE_KEY.addTo(jsonObject, defaultValueKey);
		if (menuProperty != null) {
			IJsonOption.MENU_PROPERTY.addTo(jsonObject, menuProperty.getKey());
		}
		IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, messages);
		JsonArray optionArray = new JsonArray();
		modifierOptions.stream().map(ModifierChoiceOption::toJsonValue).forEach(optionArray::add);
		IJsonOption.MODIFIER_OPTIONS.addTo(jsonObject, optionArray);
		return jsonObject;
	}

	public DialogReRollModifierChoiceParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		reRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		minimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fumble = IJsonOption.FUMBLE.getFrom(source, jsonObject);
		reRollSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		menuProperty = CommonProperty.forKey(IJsonOption.MENU_PROPERTY.getFrom(source, jsonObject));
		defaultValueKey = IJsonOption.DEFAULT_VALUE_KEY.getFrom(source, jsonObject);
		messages.clear();
		messages.addAll(Arrays.asList(IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject)));

		ReRollPropertyFactory factory = source.getFactory(FactoryType.Factory.RE_ROLL_PROPERTY);
		reRollProperties.clear();
		reRollProperties.addAll(
			Arrays.stream(IJsonOption.RE_ROLL_PROPERTIES.getFrom(source, jsonObject)).map(factory::forName)
				.collect(Collectors.toList()));

		modifierOptions.clear();
		JsonArray optionArray = IJsonOption.MODIFIER_OPTIONS.getFrom(source, jsonObject);
		if (optionArray != null) {
			for (int i = 0; i < optionArray.size(); i++) {
				modifierOptions.add(new ModifierChoiceOption().initFrom(source, optionArray.get(i)));
			}
		}
		return this;
	}

}
