package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

public class BlitzLogicModule extends MoveLogicModule {
	protected final BlockLogicExtension extension;

	public BlitzLogicModule(FantasyFootballClient client) {
		super(client);
		extension = new BlockLogicExtension(client);
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
			return super.playerInteraction(player);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			} else {
				if (!actingPlayer.hasBlocked()) {
					return extension.playerInteraction(player, true);
				}
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && extension.isBlockable(game, player)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
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
			add(ClientAction.GORED_BY_THE_BULL);
			addAll(extension.availableActions());
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
				case GORED_BY_THE_BULL:
					//TODO almost identical to block kind logic but is not sending the block command probably because we handle frenzy blocks here?
					if (isGoredAvailable(client.getGame())) {
						UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canAddBlockDie).ifPresent(goredSkill ->
							communication.sendUseSkill(goredSkill, true, actingPlayer.getPlayerId()));
					}
					break;
				default:
					extension.performAvailableAction(player, action);
					break;
			}
		}
	}

	public boolean isGoredAvailable(Game game) {
		return extension.isGoredAvailable(game);
	}
}
