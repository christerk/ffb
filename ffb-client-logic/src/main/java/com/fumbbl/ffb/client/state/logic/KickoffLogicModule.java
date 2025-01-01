package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class KickoffLogicModule extends LogicModule {

	private boolean fKicked;

	public KickoffLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.KICKOFF;
	}

	@Override
	public void postInit() {
		super.postInit();
		fKicked = false;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		if (!fKicked) {
			placeBall(pCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		if (!fKicked) {
			FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(pPlayer);
			placeBall(playerCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	private void placeBall(FieldCoordinate pCoordinate) {
		if ((pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
			client.getGame().getFieldModel().setBallMoving(true);
			client.getGame().getFieldModel().setBallCoordinate(pCoordinate);
		}
	}

	@Override
	public void endTurn() {
		FieldCoordinate ballCoordinate = client.getGame().getFieldModel().getBallCoordinate();
		if ((ballCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinate)) {
			fKicked = true;
			client.getCommunication().sendKickoff(ballCoordinate);
			client.getClientData().setEndTurnButtonHidden(true);
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		if (!fKicked && (pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}

	public boolean turnIsEnding() {
		FieldCoordinate ballCoordinate = client.getGame().getFieldModel().getBallCoordinate();
		return ((ballCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinate));
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in kick off context");
	}

}
