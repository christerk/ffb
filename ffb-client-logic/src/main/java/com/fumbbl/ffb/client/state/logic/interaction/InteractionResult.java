package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.SpecialEffect;

public class InteractionResult {

  private final Kind kind;
  private final FieldCoordinate coordinate;
  private final RangeRuler rangeRuler;
  private final PushbackSquare pushbackSquare;
  private final SpecialEffect specialEffect;

  public InteractionResult(Kind kind, PushbackSquare pushbackSquare) {
    this(kind, null, null, pushbackSquare, null);
  }

  public InteractionResult(Kind kind) {
    this(kind, null, null, null, null);
  }

  public InteractionResult(Kind kind, RangeRuler rangeRuler) {
    this(kind, null, rangeRuler, null, null);
  }

  public InteractionResult(Kind kind, FieldCoordinate coordinates) {
    this(kind, coordinates, null, null, null);
  }

  public InteractionResult(Kind kind, FieldCoordinate coordinate, RangeRuler rangeRuler, PushbackSquare pushbackSquare, SpecialEffect specialEffect) {
    this.kind = kind;
    this.coordinate = coordinate;
    this.rangeRuler = rangeRuler;
    this.pushbackSquare = pushbackSquare;
    this.specialEffect = specialEffect;
  }

	public InteractionResult(Kind kind, FieldCoordinate coordinate, SpecialEffect specialEffect) {
    this(kind, coordinate, null, null, specialEffect);
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

  public PushbackSquare getPushbackSquare() {
    return pushbackSquare;
  }

  public SpecialEffect getSpecialEffect() {
    return specialEffect;
  }

  public enum Kind {
    DRAW,
    HANDLED,
    IGNORE,
    INVALID,
    MOVE,
    PERFORM,
    RESET,
    SHOW_ACTIONS,
    SHOW_ACTION_ALTERNATIVES,
    SHOW_BLOODLUST_ACTIONS,
    SUPER,
    SUPER_DRAW
  }
}