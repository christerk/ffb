package com.fumbbl.ffb.server.step.action.block;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerPushback;
import com.fumbbl.ffb.util.UtilCards;

/**
 * 
 * @author Kalimar
 */
public class UtilBlockSequence {

	/**
	 * Initializes pushback by setting the starting square, handles skill
	 * STRIP_BALL.
	 * <p>
	 * Sets stepParameter STARTING_PUSHBACK_SQUARE for all steps on the stack. Sets
	 * stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
	 */
	public static StepParameterSet initPushback(IStep pStep) {
		StepParameterSet parameterSet = new StepParameterSet();
		Game game = pStep.getGameState().getGame();
		SkillMechanic mechanic =
			(SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());

		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		game.getFieldModel().clearPushbackSquares();
		parameterSet.add(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE,
			UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate, game.isHomePlaying())));

		Skill skillCanForceOpponentToDropBall = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.forceOpponentToDropBallOnPushback);
		if (skillCanForceOpponentToDropBall != null && defenderCoordinate.equals(game.getFieldModel().getBallCoordinate()) && game.getDefender().getTeam() != actingPlayer.getPlayer().getTeam()) {

			Skill skillCanCounterOpponentForcingDropBall = UtilCards.getSkillCancelling(game.getDefender(),
				skillCanForceOpponentToDropBall);
			

			if ((game.getDefender() != null) && skillCanCounterOpponentForcingDropBall != null && mechanic.canPreventStripBall(game.getFieldModel().getPlayerState(game.getDefender()))) {
				pStep.getResult().addReport(new ReportSkillUse(game.getDefenderId(), skillCanCounterOpponentForcingDropBall,
					true, SkillUse.CANCEL_STRIP_BALL));
			} else {
				pStep.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skillCanForceOpponentToDropBall, true, SkillUse.STEAL_BALL));
				parameterSet
					.add(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
				parameterSet.add(new StepParameter(StepParameterKey.BALL_KNOCKED_LOSE, true));
				if (game.getDefender() != null) {
					boolean defenderHasTacklezones = game.getFieldModel().getPlayerState(game.getDefender()).hasTacklezones();
					if (!defenderHasTacklezones && skillCanCounterOpponentForcingDropBall != null) {
						pStep.getResult().addReport(new ReportSkillUse(game.getDefenderId(), skillCanCounterOpponentForcingDropBall, false, SkillUse.NO_TACKLEZONE));
					}
				}
			}
		}
		return parameterSet;
	}

}
