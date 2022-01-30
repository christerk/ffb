package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.StringTool;

public class TargetSelectionState implements IJsonSerializable {
	private Status status = Status.STARTED;
	private String selectedPlayerId;
	private boolean committed;
	private PlayerState oldPlayerState;

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


	public PlayerState getOldPlayerState() {
		return oldPlayerState;
	}

	public void setOldPlayerState(PlayerState oldPlayerState) {
		this.oldPlayerState = oldPlayerState;
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
		return oldPlayerState != null ? oldPlayerState.equals(that.oldPlayerState) : that.oldPlayerState == null;
	}

	@Override
	public int hashCode() {
		int result = status != null ? status.hashCode() : 0;
		result = 31 * result + (selectedPlayerId != null ? selectedPlayerId.hashCode() : 0);
		result = 31 * result + (committed ? 1 : 0);
		result = 31 * result + (oldPlayerState != null ? oldPlayerState.hashCode() : 0);
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
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IJsonOption.TARGET_SELECTION_STATUS.addTo(jsonObject, status.name());
		IJsonOption.TARGET_SELECTION_STATUS_IS_COMMITTED.addTo(jsonObject, committed);
		IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, oldPlayerState);
		return jsonObject;
	}

	public enum Status {
		STARTED, CANCELED, SELECTED, SKIPPED, FAILED
	}
}
