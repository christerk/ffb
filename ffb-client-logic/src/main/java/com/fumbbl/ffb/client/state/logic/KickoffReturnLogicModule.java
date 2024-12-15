package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
public class KickoffReturnLogicModule extends MoveLogicModule {

	public KickoffReturnLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.KICKOFF_RETURN;
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
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			movePlayer(pCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
			add(ClientAction.MOVE);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case MOVE:
					communication.sendActingPlayer(player, PlayerAction.MOVE, false);
					break;
				case END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
			}
		}
	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

}
