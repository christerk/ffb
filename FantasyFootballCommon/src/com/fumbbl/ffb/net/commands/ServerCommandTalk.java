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
public class ServerCommandTalk extends ServerCommand {

	private String fCoach;
	private final List<String> fTalks;
	private Mode mode = Mode.REGULAR;

	public ServerCommandTalk() {
		fTalks = new ArrayList<>();
	}

	public ServerCommandTalk(String pCoach, String pTalk, Mode mode) {
		this();
		fCoach = pCoach;
		this.mode = mode;
		addTalk(pTalk);
	}

	public ServerCommandTalk(String pCoach, String[] pTalk) {
		this();
		fCoach = pCoach;
		addTalks(pTalk);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_TALK;
	}

	public String getCoach() {
		return fCoach;
	}

	public Mode getMode() {
		return mode;
	}

	public void addTalk(String pTalk) {
		if (StringTool.isProvided(pTalk)) {
			fTalks.add(pTalk);
		}
	}

	public void addTalks(String[] pTalk) {
		if (ArrayTool.isProvided(pTalk)) {
			for (String talk : pTalk) {
				addTalk(talk);
			}
		}
	}

	public String[] getTalks() {
		return fTalks.toArray(new String[0]);
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.TALKS.addTo(jsonObject, fTalks);
		IJsonOption.TALK_MODE.addTo(jsonObject, mode.toString());
		return jsonObject;
	}

	public ServerCommandTalk initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		// paranoid backwards compatibility in case a staff member was talking to coaches during the update.
		// can be removed for the next update
		if (IJsonOption.ADMIN_MODE.isDefinedIn(jsonObject)) {
			mode = IJsonOption.ADMIN_MODE.getFrom(source, jsonObject) ? Mode.STAFF : Mode.REGULAR;
		} else if (IJsonOption.TALK_MODE.isDefinedIn(jsonObject)) {
			mode = Mode.valueOf(IJsonOption.TALK_MODE.getFrom(source, jsonObject));
		}
		addTalks(IJsonOption.TALKS.getFrom(source, jsonObject));
		return this;
	}

	public enum Mode {
		REGULAR, STAFF("Staff", "!", true), DEV("Dev", "!", true);

		private final String prefix;
		private final String indicator;
		private final boolean sendToAll;

		Mode(String prefix, String indicator, boolean sendToAll) {
			this.prefix = prefix;
			this.indicator = indicator;
			this.sendToAll = sendToAll;
		}

		Mode() {
			this("", "", false);
		}

		public boolean isSendToAll() {
			return sendToAll;
		}

		public String getPrefix() {
			if (StringTool.isProvided(prefix)) {
				return prefix.trim() + " ";
			}

			return "";
		}

		public boolean findIndicator(String input) {
			return StringTool.isProvided(input) && input.startsWith(indicator);
		}

		public String cleanIndicator(String input) {
			if (StringTool.isProvided(input)) {
				return input.substring(indicator.length());
			}

			return "";
		}
	}
}
