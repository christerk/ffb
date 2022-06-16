package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.ttm.StepSwoop;
import com.fumbbl.ffb.server.step.action.ttm.StepSwoop.StepState;
import com.fumbbl.ffb.skill.bb2020.Swoop;

@RulesCollection(Rules.BB2020)
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

				if (swoopingPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) {

					state.coordinateFrom = game.getFieldModel().getPlayerCoordinate(swoopingPlayer);

					Direction scatterDirection;
					int scatterRoll = step.getGameState().getDiceRoller().rollThrowInDirection();
					if (state.coordinateFrom.getX() < state.coordinateTo.getX()) {
						scatterDirection = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.EAST, scatterRoll);
					} else if (state.coordinateFrom.getX() > state.coordinateTo.getX()) {
						scatterDirection = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.WEST, scatterRoll);
					} else if (state.coordinateFrom.getY() < state.coordinateTo.getY()) {
						scatterDirection = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.SOUTH, scatterRoll);
					} else { // coordinateFrom.getY() > coordinateTo.getY()
						scatterDirection = DiceInterpreter.getInstance().interpretThrowInDirectionRoll(Direction.NORTH, scatterRoll);
					}
					step.publishParameter(StepParameter.from(StepParameterKey.DIRECTION, scatterDirection));
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}
