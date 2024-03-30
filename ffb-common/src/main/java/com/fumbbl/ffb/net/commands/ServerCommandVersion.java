package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ServerCommandVersion extends ServerCommand {

	private String fServerVersion;
	private String fClientVersion;
	private final Map<String, String> fClientProperties;

	private boolean isTestServer;

	public ServerCommandVersion() {
		fClientProperties = new HashMap<>();
	}

	public ServerCommandVersion(String pServerVersion, String pClientVersion, String[] pClientProperties,
															String[] pClientPropertyValues, boolean isTestServer) {
		this();
		this.isTestServer = isTestServer;
		fServerVersion = pServerVersion;
		fClientVersion = pClientVersion;
		if (ArrayTool.isProvided(pClientProperties) && ArrayTool.isProvided(pClientPropertyValues)) {
			for (int i = 0; i < pClientProperties.length; i++) {
				fClientProperties.put(pClientProperties[i], pClientPropertyValues[i]);
			}
		}
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_VERSION;
	}

	public String getServerVersion() {
		return fServerVersion;
	}

	public String getClientVersion() {
		return fClientVersion;
	}

	public String[] getClientProperties() {
		return fClientProperties.keySet().toArray(new String[0]);
	}

	public String getClientPropertyValue(String pClientProperty) {
		return fClientProperties.get(pClientProperty);
	}

	public boolean isReplayable() {
		return false;
	}

	public boolean isTestServer() {
		return isTestServer;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.SERVER_VERSION.addTo(jsonObject, fServerVersion);
		IJsonOption.CLIENT_VERSION.addTo(jsonObject, fClientVersion);
		String[] clientPropertyNames = getClientProperties();
		String[] clientPropertyValues = new String[clientPropertyNames.length];
		for (int i = 0; i < clientPropertyNames.length; i++) {
			clientPropertyValues[i] = getClientPropertyValue(clientPropertyNames[i]);
		}
		IJsonOption.CLIENT_PROPERTY_NAMES.addTo(jsonObject, clientPropertyNames);
		IJsonOption.CLIENT_PROPERTY_VALUES.addTo(jsonObject, clientPropertyValues);
		IJsonOption.TESTING.addTo(jsonObject, isTestServer);
		return jsonObject;
	}

	public ServerCommandVersion initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fServerVersion = IJsonOption.SERVER_VERSION.getFrom(source, jsonObject);
		fClientVersion = IJsonOption.CLIENT_VERSION.getFrom(source, jsonObject);
		String[] clientPropertyNames = IJsonOption.CLIENT_PROPERTY_NAMES.getFrom(source, jsonObject);
		String[] clientPropertyValues = IJsonOption.CLIENT_PROPERTY_VALUES.getFrom(source, jsonObject);
		fClientProperties.clear();
		if (ArrayTool.isProvided(clientPropertyNames) && ArrayTool.isProvided(clientPropertyValues)) {
			for (int i = 0; i < clientPropertyNames.length; i++) {
				fClientProperties.put(clientPropertyNames[i], clientPropertyValues[i]);
			}
		}

		if (IJsonOption.TESTING.isDefinedIn(jsonObject)) {
			isTestServer = IJsonOption.TESTING.getFrom(source, jsonObject);
		}
		return this;
	}

}
