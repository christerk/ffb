package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandPlayerChoice extends ClientCommand {

	private PlayerChoiceMode fPlayerChoiceMode;
	private final List<String> fPlayerIds;

	public ClientCommandPlayerChoice() {
		fPlayerIds = new ArrayList<>();
	}

	public ClientCommandPlayerChoice(PlayerChoiceMode pPlayerChoiceMode, Player<?>[] pPlayers) {
		this();
		fPlayerChoiceMode = pPlayerChoiceMode;
		if (ArrayTool.isProvided(pPlayers)) {
			for (Player<?> player : pPlayers) {
				addPlayerId(player.getId());
			}
		}
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PLAYER_CHOICE;
	}

	public String getPlayerId() {
		return ((fPlayerIds.size() > 0) ? fPlayerIds.get(0) : null);
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[0]);
	}

	public void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIds.add(pPlayerId);
		}
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (int i = 0; i < pPlayerIds.length; i++) {
				addPlayerId(pPlayerIds[i]);
			}
		}
	}

	public PlayerChoiceMode getPlayerChoiceMode() {
		return fPlayerChoiceMode;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_CHOICE_MODE.addTo(jsonObject, fPlayerChoiceMode);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
		return jsonObject;
	}

	public ClientCommandPlayerChoice initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fPlayerChoiceMode = (PlayerChoiceMode) IJsonOption.PLAYER_CHOICE_MODE.getFrom(game, jsonObject);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		return this;
	}

}
