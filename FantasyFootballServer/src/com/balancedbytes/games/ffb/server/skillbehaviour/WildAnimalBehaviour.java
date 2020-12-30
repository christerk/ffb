package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.factory.ReRolledActionFactory;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportConfusionRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.StepWildAnimal;
import com.balancedbytes.games.ffb.server.step.action.common.StepWildAnimal.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.WildAnimal;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class WildAnimalBehaviour extends SkillBehaviour<WildAnimal> {
	public WildAnimalBehaviour() {
		super();

		registerModifier(new StepModifier<StepWildAnimal, StepWildAnimal.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepWildAnimal step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepWildAnimal step,
					com.balancedbytes.games.ffb.server.step.action.common.StepWildAnimal.StepState state) {

				ActionStatus status = ActionStatus.SUCCESS;
				Game game = step.getGameState().getGame();
				if (!game.getTurnMode().checkNegatraits()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				if (playerState.isConfused()) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeConfused(false));
				}
				if (playerState.isHypnotized()) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeHypnotized(false));
				}
				if (UtilCards.hasSkill(game, actingPlayer, skill)) {
					boolean doRoll = true;
					ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(game, skill);
					if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							status = ActionStatus.FAILURE;
							cancelPlayerAction(step);
						}
					} else {
						doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, skill);
					}
					if (doRoll) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean goodConditions = ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
								|| (actingPlayer.getPlayerAction() == PlayerAction.BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK)
								|| (actingPlayer.getPlayerAction() == PlayerAction.STAND_UP_BLITZ));
						int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(goodConditions);
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (!successful) {
							status = ActionStatus.FAILURE;
							if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction()))
									&& UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											reRolledAction, minimumRoll, false)) {
								status = ActionStatus.WAITING_FOR_RE_ROLL;
							} else {
								cancelPlayerAction(step);
							}
						}
						boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())
								&& (step.getReRollSource() != null));
						step.getResult().addReport(
								new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
					}
				}
				if (status == ActionStatus.SUCCESS) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (status == ActionStatus.FAILURE) {
						step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
					}
				}

				return false;
			}
		});
	}

	private void cancelPlayerAction(StepWildAnimal step) {
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (actingPlayer.getPlayerAction()) {
		case BLITZ:
		case BLITZ_MOVE:
		case KICK_TEAM_MATE:
		case KICK_TEAM_MATE_MOVE:
			game.getTurnData().setBlitzUsed(true);
			break;
		case PASS:
		case PASS_MOVE:
		case THROW_TEAM_MATE:
		case THROW_TEAM_MATE_MOVE:
			game.getTurnData().setPassUsed(true);
			break;
		case HAND_OVER:
		case HAND_OVER_MOVE:
			game.getTurnData().setHandOverUsed(true);
			break;
		case FOUL:
		case FOUL_MOVE:
			game.getTurnData().setFoulUsed(true);
			break;
		default:
			break;
		}
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if (actingPlayer.isStandingUp()) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
					playerState.changeBase(PlayerState.PRONE).changeActive(false));
		} else {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
					playerState.changeBase(PlayerState.STANDING).changeActive(false));
		}
		game.setPassCoordinate(null);
		step.getResult().setSound(SoundId.ROAR);
	}

	public int getByteArraySerializationVersion() {
		return 1;
	}

}