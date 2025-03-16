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
public class ClientCommandReplayStatus extends ClientCommand {

	private int commandNr, speed;
	private boolean running, forward;

	public ClientCommandReplayStatus() {
		super();
	}

	public ClientCommandReplayStatus(int commandNr, int speed, boolean running, boolean forward) {
		this.commandNr = commandNr;
		this.speed = speed;
		this.running = running;
		this.forward = forward;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY_STATUS;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COMMAND_NR.addTo(jsonObject, commandNr);
		IJsonOption.RUNNING.addTo(jsonObject, running);
		IJsonOption.FORWARD.addTo(jsonObject, forward);
		IJsonOption.SPEED.addTo(jsonObject, speed);
		return jsonObject;
	}

	public ClientCommandReplayStatus initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		forward = IJsonOption.FORWARD.getFrom(source, jsonObject);
		running =IJsonOption.RUNNING.getFrom(source, jsonObject);
		commandNr = IJsonOption.COMMAND_NR.getFrom(source, jsonObject);
		speed = IJsonOption.SPEED.getFrom(source, jsonObject);
		return this;
	}
}

