package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PrayerState implements IJsonSerializable {
	private final Set<String> friendsWithRef = new HashSet<>();
	private final Set<String> getAdditionalCompletionSpp = new HashSet<>();
	private final Set<String> getAdditionalCasSpp = new HashSet<>();
	private final Set<String> underScrutiny = new HashSet<>();

	public void addFriendsWithRef(Team team) {
		friendsWithRef.add(team.getId());
	}

	public void addGetAdditionalCasSpp(Team team) {
		getAdditionalCasSpp.add(team.getId());
	}

	public void addGetAdditionalCompletionSpp(Team team) {
		getAdditionalCompletionSpp.add(team.getId());
	}

	public void removeFriendsWithRef(Team team) {
		friendsWithRef.remove(team.getId());
	}

	public void removeGetAdditionalCasSpp(Team team) {
		getAdditionalCasSpp.remove(team.getId());
	}

	public void removeGetAdditionalCompletionSpp(Team team) {
		getAdditionalCompletionSpp.remove(team.getId());
	}

	public boolean isFriendsWithRef(Team team) {
		return friendsWithRef.contains(team.getId());
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

	public void addUnderScrutiny(Team team) {
		underScrutiny.add(team.getId());
	}

	public void removeUnderScrutiny(Team team) {
		underScrutiny.remove(team.getId());
	}

	public boolean isUnderScrutiny(Team team) {
		return underScrutiny.contains(team.getId());
	}

	@Override
	public PrayerState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		friendsWithRef.addAll(Arrays.asList(IServerJsonOption.FRIENDS_WITH_REF.getFrom(game, jsonObject)));
		getAdditionalCasSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.getFrom(game, jsonObject)));
		getAdditionalCompletionSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.getFrom(game, jsonObject)));
		underScrutiny.addAll(Arrays.asList(IServerJsonOption.TEAM_UNDER_SCRUTINY.getFrom(game, jsonObject)));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.FRIENDS_WITH_REF.addTo(jsonObject, friendsWithRef);
		IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.addTo(jsonObject, getAdditionalCompletionSpp);
		IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.addTo(jsonObject, getAdditionalCasSpp);
		IServerJsonOption.TEAM_UNDER_SCRUTINY.addTo(jsonObject, underScrutiny);
		return jsonObject;
	}
}
