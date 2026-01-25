package com.fumbbl.ffb.client.state.logic.mixed;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;


public class PutridRegurgitationBlitzLogicModule extends BlitzLogicModule {
	public PutridRegurgitationBlitzLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.PUTRID_REGURGITATION_BLITZ;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				return InteractionResult.selectAction(actionContext(actingPlayer));
			} else {
				if (PlayerAction.PUTRID_REGURGITATION_BLITZ == actingPlayer.getPlayerAction()
					&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
					&& extension.isBlockable(game, player)) {
					extension.block(actingPlayer.getPlayerId(), player, false, false, true, false, false);
					return InteractionResult.handled();
				}
			}
			return InteractionResult.ignore();
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			add(ClientAction.JUMP);
			add(ClientAction.BOUNDING_LEAP);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (action) {
			case PROJECTILE_VOMIT:
				communication.sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.PUTRID_REGURGITATION_BLITZ, actingPlayer.isJumping());
				break;
			case MOVE:
				communication.sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.PUTRID_REGURGITATION_MOVE, actingPlayer.isJumping());
				break;
			default:
				super.performAvailableAction(player, action);
				break;
		}
	}

	@Override
	public boolean isPutridRegurgitationAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !isMoveAvailable(actingPlayer) && actingPlayer.hasBlocked() && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(game.getActingTeam()), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.PUTRID_REGURGITATION_BLITZ && extension.isBlockable(game, player)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.reset();
		}
	}

	@Override
	public boolean isMoveAvailable(ActingPlayer actingPlayer) {
		return actingPlayer.getPlayerAction() == PlayerAction.PUTRID_REGURGITATION_BLITZ;
	}
}
