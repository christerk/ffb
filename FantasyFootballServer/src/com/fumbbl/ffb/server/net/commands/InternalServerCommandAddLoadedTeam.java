package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.util.ListTool;

import java.util.List;

/**
 * @author Kalimar
 */
public class InternalServerCommandAddLoadedTeam extends InternalServerCommand {

	private final List<String> accountProperties;
	private String coach;
	private Boolean homeTeam;
	private Team team;
	private final GameState gameState;

	public InternalServerCommandAddLoadedTeam(GameState gameState, String coach, Boolean homeTeam, Team team, List<String> accountProperties) {
		super(gameState.getId());
		this.coach = coach;
		this.homeTeam = homeTeam;
		this.team = team;
		this.accountProperties = accountProperties;
		this.gameState = gameState;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_ADD_LOADED_TEAM;
	}

	public String getCoach() {
		return coach;
	}

	public Boolean getHomeTeam() {
		return homeTeam;
	}

	public List<String> getAccountProperties() {
		return accountProperties;
	}

	public Team getTeam() {
		return team;
	}

	public GameState getGameState() {
		return gameState;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.COACH.addTo(jsonObject, coach);
		IServerJsonOption.ACCOUNT_PROPERTIES.addTo(jsonObject, accountProperties);
		IServerJsonOption.TEAM.addTo(jsonObject, team.toJsonValue());
		IServerJsonOption.HOME_TEAM.addTo(jsonObject, homeTeam);

		return jsonObject;
	}

	public InternalServerCommandAddLoadedTeam initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		super.initFrom(source, jsonValue);
		coach = IServerJsonOption.COACH.getFrom(source, jsonObject);
		ListTool.replaceAll(accountProperties, IServerJsonOption.ACCOUNT_PROPERTIES.getFrom(source, jsonObject));
		team = new Team(source).initFrom(source, IServerJsonOption.TEAM.getFrom(source, jsonObject));
		homeTeam = IServerJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		return this;
	}
}
