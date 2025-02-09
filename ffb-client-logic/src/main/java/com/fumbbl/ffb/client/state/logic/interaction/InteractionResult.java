package com.fumbbl.ffb.client.state.logic.interaction;

import com.fumbbl.ffb.*;

import java.util.List;

public class InteractionResult {

  private final Kind kind;
  private FieldCoordinate coordinate;
  private RangeRuler rangeRuler;
  private List<PushbackSquare> pushbackSquares;
  private SpecialEffect specialEffect;
  private MoveSquare moveSquare;
  private FieldCoordinate[] path;
  private ClientStateId delegate;
  private ActionContext actionContext;

  public InteractionResult(Kind kind) {
    this.kind = kind;
  }

  public static InteractionResult delegate(ClientStateId delegate) {
    return new InteractionResult(Kind.DELEGATE).with(delegate);
  }

  public static InteractionResult selectAction(ActionContext actionContext) {
    return new InteractionResult(Kind.SELECT_ACTION).with(actionContext);
  }

  public static InteractionResult invalid() {
    return new InteractionResult(Kind.INVALID);
  }

  public static InteractionResult reset() {
    return new InteractionResult(Kind.RESET);
  }

  public static InteractionResult perform() {
    return new InteractionResult(Kind.PERFORM);
  }

  public static InteractionResult ignore() {
    return new InteractionResult(Kind.IGNORE);
  }

  public static InteractionResult handled() {
    return new InteractionResult(Kind.HANDLED);
  }

  public static InteractionResult previewThrow() {
    return new InteractionResult(Kind.PREVIEW_THROW);
  }

  public InteractionResult with(ClientStateId delegate) {
    this.delegate = delegate;
    return this;
  }

  public InteractionResult with(ActionContext actionContext) {
    this.actionContext = actionContext;
    return this;
  }

  public InteractionResult with(FieldCoordinate coordinate) {
    this.coordinate = coordinate;
    return this;
  }

  public InteractionResult with(FieldCoordinate[] path) {
    this.path = path;
    return this;
  }

  public InteractionResult with(MoveSquare moveSquare) {
    this.moveSquare = moveSquare;
    return this;
  }

  public InteractionResult with(SpecialEffect specialEffect) {
    this.specialEffect = specialEffect;
    return this;
  }

  public InteractionResult with(RangeRuler rangeRuler) {
    this.rangeRuler = rangeRuler;
    return this;
  }

  public InteractionResult with(List<PushbackSquare> pushbackSquares) {
    this.pushbackSquares = pushbackSquares;
    return this;
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

  public List<PushbackSquare> getPushbackSquares() {
    return pushbackSquares;
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

  public ClientStateId getDelegate() {
    return delegate;
  }

  public ActionContext getActionContext() {
    return actionContext;
  }

  public enum Kind {
    DELEGATE,
    PREVIEW_THROW,
    HANDLED,
    IGNORE,
    INVALID,
    PERFORM,
    RESET,
    SELECT_ACTION
  }
}