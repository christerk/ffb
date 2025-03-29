package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandReplayStatus extends ServerCommand {

	private int commandNr, speed;
	private boolean running, forward;

	public ServerCommandReplayStatus() {
	}

	public ServerCommandReplayStatus(int commandNr, int speed, boolean running, boolean forward) {
		this.commandNr = commandNr;
		this.speed = speed;
		this.running = running;
		this.forward = forward;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_REPLAY_STATUS;
	}

	public int getCommandNr() {
		return commandNr;
	}

	public int getSpeed() {
		return speed;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isForward() {
		return forward;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, commandNr);
		IJsonOption.RUNNING.addTo(jsonObject, running);
		IJsonOption.FORWARD.addTo(jsonObject, forward);
		IJsonOption.SPEED.addTo(jsonObject, speed);
		return jsonObject;
	}

	public ServerCommandReplayStatus initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		forward = IJsonOption.FORWARD.getFrom(source, jsonObject);
		running =IJsonOption.RUNNING.getFrom(source, jsonObject);
		commandNr = IJsonOption.COMMAND_NR.getFrom(source, jsonObject);
		speed = IJsonOption.SPEED.getFrom(source, jsonObject);
		return this;
	}
}

