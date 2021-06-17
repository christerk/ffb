package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.PlayerResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PrayerState implements IJsonSerializable {
	private final Set<String> friendsWithRef = new HashSet<>();
	private final Set<String> getAdditionalCompletionSpp = new HashSet<>();
	private final Set<String> getAdditionalCasSpp = new HashSet<>();

	public void addFriendsWithRef(String teamId) {
		friendsWithRef.add(teamId);
	}

	public void addGetAdditionalCasSpp(String teamId) {
		getAdditionalCasSpp.add(teamId);
	}

	public void addGetAdditionalCompletionSpp(String teamId) {
		getAdditionalCompletionSpp.add(teamId);
	}

	public void removeFriendsWithRef(String teamId) {
		friendsWithRef.remove(teamId);
	}

	public void removeGetAdditionalCasSpp(String teamId) {
		getAdditionalCasSpp.remove(teamId);
	}

	public void removeGetAdditionalCompletionSpp(String teamId) {
		getAdditionalCompletionSpp.remove(teamId);
	}

	public boolean isFriendsWithRef(String teamId) {
		return friendsWithRef.contains(teamId);
	}

	public boolean getsAdditionCasSpp(String teamId) {
		return getAdditionalCasSpp.contains(teamId);
	}

	public boolean getsAdditionCompletionSpp(String teamId) {
		return getAdditionalCompletionSpp.contains(teamId);
	}

	public void addCompletion(PlayerResult playerResult) {
		playerResult.setCompletions(playerResult.getCompletions() + 1);
		if (getAdditionalCompletionSpp.contains(playerResult.getPlayer().getTeam().getId())) {
			playerResult.setCompletionsWithAdditionalSpp(playerResult.getCompletionsWithAdditionalSpp() + 1);
		}
	}

	public void addCasualty(PlayerResult playerResult) {
		playerResult.setCasualties(playerResult.getCasualties() + 1);
		if (getAdditionalCasSpp.contains(playerResult.getPlayer().getTeam().getId())) {
			playerResult.setCasualtiesWithAdditionalSpp(playerResult.getCasualtiesWithAdditionalSpp() + 1);
		}
	}

	@Override
	public PrayerState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		friendsWithRef.addAll(Arrays.asList(IServerJsonOption.FRIENDS_WITH_REF.getFrom(game, jsonObject)));
		getAdditionalCasSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.getFrom(game, jsonObject)));
		getAdditionalCompletionSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.getFrom(game, jsonObject)));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.FRIENDS_WITH_REF.addTo(jsonObject, friendsWithRef);
		IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.addTo(jsonObject, getAdditionalCompletionSpp);
		IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.addTo(jsonObject, getAdditionalCasSpp);
		return jsonObject;
	}
}
