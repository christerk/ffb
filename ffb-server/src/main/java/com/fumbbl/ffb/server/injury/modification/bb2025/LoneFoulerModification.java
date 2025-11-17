package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
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

    return !hasAnyFoulAssist(game, attacker, defender) && super.tryArmourRollModification(params);
  }


  public static boolean hasAnyFoulAssist(Game game, Player<?> attacker, Player<?> defender) {
    GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
    FieldCoordinate coordinateDefender = game.getFieldModel().getPlayerCoordinate(defender);
    FieldCoordinate coordinateAttacker = game.getFieldModel().getPlayerCoordinate(attacker);

    for (Player<?> offensive : UtilPlayer.findAdjacentPlayersWithTacklezones(game, attacker.getTeam(), coordinateDefender, false)) {
      if (offensive == attacker) {
        continue;
      }
      FieldCoordinate coordAssist = game.getFieldModel().getPlayerCoordinate(offensive);
      Player<?>[] adjacentOpponents = UtilPlayer.findAdjacentPlayersWithTacklezones(game, defender.getTeam(), coordAssist, false);

      boolean guardIsCanceled = mechanic.allowsCancellingGuard(game.getTurnMode())
        && Arrays.stream(adjacentOpponents)
        .flatMap(p -> p.getSkillsIncludingTemporaryOnes().stream())
        .anyMatch(skill -> skill.canCancel(NamedProperties.assistsFoulsInTacklezones));

      boolean putTheBootInIsCancelled = Arrays.stream(adjacentOpponents)
        .flatMap(p -> p.getSkillsIncludingTemporaryOnes().stream())
        .anyMatch(skill -> skill.canCancel(NamedProperties.canAlwaysAssistFouls));

      boolean canAlwaysAssistFouls =
        (offensive.hasSkillProperty(NamedProperties.canAlwaysAssistFouls) && !putTheBootInIsCancelled)
          || (UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_AS_FOUL_GUARD)
          && offensive.hasSkillProperty(NamedProperties.canAlwaysAssistFoulsWithSg));

      boolean validAssist =
        (adjacentOpponents.length < 1)
          || canAlwaysAssistFouls
          || (offensive.hasSkillProperty(NamedProperties.assistsFoulsInTacklezones) && !guardIsCanceled);

      if (validAssist) {
        return true;
      }
    }

    for (Player<?> defensive : UtilPlayer.findAdjacentPlayersWithTacklezones(game, defender.getTeam(), coordinateAttacker, false)) {
      if (defensive == defender) {
        continue;
      }
      FieldCoordinate coordAssist = game.getFieldModel().getPlayerCoordinate(defensive);
      boolean validAssist =
        defensive.hasSkillProperty(NamedProperties.assistsFoulsInTacklezones)
          || UtilPlayer.findAdjacentPlayersWithTacklezones(game, attacker.getTeam(), coordAssist, false).length < 2;

      if (validAssist) {
        return true;
      }
    }

    return false;
  }


}
