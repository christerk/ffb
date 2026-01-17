package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

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
			add(ClientAction.STAB);
			add(ClientAction.CHAINSAW);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.BREATHE_FIRE);
			add(ClientAction.BLOCK);
			add(ClientAction.BLITZ);
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
				case BREATHE_FIRE:
					communication.sendActingPlayer(player, PlayerAction.BREATHE_FIRE, false);
					break;
				case CHAINSAW:
					communication.sendActingPlayer(player, PlayerAction.CHAINSAW, false);
					break;
				case PROJECTILE_VOMIT:
					communication.sendActingPlayer(player, PlayerAction.PROJECTILE_VOMIT, false);
					break;
				case STAB:
					communication.sendActingPlayer(player, PlayerAction.STAB, false);
					break;
				case BLITZ:
					communication.sendActingPlayer(player, PlayerAction.BLITZ_MOVE, false);
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
			context.add(ClientAction.BLOCK);
		}

		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		boolean specialBlocksAvailable = isSpecialBlockActionAvailable(player, playerState);

		if (specialBlocksAvailable &&
			player.canDeclareSkillAction(NamedProperties.providesStabBlockAlternative, playerState)) {
			context.add(ClientAction.STAB);
		}
		if (specialBlocksAvailable &&
			player.canDeclareSkillAction(NamedProperties.providesChainsawBlockAlternative, playerState)) {
			context.add(ClientAction.CHAINSAW);
		}
		if (specialBlocksAvailable &&
			player.canDeclareSkillAction(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail, playerState)) {
			if (player.hasUnusedSkillProperty(NamedProperties.canUseVomitAfterBlock)) {
				context.add(Influences.VOMIT_DUE_TO_PUTRID_REGURGITATION);
			}
			context.add(ClientAction.PROJECTILE_VOMIT);
		}
		if (specialBlocksAvailable &&
			player.canDeclareSkillAction(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover,
				playerState)) {
			context.add(ClientAction.BREATHE_FIRE);
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
		if (isMoveActionAvailable(player)) {
			context.add(ClientAction.MOVE);
		}
		if (isBlitzActionAvailable(player)) {
			context.add(ClientAction.BLITZ);
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
		if (isRecoverFromConfusionActionAvailable(player) || isRecoverFromGazeActionAvailable(player) ||
			isRecoverFromEyeGougeActionAvailable(player)) {
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

	public boolean isSpecialBlockActionAvailable(Player<?> player, PlayerState playerState) {
		Game game = client.getGame();
		GameMechanic mechanic =
			(GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		if ((playerState != null) && !player.hasSkillProperty(NamedProperties.preventRegularBlockAction)
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& (playerState.getBase() != PlayerState.PRONE)
		) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			int blockablePlayers =
				UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 0);
		}
		return false;
	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

	public boolean isMoveActionAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return ((playerState != null) && playerState.isAbleToMove());
	}
}
