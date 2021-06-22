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
	private final Set<String> foulingFrenzy = new HashSet<>();
	private final Set<String> fanInteraction = new HashSet<>();
	private final Set<String> molesUnderThePitch = new HashSet<>();
	private final Set<String> shouldNotStall = new HashSet<>();

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

	public void addFanInteraction(Team team) {
		fanInteraction.add(team.getId());
	}

	public void removeFanInteraction(Team team) {
		fanInteraction.remove(team.getId());
	}

	public boolean hasFanInteraction(Team team) {
		return fanInteraction.contains(team.getId());
	}

	public void addFoulingFrenzy(Team team) {
		foulingFrenzy.add(team.getId());
	}

	public void removeFoulingFrenzy(Team team) {
		foulingFrenzy.remove(team.getId());
	}

	public boolean hasFoulingFrenzy(Team team) {
		return foulingFrenzy.contains(team.getId());
	}

	public void addMolesUnderThePitch(Team team) {
		molesUnderThePitch.add(team.getId());
	}

	public void removeMolesUnderThePitch(Team team) {
		molesUnderThePitch.remove(team.getId());
	}

	public Set<String> getMolesUnderThePitch() {
		return molesUnderThePitch;
	}

	public void addShouldNotStall(Team team) {
		shouldNotStall.add(team.getId());
	}

	public void removeShouldNotStall(Team team) {
		shouldNotStall.remove(team.getId());
	}

	public boolean shouldNotStall(Team team) {
		return shouldNotStall.contains(team.getId());
	}

	@Override
	public PrayerState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		friendsWithRef.addAll(Arrays.asList(IServerJsonOption.FRIENDS_WITH_REF.getFrom(game, jsonObject)));
		getAdditionalCasSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.getFrom(game, jsonObject)));
		getAdditionalCompletionSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.getFrom(game, jsonObject)));
		underScrutiny.addAll(Arrays.asList(IServerJsonOption.TEAM_UNDER_SCRUTINY.getFrom(game, jsonObject)));
		fanInteraction.addAll(Arrays.asList(IServerJsonOption.FAN_INTERACTION.getFrom(game, jsonObject)));
		foulingFrenzy.addAll(Arrays.asList(IServerJsonOption.FOULING_FRENZY.getFrom(game, jsonObject)));
		molesUnderThePitch.addAll(Arrays.asList(IServerJsonOption.MOLES_UNDER_THE_PITCH.getFrom(game, jsonObject)));
		shouldNotStall.addAll(Arrays.asList(IServerJsonOption.SHOULD_NOT_STALL.getFrom(game, jsonObject)));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.FRIENDS_WITH_REF.addTo(jsonObject, friendsWithRef);
		IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.addTo(jsonObject, getAdditionalCompletionSpp);
		IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.addTo(jsonObject, getAdditionalCasSpp);
		IServerJsonOption.TEAM_UNDER_SCRUTINY.addTo(jsonObject, underScrutiny);
		IServerJsonOption.FAN_INTERACTION.addTo(jsonObject, fanInteraction);
		IServerJsonOption.FOULING_FRENZY.addTo(jsonObject, foulingFrenzy);
		IServerJsonOption.MOLES_UNDER_THE_PITCH.addTo(jsonObject, molesUnderThePitch);
		IServerJsonOption.SHOULD_NOT_STALL.addTo(jsonObject, shouldNotStall);
		return jsonObject;
	}
}
