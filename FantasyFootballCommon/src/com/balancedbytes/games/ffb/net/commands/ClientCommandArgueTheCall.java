package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandArgueTheCall extends ClientCommand {

	private List<String> fPlayerIds;

	public ClientCommandArgueTheCall() {
		fPlayerIds = new ArrayList<String>();
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

	public ClientCommandArgueTheCall initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(jsonObject));
		return this;
	}

}
