package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.FactoryType.FactoryContext;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class NetCommandFactory {

	// JSON serialization

	private IFactorySource applicationFactorySource;

	public NetCommandFactory(IFactorySource applicationFactorySource) {
		this.applicationFactorySource = applicationFactorySource;
	}
	
	public NetCommand forJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		NetCommand netCommand = null;
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		
		NetCommandId netCommandId = (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(applicationFactorySource, jsonObject);
		if (netCommandId != null) {
			netCommand = netCommandId.createNetCommand();
			if (netCommand != null) {
				IFactorySource commandSource = netCommand.getContext() == FactoryContext.APPLICATION ? applicationFactorySource : source;
				netCommand.initFrom(commandSource, pJsonValue);
			}
		}
		return netCommand;
	}

}
