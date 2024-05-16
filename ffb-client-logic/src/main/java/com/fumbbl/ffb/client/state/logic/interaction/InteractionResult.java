package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.FieldCoordinate;

public class InteractionResult {

	private final Kind kind;
	private final FieldCoordinate coordinate;

	public InteractionResult(Kind kind) {
		this(kind, null);
	}

	public InteractionResult(Kind kind, FieldCoordinate coordinates) {
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
		DESELECT,
		IGNORE,
		MOVE,
		PERFORM,
		RESET,
		SHOW_ACTIONS,
		SUPER
	}
}