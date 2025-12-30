package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogDefenderActionParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.action.block.StepDumpOff;
import com.fumbbl.ffb.server.step.action.block.StepDumpOff.StepState;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.common.DumpOff;
import com.fumbbl.ffb.util.UtilCards;

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
				UtilServerDialog.hideDialog(step.getGameState());

				if (game.getTurnMode() == TurnMode.DUMP_OFF) {
					game.setTurnMode(state.oldTurnMode);
					game.setThrowerId(null);
					step.getResult().setNextAction(StepAction.NEXT_STEP);

				} else if (state.usingDumpOff == null) {
					Player<?> defender;
					TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
					FieldCoordinate defenderPosition;
					if (targetSelectionState == null) {
						defender = game.getDefender();
						defenderPosition = state.defenderPosition;
					} else {
						defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
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
					step.commitTargetSelection();
					TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
					String defenderId = targetSelectionState == null ? game.getDefenderId() : targetSelectionState.getSelectedPlayerId();
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
					step.commitTargetSelection();
					TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
					String defenderId = targetSelectionState == null ? game.getDefenderId() : targetSelectionState.getSelectedPlayerId();
					step.getResult().addReport(new ReportSkillUse(defenderId, skill, false, null));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}