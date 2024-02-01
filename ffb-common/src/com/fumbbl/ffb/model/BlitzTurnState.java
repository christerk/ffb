package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

public class BlitzTurnState implements IJsonSerializable {
	private int amount, available, limit;
	private boolean actingPlayerWasChanged;

	public BlitzTurnState() {
	}

	public BlitzTurnState(int limit, int available) {
		this.limit = limit;
		this.available = available;
	}

	public int getAmount() {
		return amount;
	}

	public int getLimit() {
		return limit;
	}

	public int getAvailable() {
		return available;
	}

	public void addActivation() {
		amount++;
		available--;
	}

	public boolean limitReached() {
		return amount == limit;
	}

	public boolean availablePlayersLeft() {
		return available > 0;
	}

	public boolean actingPlayerWasChanged() {
		return actingPlayerWasChanged;
	}

	public void changeActingPlayer() {
		actingPlayerWasChanged = true;
	}

	@Override
	public BlitzTurnState initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		amount = IJsonOption.NR_OF_PLAYERS.getFrom(source, jsonObject);
		limit = IJsonOption.NR_OF_PLAYERS_ALLOWED.getFrom(source, jsonObject);
		available = IJsonOption.NUMBER.getFrom(source, jsonObject);
		actingPlayerWasChanged = IJsonOption.ACTING_PLAYER_WAS_CHANGED.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NR_OF_PLAYERS.addTo(jsonObject, amount);
		IJsonOption.NR_OF_PLAYERS_ALLOWED.addTo(jsonObject, limit);
		IJsonOption.NUMBER.addTo(jsonObject, available);
		IJsonOption.ACTING_PLAYER_WAS_CHANGED.addTo(jsonObject, actingPlayerWasChanged);
		return jsonObject;
	}
}
