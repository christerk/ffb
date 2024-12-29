package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.*;

public class InteractionResult {

  private final Kind kind;
  private final FieldCoordinate coordinate;
  private final RangeRuler rangeRuler;
  private final PushbackSquare pushbackSquare;
  private final SpecialEffect specialEffect;
  private final MoveSquare moveSquare;
  private final FieldCoordinate[] path;

  public InteractionResult(Kind kind) {
    this(kind, null, null, null, null, null, null);
  }

  public InteractionResult(Kind kind, MoveSquare moveSquare) {
    this(kind, null, null, null, null, moveSquare, null);
  }

  public InteractionResult(Kind kind,  FieldCoordinate[] path) {
    this(kind, null, null, null, null, null, path);
  }

  public InteractionResult(Kind kind, PushbackSquare pushbackSquare) {
    this(kind, null, null, pushbackSquare, null, null, null);
  }

  public InteractionResult(Kind kind, RangeRuler rangeRuler) {
    this(kind, null, rangeRuler, null, null, null, null);
  }

  public InteractionResult(Kind kind, FieldCoordinate coordinates) {
    this(kind, coordinates, null, null, null, null, null);
  }

  public InteractionResult(Kind kind, FieldCoordinate coordinate, SpecialEffect specialEffect) {
    this(kind, coordinate, null, null, specialEffect, null, null);
  }


  public InteractionResult(Kind kind, FieldCoordinate coordinate, RangeRuler rangeRuler, PushbackSquare pushbackSquare, SpecialEffect specialEffect, MoveSquare moveSquare, FieldCoordinate[] path) {
    this.kind = kind;
    this.coordinate = coordinate;
    this.rangeRuler = rangeRuler;
    this.pushbackSquare = pushbackSquare;
    this.specialEffect = specialEffect;
		this.moveSquare = moveSquare;
		this.path = path;
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

  public FieldCoordinate[] getPath() {
    return path;
  }

  public MoveSquare getMoveSquare() {
    return moveSquare;
  }

  public enum Kind {
    DRAW,
    HANDLED,
    IGNORE,
    INVALID,
    PERFORM,
    RESET,
    SHOW_ACTIONS,
    SHOW_ACTION_ALTERNATIVES,
    SHOW_BLOODLUST_ACTIONS,
    SUPER
  }
}