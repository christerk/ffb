package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandBlockOrReRollChoiceForTarget extends ClientCommand {
	private String targetId;
	private int selectedIndex = -1, proIndex;
	private ReRollSource reRollSource;

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET;
	}

	public ClientCommandBlockOrReRollChoiceForTarget() {
	}

	public ClientCommandBlockOrReRollChoiceForTarget(String targetId, int selectedIndex, int proIndex, ReRollSource reRollSource) {
		this.targetId = targetId;
		this.selectedIndex = selectedIndex;
		this.proIndex = proIndex;
		this.reRollSource = reRollSource;
	}

	public String getTargetId() {
		return targetId;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public int getProIndex() {
		return proIndex;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		IJsonOption.DICE_INDEX.addTo(jsonObject, selectedIndex);
		IJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		return jsonObject;
	}

	@Override
	public ClientCommandBlockOrReRollChoiceForTarget initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
		targetId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		selectedIndex = IJsonOption.DICE_INDEX.getFrom(source, jsonObject);
		proIndex = IJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		return this;
	}
}
