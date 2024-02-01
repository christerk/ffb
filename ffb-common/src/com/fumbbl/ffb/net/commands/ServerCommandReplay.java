package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandFactory;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ServerCommandReplay extends ServerCommand {

	public static final int MAX_NR_OF_COMMANDS = 100;

	private final List<ServerCommand> fReplayCommands;
	private int fTotalNrOfCommands;
	private boolean lastCommand;

	public NetCommandId getId() {
		return NetCommandId.SERVER_REPLAY;
	}

	public ServerCommandReplay() {
		fReplayCommands = new ArrayList<>();
	}

	public void add(ServerCommand pServerCommand) {
		if (pServerCommand != null) {
			fReplayCommands.add(pServerCommand);
		}
	}

	public void add(ServerCommand[] pServerCommands) {
		if (ArrayTool.isProvided(pServerCommands)) {
			for (ServerCommand serverCommand : pServerCommands) {
				add(serverCommand);
			}
		}
	}

	public int getNrOfCommands() {
		return fReplayCommands.size();
	}

	public void setTotalNrOfCommands(int pTotalNrOfCommands) {
		fTotalNrOfCommands = pTotalNrOfCommands;
	}

	public int getTotalNrOfCommands() {
		return fTotalNrOfCommands;
	}

	public ServerCommand[] getReplayCommands() {
		return fReplayCommands.toArray(new ServerCommand[fReplayCommands.size()]);
	}

	public boolean isReplayable() {
		return false;
	}

	public int findHighestCommandNr() {
		int highestCommandNr = 0;
		for (ServerCommand serverCommand : fReplayCommands) {
			if (serverCommand.getCommandNr() > highestCommandNr) {
				highestCommandNr = serverCommand.getCommandNr();
			}
		}
		return highestCommandNr;
	}

	public int findLowestCommandNr() {
		int lowestCommandNr = Integer.MAX_VALUE;
		for (ServerCommand serverCommand : fReplayCommands) {
			if (serverCommand.getCommandNr() < lowestCommandNr) {
				lowestCommandNr = serverCommand.getCommandNr();
			}
		}
		return lowestCommandNr;
	}

	public boolean isLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(boolean lastCommand) {
		this.lastCommand = lastCommand;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.TOTAL_NR_OF_COMMANDS.addTo(jsonObject, fTotalNrOfCommands);
		JsonArray commandArray = new JsonArray();
		for (ServerCommand replayCommand : getReplayCommands()) {
			commandArray.add(replayCommand.toJsonValue());
		}
		IJsonOption.COMMAND_ARRAY.addTo(jsonObject, commandArray);
		IJsonOption.LAST_COMMAND.addTo(jsonObject, lastCommand);
		return jsonObject;
	}

	public ServerCommandReplay initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		fTotalNrOfCommands = IJsonOption.TOTAL_NR_OF_COMMANDS.getFrom(source, jsonObject);
		JsonArray commandArray = IJsonOption.COMMAND_ARRAY.getFrom(source, jsonObject);
		fReplayCommands.clear();
		NetCommandFactory netCommandFactory = new NetCommandFactory(source);
		for (int i = 0; i < commandArray.size(); i++) {
			ServerCommand replayCommand = (ServerCommand) netCommandFactory.forJsonValue(source, commandArray.get(i));
			add(replayCommand);
		}
		lastCommand = IJsonOption.LAST_COMMAND.getFrom(source, jsonObject);
		return this;
	}

}
