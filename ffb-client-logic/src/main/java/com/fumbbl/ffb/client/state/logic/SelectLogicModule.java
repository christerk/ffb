package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
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
	public void postInit() {
		super.postInit();
		client.getGame().setDefenderId(null);
		client.getClientData().clearBlockDiceResult();
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
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
		}};
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
				default:
					break;
			}
		}

	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

	public Set<String> findAlternateBlockActions(Player<?> pPlayer) {
		return pPlayer.getSkillsIncludingTemporaryOnes().stream().filter(skill ->
				skill.hasSkillProperty(NamedProperties.providesBlockAlternative)
					&& SkillUsageType.REGULAR == skill.getSkillUsageType())
			.map(Skill::getName).collect(Collectors.toSet());
	}

	public boolean isBlockActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive() && !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlockAction)
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 0);
		}
		return false;
	}

	public boolean isMultiBlockActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive()
			&& ((UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockMoreThanOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockMoreThanOnce))
			|| (UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)))
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 1);
		}
		return false;
	}

	public boolean isThrowBombActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null)
			&& mechanic.isBombActionAllowed(game.getTurnMode())
			&& !game.getTurnData().isBombUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& !playerState.isProneOrStunned()
			&& pPlayer.hasSkillProperty(NamedProperties.enableThrowBombAction));
	}

	public boolean isMoveActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isAbleToMove());
	}

	public boolean isBlitzActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isBlitzUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED) && (playerState != null)
			&& playerState.isActive() && (playerState.isAbleToMove() || playerState.isRooted())
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlitzAction));
	}

	public boolean isFoulActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.isFoulActionAllowed(game.getTurnMode())
			&& playerState.isActive() && (!game.getTurnData().isFoulUsed() || pPlayer.hasSkillProperty(NamedProperties.allowsAdditionalFoul))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularFoulAction)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = game.getFieldModel().getPlayerState(opponent);
				if (opponentState.canBeFouled()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPassActionAvailable(Player<?> pPlayer, boolean treacherousAvailable) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, pPlayer) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, pPlayer) || treacherousAvailable))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularPassAction));
	}

	public boolean isHandOverActionAvailable(Player<?> pPlayer, boolean treacherousAvailable) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isHandOverUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, pPlayer) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, pPlayer) || treacherousAvailable))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularHandOverAction));
	}

	public boolean isThrowTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState == null) || pPlayer.hasSkillProperty(NamedProperties.preventThrowTeamMateAction)) {
			return false;
		}

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = client.getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeThrown(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = ArrayTool.isProvided(mechanic.findThrowableTeamMates(game, pPlayer));

		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.canThrow(pPlayer) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	public boolean isKickTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (!gameMechanic.isKickTeamMateActionAllowed(game.getTurnMode()) || playerState == null || pPlayer.hasSkillProperty(NamedProperties.preventKickTeamMateAction)) {
			return false;
		}
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = client.getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeKicked(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = false;
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		Player<?>[] adjacentTeamPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(game, pPlayer.getTeam(),
			playerCoordinate, false);
		for (Player<?> adjacentTeamPlayer : adjacentTeamPlayers) {
			if (mechanic.canBeKicked(game, adjacentTeamPlayer)) {
				rightStuffAdjacent = true;
				break;
			}
		}

		return (mechanic.isKtmAvailable(game.getTurnData())
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& pPlayer.hasSkillProperty(NamedProperties.canKickTeamMates) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	public boolean isStandUpActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && (playerState.getBase() == PlayerState.PRONE) && playerState.isActive()
			&& !pPlayer.hasSkillProperty(NamedProperties.preventStandUpAction));
	}

	public boolean isRecoverFromConfusionActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isConfused() && playerState.isActive()
			&& (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromConcusionAction));
	}

	public boolean isRecoverFromGazeActionAvailable(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isHypnotized() && (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromGazeAction));
	}

	public boolean isBeerBarrelBashAvailable(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return game.getTurnMode() == TurnMode.REGULAR && playerState.getBase() == PlayerState.STANDING && UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canThrowKeg);
	}

	public boolean isAllYouCanEatAvailable(Player<?> player) {
		Game game = client.getGame();
		return isThrowBombActionAvailable(player) && game.getTurnMode() == TurnMode.REGULAR
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseThrowBombActionTwice);
	}

	public boolean isKickEmBlockAvailable(Player<?> player) {
		return isKickEmAvailable(player, false);
	}

	public boolean isKickEmBlitzAvailable(Player<?> player) {
		return isKickEmAvailable(player, true);
	}

	private boolean isKickEmAvailable(Player<?> player, boolean moveAllowed) {
		Game game = client.getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		PlayerState playerState = fieldModel.getPlayerState(player);
		if ((playerState != null) && playerState.isActive() && (!game.getTurnData().isBlitzUsed() || !moveAllowed)
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseChainsawOnDownedOpponents) && player.hasSkill(NamedProperties.blocksLikeChainsaw)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = fieldModel.getPlayerState(opponent);
				if (opponentState.canBeFouled() && (moveAllowed || playerCoordinate.isAdjacent(fieldModel.getPlayerCoordinate(opponent)))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isFlashingBladeAvailable(Player<?> player) {
		Game game = client.getGame();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		return (playerState != null) && playerState.isActive()
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& (playerState.getBase() != PlayerState.PRONE)
			&& player.hasUnusedSkillProperty(NamedProperties.canStabAndMoveAfterwards)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player)));
	}

}
