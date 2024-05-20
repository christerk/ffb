package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RangeRuler;

public class InteractionResult {

	private final Kind kind;
	private final FieldCoordinate coordinate;
	private final RangeRuler rangeRuler;

	public InteractionResult(Kind kind) {
		this(kind, null, null);
	}

	public InteractionResult(Kind kind, RangeRuler rangeRuler) {
		this(kind, null, rangeRuler);
	}

	public InteractionResult(Kind kind, FieldCoordinate coordinates) {
		this(kind, coordinates, null);
	}

	public InteractionResult(Kind kind, FieldCoordinate coordinate, RangeRuler rangeRuler) {
		this.kind = kind;
		this.coordinate = coordinate;
		this.rangeRuler = rangeRuler;
	}

	public Kind getKind() {
		return kind;
	}

	public FieldCoordinate getCoordinate() {
		return coordinate;
	}

	public RangeRuler getRangeRuler() {
		return rangeRuler;
	}

	public enum Kind {
		DESELECT,
		DRAW,
		IGNORE,
		MOVE,
		PERFORM,
		RESET,
		SHOW_ACTIONS,
		SUPER
	}
}