package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogDefenderActionParameter;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.BlitzState;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepDumpOff;
import com.balancedbytes.games.ffb.server.step.action.block.StepDumpOff.StepState;
import com.balancedbytes.games.ffb.server.step.generator.Pass;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.DumpOff;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class DumpOffBehaviour extends SkillBehaviour<DumpOff> {
	public DumpOffBehaviour() {
		super();

		registerModifier(new StepModifier<StepDumpOff, StepState>() {

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
					Player<?> defender;
					BlitzState blitzState = game.getFieldModel().getBlitzState();
					FieldCoordinate defenderPosition;
					if (blitzState == null) {
						defender = game.getDefender();
						defenderPosition = state.defenderPosition;
					} else {
						defender = game.getPlayerById(blitzState.getSelectedPlayerId());
						defenderPosition = game.getFieldModel().getPlayerCoordinate(defender);
					}
					if (UtilCards.hasSkill(defender, skill) && (defenderPosition != null)
							&& defenderPosition.equals(game.getFieldModel().getBallCoordinate())
							&& !game.getFieldModel().isBallMoving()
							&& !(game.getFieldModel().getPlayerState(defender).isConfused()
									|| game.getFieldModel().getPlayerState(defender).isHypnotized())) {
						UtilServerDialog.showDialog(step.getGameState(),
								new DialogSkillUseParameter(defender.getId(), skill, 0), true);
						step.getResult().setNextAction(StepAction.CONTINUE);
					} else {
						state.usingDumpOff = false;
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}

				} else if (state.usingDumpOff) {
					BlitzState blitzState = game.getFieldModel().getBlitzState();
					String defenderId = blitzState == null ? game.getDefenderId() : blitzState.getSelectedPlayerId();
					state.oldTurnMode = game.getTurnMode();
					game.setTurnMode(TurnMode.DUMP_OFF);
					game.setThrowerId(defenderId);
					game.setThrowerAction(PlayerAction.DUMP_OFF);
					game.setDefenderAction(PlayerAction.DUMP_OFF);
					UtilServerDialog.showDialog(step.getGameState(), new DialogDefenderActionParameter(), true);
					step.getGameState().pushCurrentStepOnStack();
					SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
					((Pass) factory.forName(SequenceGenerator.Type.Pass.name()))
						.pushSequence(new Pass.SequenceParams(step.getGameState()));
					step.getResult().setNextAction(StepAction.NEXT_STEP);

				} else {
					BlitzState blitzState = game.getFieldModel().getBlitzState();
					String defenderId = blitzState == null ? game.getDefenderId() : blitzState.getSelectedPlayerId();
					step.getResult().addReport(new ReportSkillUse(defenderId, skill, false, null));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}