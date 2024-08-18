package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;

public class SlayerModification extends AvOrInjModification {

  @Override
  protected boolean tryArmourRollModification(ModificationParams params) {
    return super.tryArmourRollModification(params) && defenderHasSt5OrMore(params.getGameState().getGame(), params.getNewContext().fDefenderId);
  }

  private boolean defenderHasSt5OrMore(Game game, String playerId) {
    return game.getPlayerById(playerId).getStrengthWithModifiers() >= 5;
  }

  @Override
  protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
    return super.tryInjuryModification(game, injuryContext, injuryType) && defenderHasSt5OrMore(game, injuryContext.getDefenderId());
  }
}
