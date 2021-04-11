package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ClientCommandBlockOrReRollChoiceForTarget extends ClientCommand {
	private String targetId;
	private Integer selectedIndex;
	private ReRollSource reRollSource;

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET;
	}

	public ClientCommandBlockOrReRollChoiceForTarget() {
	}

	public ClientCommandBlockOrReRollChoiceForTarget(String targetId, Integer selectedIndex, ReRollSource reRollSource) {
		this.targetId = targetId;
		this.selectedIndex = selectedIndex;
		this.reRollSource = reRollSource;
	}

	public String getTargetId() {
		return targetId;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		IJsonOption.DICE_INDEX.addTo(jsonObject, selectedIndex);
		return jsonObject;
	}

	@Override
	public ClientCommandBlockOrReRollChoiceForTarget initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		targetId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		selectedIndex = IJsonOption.DICE_INDEX.getFrom(game, jsonObject);
		return this;
	}
}
