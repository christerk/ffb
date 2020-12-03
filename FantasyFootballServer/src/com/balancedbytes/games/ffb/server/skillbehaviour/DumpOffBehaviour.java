package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogDefenderActionParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepDumpOff;
import com.balancedbytes.games.ffb.server.step.action.block.StepDumpOff.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.DumpOff;
import com.balancedbytes.games.ffb.util.UtilCards;

public class DumpOffBehaviour extends SkillBehaviour<DumpOff> {
	public DumpOffBehaviour() {
		super(DumpOff.class);

		registerModifier(new StepModifier<StepDumpOff, StepDumpOff.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDumpOff step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.usingDumpOff = useSkillCommand.isSkillUsed();
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDumpOff step, StepState state) {
				Game game = step.getGameState().getGame();

				if (game.getTurnMode() == TurnMode.DUMP_OFF) {
					game.setTurnMode(state.oldTurnMode);
					game.setThrowerId(null);
					step.getResult().setNextAction(StepAction.NEXT_STEP);

				} else if (state.usingDumpOff == null) {
					if (UtilCards.hasSkill(game, game.getDefender(), skill) && (state.defenderPosition != null)
							&& state.defenderPosition.equals(game.getFieldModel().getBallCoordinate())
							&& !game.getFieldModel().isBallMoving()
							&& !(game.getFieldModel().getPlayerState(game.getDefender()).isConfused()
									|| game.getFieldModel().getPlayerState(game.getDefender()).isHypnotized())) {
						UtilServerDialog.showDialog(
								step.getGameState(),
								new DialogSkillUseParameter(game.getDefenderId(), skill, 0),
								true
								);
						step.getResult().setNextAction(StepAction.CONTINUE);
					} else {
						state.usingDumpOff = false;
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}

				} else if (state.usingDumpOff) {
					state.oldTurnMode = game.getTurnMode();
					game.setTurnMode(TurnMode.DUMP_OFF);
					game.setThrowerId(game.getDefenderId());
					game.setThrowerAction(PlayerAction.DUMP_OFF);
					game.setDefenderAction(PlayerAction.DUMP_OFF);
					UtilServerDialog.showDialog(step.getGameState(), new DialogDefenderActionParameter(), true);
					step.getGameState().pushCurrentStepOnStack();
					SequenceGenerator.getInstance().pushPassSequence(step.getGameState());
					step.getResult().setNextAction(StepAction.NEXT_STEP);

				} else {
					step.getResult().addReport(new ReportSkillUse(game.getDefenderId(), skill, false, null));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}