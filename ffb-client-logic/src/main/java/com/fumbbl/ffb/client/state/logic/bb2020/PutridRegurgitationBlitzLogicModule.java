package com.fumbbl.ffb.client.state.logic.bb2020;

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
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			} else {
				if (PlayerAction.PUTRID_REGURGITATION_BLITZ == actingPlayer.getPlayerAction()
					&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)) {
					extension.block(actingPlayer.getPlayerId(), pPlayer, false, false, true, false);
					return new InteractionResult(InteractionResult.Kind.HANDLED);
				}
			}
			return new InteractionResult(InteractionResult.Kind.IGNORE);
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.MOVE);
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
				break;
		}
	}

	@Override
	public boolean isPutridRegurgitationAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return actingPlayer.hasBlocked() && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseVomitAfterBlock)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(game.getActingTeam()), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
	}
}