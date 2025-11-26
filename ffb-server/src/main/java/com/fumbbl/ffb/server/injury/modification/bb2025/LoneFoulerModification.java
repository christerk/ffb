package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.mechanics.SkillMechanic;

import java.util.Collections;

public class LoneFoulerModification extends RerollArmourModification {

  public LoneFoulerModification() {
    super(Collections.singleton(Foul.class));
  }

  @Override
  protected boolean tryArmourRollModification(ModificationParams params) {
    Game game = params.getGameState().getGame();
    Player<?> attacker = game.getActingPlayer().getPlayer();
    Player<?> defender = game.getPlayerById(params.getNewContext().fDefenderId);

    SkillMechanic mechanic =
      (SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());

    int offensiveAssists = UtilPlayer.findOffensiveFoulAssists(game, attacker, defender, mechanic);
    int defensiveAssists = UtilPlayer.findDefensiveFoulAssists(game, attacker, defender);
    boolean noAssists = offensiveAssists == 0 && defensiveAssists == 0;

    return noAssists && super.tryArmourRollModification(params);
  }

}
