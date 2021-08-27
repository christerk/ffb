package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandGameState extends ServerCommand {

	private Game fGame;

	public ServerCommandGameState() {
		super();
	}

	public ServerCommandGameState(Game pGame) {
		this();
		fGame = pGame;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_GAME_STATE;
	}

	public Game getGame() {
		return fGame;
	}

	public ServerCommandGameState transform() {
		return new ServerCommandGameState(getGame().transform());
	}

	public boolean isReplayable() {
		return false;
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
		if (fGame != null) {
			IJsonOption.GAME.addTo(jsonObject, fGame.toJsonValue());
		}
		return jsonObject;
	}

	public ServerCommandGameState initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		JsonObject gameObject = IJsonOption.GAME.getFrom(source, jsonObject);
		if (gameObject != null) {
			Game game = new Game(source.forContext(FactoryContext.APPLICATION), source.getFactoryManager());
			game.initFrom(source, gameObject);
			fGame = game;
		}
		return this;
	}

}
