package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.ThrowInMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2025.ReportSwoopDirection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.ttm.StepSwoop;
import com.fumbbl.ffb.server.step.bb2025.ttm.StepSwoop.StepState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2025.Swoop;

@RulesCollection(Rules.BB2025)
public class SwoopBehaviour extends SkillBehaviour<Swoop> {
	public SwoopBehaviour() {
		super();

		registerModifier(new StepModifier<StepSwoop, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepSwoop step, StepState state,
				ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepSwoop step, StepState state) {

				GameState gameState = step.getGameState();
				Game game = gameState.getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> swoopingPlayer = actingPlayer.getPlayer();

				if (state.usingSwoop && swoopingPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) {

					if (state.reRolledAction == ReRolledActions.SWOOP_DIRECTION &&
						(state.reRollSource == null || !UtilServerReRoll.useReRoll(step, state.reRollSource, swoopingPlayer))) {
						step.publishParameter(StepParameter.from(StepParameterKey.DIRECTION, state.swoopDirection));
						step.publishParameter(StepParameter.from(StepParameterKey.USING_SWOOP, true));
						step.getResult().setNextAction(StepAction.NEXT_STEP);
						return false;
					}

					state.coordinateFrom = game.getFieldModel().getPlayerCoordinate(swoopingPlayer);

					Direction scatterDirection;
					int scatterRoll = step.getGameState().getDiceRoller().rollThrowInDirection();
					ThrowInMechanic mechanic = game.getMechanic(Mechanic.Type.THROW_IN);
					if (state.coordinateFrom.getX() < state.coordinateTo.getX()) {
						scatterDirection = mechanic.interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
					} else if (state.coordinateFrom.getX() > state.coordinateTo.getX()) {
						scatterDirection = mechanic.interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
					} else if (state.coordinateFrom.getY() < state.coordinateTo.getY()) {
						scatterDirection = mechanic.interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
					} else { // coordinateFrom.getY() > coordinateTo.getY()
						scatterDirection = mechanic.interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
					}
					state.swoopDirection = scatterDirection;

					FieldModel fieldModel = game.getFieldModel();

					FieldCoordinate indicatorCoordinate = state.coordinateFrom.move(state.swoopDirection, 1);
					fieldModel.clearMoveSquares();
					boolean outOfBounds = !FieldCoordinateBounds.FIELD.isInBounds(indicatorCoordinate);
					if (!outOfBounds) {
						fieldModel.add(new MoveSquare(indicatorCoordinate, 0, 0));
					}
					step.getResult().addReport(
						new ReportSwoopDirection(state.swoopDirection, scatterRoll, actingPlayer.getPlayerId(),
							outOfBounds));


					if (state.reRolledAction == null &&
						UtilServerReRoll.askForReRollIfAvailable(gameState, actingPlayer, ReRolledActions.SWOOP_DIRECTION, 0,
							false)) {
						step.getResult().setNextAction(StepAction.CONTINUE);
					} else {

						step.publishParameter(StepParameter.from(StepParameterKey.DIRECTION, state.swoopDirection));
						step.publishParameter(StepParameter.from(StepParameterKey.USING_SWOOP, true));
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}

				return false;
			}

		});
	}
}
