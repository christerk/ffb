package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientCommandArgueTheCall extends ClientCommand {

	private final List<String> fPlayerIds;

	public ClientCommandArgueTheCall() {
		fPlayerIds = new ArrayList<>();
	}

	public ClientCommandArgueTheCall(String playerId) {
		this();
		addPlayerId(playerId);
	}

	public ClientCommandArgueTheCall(String[] pPlayerIds) {
		this();
		addPlayerIds(pPlayerIds);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_ARGUE_THE_CALL;
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[fPlayerIds.size()]);
	}

	public boolean hasPlayerId(String pPlayerId) {
		return fPlayerIds.contains(pPlayerId);
	}

	private void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIds.add(pPlayerId);
		}
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerId(playerId);
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
		return jsonObject;
	}

	public ClientCommandArgueTheCall initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		return this;
	}

}
