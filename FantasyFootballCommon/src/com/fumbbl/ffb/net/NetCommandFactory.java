package com.fumbbl.ffb.net;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

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
			IFactorySource commandSource = netCommand.getContext() == FactoryContext.APPLICATION ? applicationFactorySource : source;
			netCommand.initFrom(commandSource, pJsonValue);
		}
		return netCommand;
	}

}
