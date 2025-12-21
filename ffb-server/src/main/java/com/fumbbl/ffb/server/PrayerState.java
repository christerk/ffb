package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PrayerState implements IJsonSerializable {
    private final Set<String> friendsWithRef = new HashSet<>();
    private final Set<String> getAdditionalCatchesSpp = new HashSet<>();
    private final Set<String> getAdditionalCompletionSpp = new HashSet<>();
    private final Set<String> getAdditionalCasSpp = new HashSet<>();
    private final Set<String> underScrutiny = new HashSet<>();
    private final Set<String> foulingFrenzy = new HashSet<>();
    private final Set<String> fanInteraction = new HashSet<>();
    private final Set<String> molesUnderThePitch = new HashSet<>();
    private final Set<String> shouldNotStall = new HashSet<>();
    private final Set<String> stallers = new HashSet<>();

    public void addFriendsWithRef(Team team) {
        friendsWithRef.add(team.getId());
    }

    public void addGetAdditionalCasSpp(Team team) {
        getAdditionalCasSpp.add(team.getId());
    }

    public void addGetAdditionalCompletionSpp(Team team) {
        getAdditionalCompletionSpp.add(team.getId());
    }

    public void addGetAdditionalCatchesSpp(Team team) {
        getAdditionalCatchesSpp.add(team.getId());
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

    public Set<String> getAdditionalCompletionSppTeams() { 
        return getAdditionalCompletionSpp; 
    }

    public Set<String> getAdditionalCasSppTeams() { 
        return getAdditionalCasSpp; 
    }

    public Set<String> getAdditionalCatchesSppTeams() { 
        return getAdditionalCatchesSpp; 
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

    public void addStaller(Player<?> player) {
        stallers.add(player.getId());
    }

    public void removeStaller(Player<?> player) {
        stallers.remove(player.getId());
    }

    public void clearStallers() {
        stallers.clear();
    }

    public boolean isStalling(Player<?> player) {
        return stallers.contains(player.getId());
    }

    public Set<String> getStallerIds() {
        return stallers;
    }

    @Override
    public PrayerState initFrom(IFactorySource source, JsonValue jsonValue) {
        JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
        friendsWithRef.addAll(Arrays.asList(IServerJsonOption.FRIENDS_WITH_REF.getFrom(source, jsonObject)));
        getAdditionalCasSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_CASUALTY_SPP.getFrom(source, jsonObject)));
        getAdditionalCompletionSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_COMPLETION_SPP.getFrom(source, jsonObject)));
        underScrutiny.addAll(Arrays.asList(IServerJsonOption.TEAM_UNDER_SCRUTINY.getFrom(source, jsonObject)));
        fanInteraction.addAll(Arrays.asList(IServerJsonOption.FAN_INTERACTION.getFrom(source, jsonObject)));
        foulingFrenzy.addAll(Arrays.asList(IServerJsonOption.FOULING_FRENZY.getFrom(source, jsonObject)));
        molesUnderThePitch.addAll(Arrays.asList(IServerJsonOption.MOLES_UNDER_THE_PITCH.getFrom(source, jsonObject)));
        shouldNotStall.addAll(Arrays.asList(IServerJsonOption.SHOULD_NOT_STALL.getFrom(source, jsonObject)));
        stallers.addAll(Arrays.asList(IServerJsonOption.STALLERS.getFrom(source, jsonObject)));
        if (IServerJsonOption.GET_ADDITIONAL_CATCHES_SPP.isDefinedIn(jsonObject)) {
            getAdditionalCatchesSpp.addAll(Arrays.asList(IServerJsonOption.GET_ADDITIONAL_CATCHES_SPP.getFrom(source, jsonObject)));
        }
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
        IServerJsonOption.STALLERS.addTo(jsonObject, stallers);
        IServerJsonOption.GET_ADDITIONAL_CATCHES_SPP.addTo(jsonObject, getAdditionalCatchesSpp);
        return jsonObject;
    }
}
