package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportAnimosityRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.pass.StepAnimosity;
import com.fumbbl.ffb.server.step.action.pass.StepAnimosity.StepState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Animosity;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(Rules.BB2016)
public class AnimosityBehaviour extends SkillBehaviour<Animosity> {
	public AnimosityBehaviour() {
		super();

		registerModifier(new StepModifier<StepAnimosity, StepAnimosity.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepAnimosity step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}

			@Override
			public boolean handleExecuteStepHook(StepAnimosity step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();

				Player<?> thrower = game.getThrower();
				FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
				Player<?> catcher = game.getPlayerById(state.catcherId);
				if (actingPlayer.isSufferingAnimosity()) {
					if ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER)) {
						boolean targetAvailable = false;
						Player<?>[] targets = UtilPlayer.findAdjacentBlockablePlayers(game, UtilPlayer.findOtherTeam(game, thrower),
							throwerCoordinate);
						for (Player<?> target : targets) {
							targetAvailable |= thrower.getRace().equalsIgnoreCase(target.getRace());
						}
						if (targetAvailable) {
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnFailure);
						}
					} else if ((catcher != null) && !(thrower.getRace().equalsIgnoreCase(catcher.getRace()))) {
						// step END_PASSING will push a new pass sequence onto the stack
						game.setPassCoordinate(null);
						game.getFieldModel().setRangeRuler(null);
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnFailure);
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				} else {
					if (ReRolledActions.ANIMOSITY == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), thrower)) {
							actingPlayer.setSufferingAnimosity(true);
						} else {
							state.doRoll = true;
						}
					} else {
						if ((catcher != null) && (catcher.getRace() != null) && (thrower != null) && (thrower.getRace() != null)) {
							state.doRoll = (UtilCards.hasSkill(thrower, skill)
								&& !(thrower.getRace().equalsIgnoreCase(catcher.getRace()))
								&& thrower.getTeam().getId().equals(catcher.getTeam().getId())
							);
						}
					}
					if (state.doRoll) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollAnimosity();
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (successful) {
							actingPlayer.setSufferingAnimosity(false);
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							if ((ReRolledActions.ANIMOSITY == step.getReRolledAction())
								|| !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), thrower,
								ReRolledActions.ANIMOSITY, minimumRoll, false)) {
								actingPlayer.setSufferingAnimosity(true);
							}
						}
						boolean reRolled = ((ReRolledActions.ANIMOSITY == step.getReRolledAction())
							&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportAnimosityRoll(actingPlayer.getPlayerId(),
							successful, roll, minimumRoll, reRolled, null));
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
					if (actingPlayer.isSufferingAnimosity()) {
						boolean animosityPassPossible = false;
						Team team = game.getTeamHome().hasPlayer(actingPlayer.getPlayer()) ? game.getTeamHome()
							: game.getTeamAway();
						for (Player<?> player : team.getPlayers()) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
							if ((playerState != null) && playerState.hasTacklezones()
								&& StringTool.isEqual(actingPlayer.getRace(), player.getRace())) {
								PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
								if (((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER)
									&& playerCoordinate.isAdjacent(throwerCoordinate))
									|| ((actingPlayer.getPlayerAction() == PlayerAction.PASS)
									&& mechanic.findPassingDistance(game, throwerCoordinate, playerCoordinate, false) != null)) {
									animosityPassPossible = true;
									break;
								}
							}
						}
						if (animosityPassPossible) {
							// step END_PASSING will push a new pass sequence onto the stack
							game.setPassCoordinate(null);
							game.getFieldModel().setRangeRuler(null);
						}
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnFailure);
					}
				}
				return false;
			}
		});
	}
}
