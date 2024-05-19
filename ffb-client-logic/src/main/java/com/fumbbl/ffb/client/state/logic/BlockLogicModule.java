package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.Set;

public class BlockLogicModule extends LogicModule {

	public BlockLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			if (isSufferingBloodLust(actingPlayer)) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			} else if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
				client.getCommunication().sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.BLITZ_MOVE,
					actingPlayer.isJumping());
			} else {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			}
		} else {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			addAll(genericBlockActions());
		}};
	}

	public Set<ClientAction> genericBlockActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.BLOCK);
			add(ClientAction.STAB);
			add(ClientAction.CHAINSAW);
			add(ClientAction.PROJECTILE_VOMIT);
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
		switch (action) {
			case END_MOVE:
				client.getCommunication().sendActingPlayer(null, null, false);
				break;
			case MOVE:
				client.getCommunication().sendActingPlayer(player, PlayerAction.MOVE, client.getGame().getActingPlayer().isJumping());
				break;
			default:
				performBlockAction(player, action);
				break;
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
		}
	}

	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(game.getTurnMode());
	}

	public boolean isBlockable(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		return isValidBlitzTarget(game, pPlayer) && defenderCoordinate.isAdjacent(attackerCoordinate)
			&& (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null);
	}

	public boolean isValidBlitzTarget(Game game, Player<?> pPlayer) {
		if (pPlayer != null) {
			FieldModel fieldModel = game.getFieldModel();
			PlayerState defenderState = fieldModel.getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
			return (defenderState.canBeBlocked() && game.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
				&& (fieldModel.getTargetSelectionState() == null || pPlayer.getId().equals(fieldModel.getTargetSelectionState().getSelectedPlayerId())));
		}
		return false;
	}

	public boolean isSufferingBloodLust(ActingPlayer actingPlayer) {
		return actingPlayer.isSufferingBloodLust();
	}

}
