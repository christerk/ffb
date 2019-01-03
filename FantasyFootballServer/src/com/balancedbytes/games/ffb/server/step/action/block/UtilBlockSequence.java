package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerPushback;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class UtilBlockSequence {

  /**
   * Initializes pushback by setting the starting square, handles skill STRIP_BALL.
   * 
   * Sets stepParameter STARTING_PUSHBACK_SQUARE for all steps on the stack. Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
   */
  public static StepParameterSet initPushback(IStep pStep) {
    StepParameterSet parameterSet = new StepParameterSet();
    Game game = pStep.getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
    game.getFieldModel().clearPushbackSquares();
    parameterSet.add(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE,
      UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate, game.isHomePlaying())));
    if (UtilCards.hasSkill(game, actingPlayer, Skill.STRIP_BALL)
	  && defenderCoordinate != null
      && defenderCoordinate.equals(game.getFieldModel().getBallCoordinate())
      && (game.getDefender().getTeam() != actingPlayer.getPlayer().getTeam())) {
      if ((game.getDefender() != null) && UtilCards.hasSkill(game, game.getDefender(), Skill.SURE_HANDS)) {
        pStep.getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.SURE_HANDS, true, SkillUse.CANCEL_STRIP_BALL));
      } else if ((game.getDefender() != null) && UtilCards.hasSkill(game, game.getDefender(), Skill.MOUNSTROUS_MOUTH)) {
        pStep.getResult().addReport(new ReportSkillUse(game.getDefenderId(), Skill.MOUNSTROUS_MOUTH, true, SkillUse.CANCEL_STRIP_BALL));
      } else {
        pStep.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.STRIP_BALL, true, SkillUse.STEAL_BALL));
        parameterSet.add(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
        actingPlayer.markSkillUsed(Skill.STRIP_BALL);
      }
    }
    return parameterSet;
  }

}
