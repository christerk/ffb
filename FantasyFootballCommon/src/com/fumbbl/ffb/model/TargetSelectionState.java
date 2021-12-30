package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.StringTool;

import java.util.Objects;

public class TargetSelectionState implements IJsonSerializable {
	private Status status = Status.STARTED;
	private String selectedPlayerId;

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

	public boolean isFailed() { return status == Status.FAILED; }

	@Override
	public int hashCode() {
		return Objects.hash(selectedPlayerId, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TargetSelectionState)) {
			return false;
		}
		TargetSelectionState other = (TargetSelectionState) obj;
		return status == other.status
			&& ((selectedPlayerId == null && other.selectedPlayerId == null) || (selectedPlayerId != null && selectedPlayerId.equals(other.selectedPlayerId))
		);
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
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IJsonOption.TARGET_SELECTION_STATUS.addTo(jsonObject, status.name());
		return jsonObject;
	}

	public enum Status {
		STARTED, CANCELED, SELECTED, SKIPPED, FAILED
	}
}
