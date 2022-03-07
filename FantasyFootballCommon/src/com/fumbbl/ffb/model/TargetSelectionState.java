package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashSet;
import java.util.Set;

public class TargetSelectionState implements IJsonSerializable {
	private Status status = Status.STARTED;
	private String selectedPlayerId;
	private boolean committed;
	private PlayerState oldPlayerState;
	private final Set<Skill> usedSkills = new HashSet<>();

	public TargetSelectionState() {
	}

	public TargetSelectionState(String selectedPlayerId) {
		this.selectedPlayerId = selectedPlayerId;
	}

	public String getSelectedPlayerId() {
		return selectedPlayerId;
	}

	public Status getStatus() {
		return status;
	}

	public TargetSelectionState cancel() {
		status = Status.CANCELED;
		return this;
	}

	public TargetSelectionState select() {
		status = Status.SELECTED;
		return this;
	}

	public TargetSelectionState skip() {
		status = Status.SKIPPED;
		return this;
	}

	public TargetSelectionState failed() {
		status = Status.FAILED;
		return this;
	}

	public boolean isCanceled() {
		return status == Status.CANCELED;
	}

	public boolean isSelected() {
		return status == Status.SELECTED;
	}

	public boolean isSkipped() {
		return status == Status.SKIPPED;
	}

	public boolean isFailed() {
		return status == Status.FAILED;
	}

	public boolean isCommitted() {
		return committed;
	}

	public void commit() {
		committed = true;
	}

	public void removePlayer() {
		selectedPlayerId = null;
	}

	public PlayerState getOldPlayerState() {
		return oldPlayerState;
	}

	public void setOldPlayerState(PlayerState oldPlayerState) {
		this.oldPlayerState = oldPlayerState;
	}

	public Set<Skill> getUsedSkills() {
		return usedSkills;
	}


	public void addUsedSkill(Skill skill) {
		usedSkills.add(skill);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TargetSelectionState that = (TargetSelectionState) o;

		if (committed != that.committed) return false;
		if (status != that.status) return false;
		if (selectedPlayerId != null ? !selectedPlayerId.equals(that.selectedPlayerId) : that.selectedPlayerId != null)
			return false;
		if (oldPlayerState != null ? !oldPlayerState.equals(that.oldPlayerState) : that.oldPlayerState != null)
			return false;
		return usedSkills.equals(that.usedSkills);
	}

	@Override
	public int hashCode() {
		int result = status.hashCode();
		result = 31 * result + (selectedPlayerId != null ? selectedPlayerId.hashCode() : 0);
		result = 31 * result + (committed ? 1 : 0);
		result = 31 * result + (oldPlayerState != null ? oldPlayerState.hashCode() : 0);
		result = 31 * result + usedSkills.hashCode();
		return result;
	}

	@Override
	public TargetSelectionState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		selectedPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		String statusString = IJsonOption.TARGET_SELECTION_STATUS.getFrom(game, jsonObject);
		if (!StringTool.isProvided(statusString)) {
			statusString = IJsonOption.BLITZ_STATUS.getFrom(game, jsonObject);
		}
		if (StringTool.isProvided(statusString)) {

			status = Status.valueOf(statusString);
		}

		if (IJsonOption.TARGET_SELECTION_STATUS_IS_COMMITTED.isDefinedIn(jsonObject)) {
			committed = IJsonOption.TARGET_SELECTION_STATUS_IS_COMMITTED.getFrom(game, jsonObject);
		}

		if (IJsonOption.PLAYER_STATE_OLD.isDefinedIn(jsonObject)) {
			oldPlayerState = IJsonOption.PLAYER_STATE_OLD.getFrom(game, jsonObject);
		}

		if (IJsonOption.USED_SKILLS.isDefinedIn(jsonObject)) {
			JsonArray skillArray = IJsonOption.USED_SKILLS.getFrom(game, jsonObject);
			for (int i = 0; i < skillArray.size(); i++) {
				SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
				usedSkills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
			}
		}
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IJsonOption.TARGET_SELECTION_STATUS.addTo(jsonObject, status.name());
		IJsonOption.TARGET_SELECTION_STATUS_IS_COMMITTED.addTo(jsonObject, committed);
		IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, oldPlayerState);
		JsonArray skillArray = new JsonArray();
		for (Skill skill : usedSkills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.USED_SKILLS.addTo(jsonObject, skillArray);
		return jsonObject;
	}

	public enum Status {
		STARTED, CANCELED, SELECTED, SKIPPED, FAILED
	}
}
