package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

public class BlitzLogicModule extends MoveLogicModule {
	public BlitzLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public boolean playerActivationUsed() {
		FieldModel fieldModel = client.getGame().getFieldModel();
		if (fieldModel.getTargetSelectionState() == null) {
			return super.playerActivationUsed();
		}
		return fieldModel.getTargetSelectionState().isCommitted();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			} else {
				if (!actingPlayer.hasBlocked()) {
					return new InteractionResult(InteractionResult.Kind.PERFORM);
				}
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult.Kind playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isBlockable(game, player)) {
			return InteractionResult.Kind.PERFORM;
		} else {
			return InteractionResult.Kind.RESET;
		}
	}

	protected PlayerAction moveAction() {
		return PlayerAction.BLITZ_MOVE;
	}

	protected void sendCommand(ActingPlayer actingPlayer, FieldCoordinate coordinateFrom, FieldCoordinate[] pCoordinates) {
		client.getCommunication().sendPlayerBlitzMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
			add(ClientAction.JUMP);
			add(ClientAction.MOVE);
			add(ClientAction.FUMBLEROOSKIE);
			add(ClientAction.BOUNDING_LEAP);
			add(ClientAction.BLOCK);
			add(ClientAction.STAB);
			add(ClientAction.CHAINSAW);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.GORED_BY_THE_BULL);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.BLACK_INK);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			switch (action) {
				case END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
				case JUMP:
					if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(player, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case MOVE:
					if (actingPlayer.isSufferingBloodLust()) {
						client.getCommunication().sendActingPlayer(player, moveAction(), actingPlayer.isJumping());
					}
					break;
				case FUMBLEROOSKIE:
					communication.sendUseFumblerooskie();
					break;
				case BOUNDING_LEAP:
					isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
						communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
					break;
				default:
					performBlockAction(player, action);
					break;
			}
		}
	}

	protected void performBlockAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();
		switch (action) {
			case BLOCK:
				client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, false);
				break;
			case STAB:
				client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, true, false, false);
				break;
			case CHAINSAW:
				client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, true, false);
				break;
			case PROJECTILE_VOMIT:
				client.getCommunication().sendBlock(actingPlayer.getPlayerId(), player, false, false, true);
				break;
			case TREACHEROUS:
				Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
				communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
				break;
			case WISDOM:
				communication.sendUseWisdom();
				break;
			case RAIDING_PARTY:
				Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
				communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
				break;
			case LOOK_INTO_MY_EYES:
				UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
					.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
				break;
			case BALEFUL_HEX:
				Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
				communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
				break;
			case BLACK_INK:
				Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
				communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
				break;
			case GORED_BY_THE_BULL:
				//TODO almost identical to block kind logic but is not sending the block command probably because we handle frenzy blocks here?
				if (UtilClientStateBlocking.isGoredAvailable(client.getGame())) {
					UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canAddBlockDie).ifPresent(goredSkill ->
						communication.sendUseSkill(goredSkill, true, actingPlayer.getPlayerId()));
				}
				break;
		}
	}
}
