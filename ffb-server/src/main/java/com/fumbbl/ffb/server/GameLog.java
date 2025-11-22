package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandFactory;
import com.fumbbl.ffb.net.commands.ServerCommand;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class GameLog implements IJsonSerializable {

	private final List<ServerCommand> fServerCommands;

	private transient int fLastCommitedCommandNr;
	private final transient GameState fGameState;

	public GameLog(GameState pGameState) {
		fGameState = pGameState;
		fServerCommands = new ArrayList<>();
	}

	public void add(ServerCommand pServerCommand) {
		if (pServerCommand != null) {
			synchronized (fServerCommands) {
				if (pServerCommand.isReplayable()) {
					fServerCommands.add(pServerCommand);
				}
			}
		}
	}

	public ServerCommand[] getServerCommands() {
		synchronized (fServerCommands) {
			return fServerCommands.toArray(new ServerCommand[fServerCommands.size()]);
		}
	}

	public ServerCommand[] getUncommitedServerCommands() {
		List<ServerCommand> uncommitedCommands = new ArrayList<>();
		synchronized (fServerCommands) {
			for (ServerCommand serverCommand : fServerCommands) {
				if (serverCommand.getCommandNr() > fLastCommitedCommandNr) {
					uncommitedCommands.add(serverCommand);
				}
			}
		}
		return uncommitedCommands.toArray(new ServerCommand[uncommitedCommands.size()]);
	}

	public int findMaxCommandNr() {
		int maxCommandNr = 0;
		synchronized (fServerCommands) {
			for (ServerCommand serverCommand : fServerCommands) {
				if (serverCommand.getCommandNr() > maxCommandNr) {
					maxCommandNr = serverCommand.getCommandNr();
				}
			}
		}
		return maxCommandNr;
	}

	public void setLastCommitedCommandNr(int pLastCommitedCommandNr) {
		synchronized (fServerCommands) {
			fLastCommitedCommandNr = pLastCommitedCommandNr;
		}
	}

	public int getLastCommitedCommandNr() {
		synchronized (fServerCommands) {
			return fLastCommitedCommandNr;
		}
	}

	public void clear() {
		synchronized (fServerCommands) {
			fServerCommands.clear();
		}
	}

	public int size() {
		synchronized (fServerCommands) {
			return fServerCommands.size();
		}
	}

	public GameState getGameState() {
		return fGameState;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray commandArray = new JsonArray();
		for (ServerCommand serverCommand : getServerCommands()) {
			commandArray.add(serverCommand.toJsonValue());
		}
		IJsonOption.COMMAND_ARRAY.addTo(jsonObject, commandArray);
		return jsonObject;
	}

	public GameLog initFrom(IFactorySource source, JsonValue jsonValue) {
		NetCommandFactory netCommandFactory = new NetCommandFactory(fGameState.getServer().getFactorySource());
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray commandArray = IJsonOption.COMMAND_ARRAY.getFrom(source, jsonObject);
		fServerCommands.clear();
		for (int i = 0; i < commandArray.size(); i++) {
			ServerCommand serverCommand = (ServerCommand) netCommandFactory.forJsonValue(source, commandArray.get(i));
			add(serverCommand);
		}
		return this;
	}

}
