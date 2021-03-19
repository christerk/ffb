package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Objects;

public class BlitzState implements IJsonSerializable {
	private Status status = Status.STARTED;
	private String selectedPlayerId;

	public BlitzState() {
	}

	public BlitzState(String selectedPlayerId) {
		this.selectedPlayerId = selectedPlayerId;
	}

	public String getSelectedPlayerId() {
		return selectedPlayerId;
	}

	public Status getStatus() {
		return status;
	}

	public BlitzState cancel() {
		status = Status.CANCELED;
		return this;
	}

	public BlitzState select() {
		status = Status.SELECTED;
		return this;
	}

	public BlitzState skip() {
		status = Status.SKIPPED;
		return this;
	}

	public BlitzState failed() {
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
		if (!(obj instanceof BlitzState)) {
			return false;
		}
		BlitzState other = (BlitzState) obj;
		return status == other.status
			&& ((selectedPlayerId == null && other.selectedPlayerId == null) || (selectedPlayerId != null && selectedPlayerId.equals(other.selectedPlayerId))
		);
	}

	@Override
	public BlitzState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		selectedPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		String statusString = IJsonOption.BLITZ_STATUS.getFrom(game, jsonObject);
		if (statusString != null) {
			status = Status.valueOf(statusString);
		}
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IJsonOption.BLITZ_STATUS.addTo(jsonObject, status.name());
		return jsonObject;
	}

	public enum Status {
		STARTED, CANCELED, SELECTED, SKIPPED, FAILED
	}
}
