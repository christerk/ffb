package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.server.injury.modification.AvOrInjModification;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

public class SlayerModification extends AvOrInjModification {

  @Override
  protected boolean tryArmourRollModification(ModificationParams params) {
    return super.tryArmourRollModification(params) && defenderIsBigGuy(params.getGameState().getGame(), params.getNewContext().fDefenderId);
  }

  private boolean defenderIsBigGuy(Game game, String playerId) {
    return game.getPlayerById(playerId).getPosition().getKeywords().contains(Keyword.BIG_GUY);
  }

  @Override
  protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
    return super.tryInjuryModification(game, injuryContext, injuryType) && defenderIsBigGuy(game, injuryContext.getDefenderId());
  }
}