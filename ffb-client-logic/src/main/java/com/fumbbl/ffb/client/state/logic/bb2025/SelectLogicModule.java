package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class SelectLogicModule extends LogicModule {

	public SelectLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void setUp() {
		super.setUp();
		client.getGame().setDefenderId(null);
		client.getClientData().clearBlockDiceResult();
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if (game.getTeamHome().hasPlayer(player) && playerState.isActive()) {
			return InteractionResult.selectAction(actionContext(player));
		}
		return InteractionResult.ignore();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.BLOCK);
			add(ClientAction.BLITZ);
			add(ClientAction.FRENZIED_RUSH);
			add(ClientAction.FOUL);
			add(ClientAction.MOVE);
			add(ClientAction.STAND_UP);
			add(ClientAction.STAND_UP_BLITZ);
			add(ClientAction.HAND_OVER);
			add(ClientAction.PASS);
			add(ClientAction.THROW_TEAM_MATE);
			add(ClientAction.KICK_TEAM_MATE);
			add(ClientAction.RECOVER);
			add(ClientAction.MULTIPLE_BLOCK);
			add(ClientAction.BOMB);
			add(ClientAction.GAZE);
			add(ClientAction.GAZE_ZOAT);
			add(ClientAction.SHOT_TO_NOTHING);
			add(ClientAction.SHOT_TO_NOTHING_BOMB);
			add(ClientAction.BEER_BARREL_BASH);
			add(ClientAction.ALL_YOU_CAN_EAT);
			add(ClientAction.KICK_EM_BLOCK);
			add(ClientAction.KICK_EM_BLITZ);
			add(ClientAction.THE_FLASHING_BLADE);
			add(ClientAction.VICIOUS_VINES);
			add(ClientAction.FURIOUS_OUTBURST);
			add(ClientAction.SECURE_THE_BALL);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in select context");
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case BLOCK:
					communication.sendActingPlayer(player, PlayerAction.BLOCK, false);
					break;
				case BLITZ:
					communication.sendActingPlayer(player, PlayerAction.BLITZ_MOVE, false);
					break;
				case FRENZIED_RUSH:
					communication.sendActingPlayer(player, PlayerAction.BLITZ_MOVE, false);
					Skill skill = player.getSkillWithProperty(NamedProperties.canGainFrenzyForBlitz);
					communication.sendUseSkill(skill, true, player.getId());
					break;
				case FOUL:
					communication.sendActingPlayer(player, PlayerAction.FOUL_MOVE, false);
					break;
				case MOVE:
					communication.sendActingPlayer(player, PlayerAction.MOVE, false);
					break;
				case SECURE_THE_BALL:
					if (isSecureTheBallActionAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.SECURE_THE_BALL, false);
					}
					break;
				case STAND_UP:
					communication.sendActingPlayer(player, PlayerAction.STAND_UP, false);
					break;
				case STAND_UP_BLITZ:
					communication.sendActingPlayer(player, PlayerAction.STAND_UP_BLITZ, false);
					break;
				case HAND_OVER:
					communication.sendActingPlayer(player, PlayerAction.HAND_OVER_MOVE, false);
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case PASS:
					communication.sendActingPlayer(player, PlayerAction.PASS_MOVE, false);
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case THROW_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.THROW_TEAM_MATE_MOVE, false);
					break;
				case KICK_TEAM_MATE:
					communication.sendActingPlayer(player, PlayerAction.KICK_TEAM_MATE_MOVE, false);
					break;
				case RECOVER:
					communication.sendActingPlayer(player, PlayerAction.REMOVE_CONFUSION, false);
					break;
				case MULTIPLE_BLOCK:
					communication.sendActingPlayer(player, PlayerAction.MULTIPLE_BLOCK, false);
					break;
				case BOMB:
					if (isThrowBombActionAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_BOMB, false);
					}
					break;
				case GAZE:
					communication.sendActingPlayer(player, PlayerAction.GAZE_MOVE, false);
					break;
				case GAZE_ZOAT:
					communication.sendActingPlayer(player, PlayerAction.GAZE_MOVE, false);
					Skill gazeSkill = player.getSkillWithProperty(NamedProperties.canGainGaze);
					communication.sendUseSkill(gazeSkill, true, player.getId());
					break;
				case SHOT_TO_NOTHING:
					communication.sendActingPlayer(player, PlayerAction.PASS_MOVE, false);
					Skill stnSkill = player.getSkillWithProperty(NamedProperties.canGainHailMary);
					communication.sendUseSkill(stnSkill, true, player.getId());
					if (isTreacherousAvailable(player)) {
						Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, player.getId());
					}
					break;
				case SHOT_TO_NOTHING_BOMB:
					if (isThrowBombActionAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_BOMB, false);
						Skill stnbSkill = player.getSkillWithProperty(NamedProperties.canGainHailMary);
						communication.sendUseSkill(stnbSkill, true, player.getId());
						if (isTreacherousAvailable(player)) {
							Skill treacherous = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
							communication.sendUseSkill(treacherous, true, player.getId());
						}
					}
					break;
				case BEER_BARREL_BASH:
					if (isBeerBarrelBashAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THROW_KEG, false);
					}
					break;
				case ALL_YOU_CAN_EAT:
					if (isAllYouCanEatAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.ALL_YOU_CAN_EAT, false);
					}
					break;
				case KICK_EM_BLOCK:
					if (isKickEmBlockAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.KICK_EM_BLOCK, false);
					}
					break;
				case KICK_EM_BLITZ:
					if (isKickEmBlitzAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.KICK_EM_BLITZ, false);
					}
					break;
				case THE_FLASHING_BLADE:
					if (isFlashingBladeAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.THE_FLASHING_BLADE, false);
					}
					break;
				case VICIOUS_VINES:
					if (isViciousVinesAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.VICIOUS_VINES, false);
					}
					break;
				case FURIOUS_OUTBURST:
					if (isFuriousOutburstAvailable(player)) {
						communication.sendActingPlayer(player, PlayerAction.FURIOUS_OUTPBURST, false);
					}
					break;
				default:
					break;
			}
		}
	}

	protected ActionContext actionContext(Player<?> player) {
		ActionContext context = new ActionContext();
		Game game = client.getGame();

		boolean treacherousAvailable = isTreacherousAvailable(player);
		if (treacherousAvailable) {
			context.add(Influences.BALL_ACTIONS_DUE_TO_TREACHEROUS);
		}

		if (isBlockActionAvailable(player)) {
			findAlternateBlockActions(player).forEach(context::add);
			context.add(ClientAction.BLOCK);
		}
		if (isMultiBlockActionAvailable(player)) {
			context.add(ClientAction.MULTIPLE_BLOCK);
		}
		if (isThrowBombActionAvailable(player)) {
			context.add(ClientAction.BOMB);
			if (UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGainHailMary)) {
				context.add(ClientAction.SHOT_TO_NOTHING_BOMB);
			}
		}
		if (isHypnoticGazeActionAvailable(true, player, NamedProperties.inflictsConfusion)) {
			context.add(ClientAction.GAZE);
		}
		if (isHypnoticGazeActionAvailable(true, player, NamedProperties.canGainGaze)) {
			context.add(ClientAction.GAZE_ZOAT);
		}
		if (isMoveActionAvailable(player)) {
			context.add(ClientAction.MOVE);
		}
		if (isBlitzActionAvailable(player)) {
			context.add(ClientAction.BLITZ);
			if (UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGainFrenzyForBlitz)) {
				context.add(ClientAction.FRENZIED_RUSH);
			}
		}
		if (isFoulActionAvailable(player)) {
			context.add(ClientAction.FOUL);
		}
		if (isPassActionAvailable(player, treacherousAvailable)) {
			context.add(ClientAction.PASS);
			if (UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGainHailMary)) {
				context.add(ClientAction.SHOT_TO_NOTHING);
			}
		}
		if (isHandOverActionAvailable(player, treacherousAvailable)) {
			context.add(ClientAction.HAND_OVER);
		}
		if (isThrowTeamMateActionAvailable(player)) {
			context.add(ClientAction.THROW_TEAM_MATE);
		}
		if (isKickTeamMateActionAvailable(player)) {
			context.add(ClientAction.KICK_TEAM_MATE);
		}
		if (isBeerBarrelBashAvailable(player)) {
			context.add(ClientAction.BEER_BARREL_BASH);
		}
		if (isAllYouCanEatAvailable(player)) {
			context.add(ClientAction.ALL_YOU_CAN_EAT);
		}
		if (isKickEmBlockAvailable(player)) {
			context.add(ClientAction.KICK_EM_BLOCK);
		}
		if (isKickEmBlitzAvailable(player)) {
			context.add(ClientAction.KICK_EM_BLITZ);
		}
		if (isFlashingBladeAvailable(player)) {
			context.add(ClientAction.THE_FLASHING_BLADE);
		}
		if (isViciousVinesAvailable(player)) {
			context.add(ClientAction.VICIOUS_VINES);
		}
		if (isFuriousOutburstAvailable(player)) {
			context.add(ClientAction.FURIOUS_OUTBURST);
		}
		if (isRecoverFromConfusionActionAvailable(player) || isRecoverFromGazeActionAvailable(player) || isRecoverFromEyeGougeActionAvailable(player)) {
			context.add(ClientAction.RECOVER);
		}
		if (isStandUpActionAvailable(player)
			&& player.hasSkillProperty(NamedProperties.enableStandUpAndEndBlitzAction)
			&& !game.getTurnData().isBlitzUsed()) {
			context.add(ClientAction.STAND_UP_BLITZ);
		}
		if (isStandUpActionAvailable(player)) {
			context.add(ClientAction.STAND_UP);
		}
		if (isSecureTheBallActionAvailable(player)) {
			context.add(ClientAction.SECURE_THE_BALL);
		}
		return context;
	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

	private List<Skill> findAlternateBlockActions(Player<?> player) {
		return player.getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> skill.hasSkillProperty(NamedProperties.providesBlockAlternative))
			.collect(Collectors.toList());
	}

	public boolean isMoveActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null) && playerState.isAbleToMove());
	}
}
