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
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class TouchbackLogicModule extends LogicModule {

	private boolean fTouchbackToAnyField;

	public TouchbackLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.TOUCHBACK;
	}

	@Override
	public void postInit() {
		super.postInit();
		// check if there are players on the field to give the ball to
		Game game = client.getGame();
		fTouchbackToAnyField = true;
		for (Player<?> player : game.getTeamHome().getPlayers()) {
			if (isPlayerSelectable(player)) {
				fTouchbackToAnyField = false;
				break;
			}
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (fTouchbackToAnyField || isPlayerSelectable(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		if (fTouchbackToAnyField) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		if ((fTouchbackToAnyField || isPlayerSelectable(pPlayer))) {
			FieldCoordinate touchBackCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			client.getCommunication().sendTouchback(touchBackCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		if (fTouchbackToAnyField) {
			client.getCommunication().sendTouchback(pCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	private boolean isPlayerSelectable(Player<?> pPlayer) {
		boolean selectable = false;
		if (pPlayer != null) {
			Game game = client.getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			selectable = ((playerState != null) && playerState.hasTacklezones() && game.getTeamHome().hasPlayer(pPlayer)
					&& !pPlayer.hasSkillProperty(NamedProperties.preventHoldBall));
		}
		return selectable;
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in touchback context");
	}

}
