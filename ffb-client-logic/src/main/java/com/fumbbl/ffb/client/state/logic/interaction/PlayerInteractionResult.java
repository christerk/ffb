package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.FieldCoordinate;

public class PlayerInteractionResult {

	private final Kind kind;
	private final FieldCoordinate coordinate;

	public PlayerInteractionResult(Kind kind) {
		this(kind, null);
	}

	public PlayerInteractionResult(Kind kind, FieldCoordinate coordinates) {
		this.kind = kind;
		this.coordinate = coordinates;
	}

	public Kind getKind() {
		return kind;
	}

	public FieldCoordinate getCoordinate() {
		return coordinate;
	}

	public enum Kind {
		DESELECT, SHOW_ACTIONS, MOVE, IGNORE
	}
}