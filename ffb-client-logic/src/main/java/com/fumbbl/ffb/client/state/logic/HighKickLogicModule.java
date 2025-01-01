package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class HighKickLogicModule extends LogicModule {

	private FieldCoordinate fOldCoordinate;

	public HighKickLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.HIGH_KICK;
	}

	@Override
	public void postInit() {
		super.postInit();
		fOldCoordinate = null;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		if (isPlayerSelectable(pPlayer)) {
			Game game = client.getGame();
			Player<?> oldPlayer = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
			if (pPlayer != oldPlayer) {
				if ((oldPlayer != null) && (fOldCoordinate != null)) {
					client.getCommunication().sendSetupPlayer(oldPlayer, fOldCoordinate);
				}
				fOldCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
				client.getCommunication().sendSetupPlayer(pPlayer, game.getFieldModel().getBallCoordinate());
			} else {
				client.getCommunication().sendSetupPlayer(pPlayer, fOldCoordinate);
				fOldCoordinate = null;
			}
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (isPlayerSelectable(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}

	private boolean isPlayerSelectable(Player<?> pPlayer) {
		boolean selectable = false;
		if (pPlayer != null) {
			Game game = client.getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			selectable = ((playerState != null) && playerState.isActive() && game.getTeamHome().hasPlayer(pPlayer));
		}
		return selectable;
	}

	@Override
	public void endTurn() {
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		client.getClientData().setEndTurnButtonHidden(true);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}
	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		// no actions
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in high kick context");
	}

}
