package com.fumbbl.ffb.server.step.bb2025.special;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.SpecialEffect.SequenceParams;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepResolveBomb extends AbstractStep {

	private FieldCoordinate fBombCoordinate;
  private String fCatcherId;

	public StepResolveBomb(GameState gameState) {
		super(gameState, StepAction.NEXT_STEP);
	}

	@Override
	public StepId getId() {
		return StepId.RESOLVE_BOMB;
	}

  @Override
  public void init(StepParameterSet parameterSet) {
    super.init(parameterSet);
    if (parameterSet != null) {
      for (StepParameter parameter : parameterSet.values()) {
        switch (parameter.getKey()) {
          case CATCHER_ID:
            fCatcherId = (String) parameter.getValue();
            break;
          default:
            break;
        }
      }
    }
  }

  @Override
  public boolean setParameter(StepParameter parameter) {
    if ((parameter != null) && !super.setParameter(parameter)) {
      if (parameter.getKey() == StepParameterKey.CATCHER_ID) {
        fCatcherId = (String) parameter.getValue();
        return true;
      }
    }
    return false;
  }

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		fBombCoordinate = game.getFieldModel().getBombCoordinate();
		if (fBombCoordinate == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
    if (StringTool.isProvided(fCatcherId)) {
      getResult().setNextAction(StepAction.NEXT_STEP);
      return;
    }
		game.getFieldModel().setBombMoving(false);
    game.getFieldModel().setBombCoordinate(null);
    getResult().setAnimation(new Animation(AnimationType.BOMB_EXPLOSION, fBombCoordinate));
    UtilServerGame.syncGameModel(this);
    game.getFieldModel().add(new BloodSpot(fBombCoordinate, new PlayerState(PlayerState.HIT_BY_BOMB)));
    List<Player<?>> affectedPlayers = new ArrayList<>();
    FieldCoordinate[] targetCoordinates = game.getFieldModel().findAdjacentCoordinates(fBombCoordinate,
        FieldCoordinateBounds.FIELD, 1, true);
    for (int i = targetCoordinates.length - 1; i >= 0; i--) {
        Player<?> player = game.getFieldModel().getPlayer(targetCoordinates[i]);
        if (player != null) {
            affectedPlayers.add(player);
        }
    }
    if (!affectedPlayers.isEmpty()) {
        SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
        com.fumbbl.ffb.server.step.generator.SpecialEffect generator =
            (com.fumbbl.ffb.server.step.generator.SpecialEffect) factory.forName(SequenceGenerator.Type.SpecialEffect.name());
        affectedPlayers.stream().map(player -> {
            boolean rollForEffect = !fBombCoordinate.equals(game.getFieldModel().getPlayerCoordinate(player));
            return new SequenceParams(getGameState(), SpecialEffect.BOMB, player.getId(), rollForEffect);
        }).forEach(generator::pushSequence);
    }
    publishParameter(new StepParameter(StepParameterKey.BOMB_EXPLODED, true));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.BOMB_COORDINATE.addTo(jsonObject, fBombCoordinate);
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		return jsonObject;
	}

	@Override
	public StepResolveBomb initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBombCoordinate = IServerJsonOption.BOMB_COORDINATE.getFrom(source, jsonObject);
    fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		return this;
	}
}
