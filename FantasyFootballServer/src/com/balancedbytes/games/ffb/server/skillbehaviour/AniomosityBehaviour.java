package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.pass.StepAnimosity;
import com.balancedbytes.games.ffb.server.step.action.pass.StepAnimosity.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Animosity;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

@RulesCollection(Rules.COMMON)
public class AniomosityBehaviour extends SkillBehaviour<Animosity> {
	public AniomosityBehaviour() {
		super();

		registerModifier(new StepModifier<StepAnimosity, StepAnimosity.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepAnimosity step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				// TODO Auto-generated method stub
				return null;
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
							state.doRoll = (UtilCards.hasSkill(game, thrower, skill)
									&& !(thrower.getRace().equalsIgnoreCase(catcher.getRace())));
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
									|| !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											ReRolledActions.ANIMOSITY, minimumRoll, false)) {
								actingPlayer.setSufferingAnimosity(true);
							}
						}
						boolean reRolled = ((ReRolledActions.ANIMOSITY == step.getReRolledAction())
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportSkillRoll(ReportId.ANIMOSITY_ROLL, actingPlayer.getPlayerId(),
								successful, roll, minimumRoll, reRolled));
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
