package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class NetCommandFactory {

	// JSON serialization

	public NetCommand forJsonValue(Game game, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		NetCommand netCommand = null;
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		NetCommandId netCommandId = (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject);
		if (netCommandId != null) {
			netCommand = netCommandId.createNetCommand();
			if (netCommand != null) {
				netCommand.initFrom(game, pJsonValue);
			}
		}
		return netCommand;
	}

}
