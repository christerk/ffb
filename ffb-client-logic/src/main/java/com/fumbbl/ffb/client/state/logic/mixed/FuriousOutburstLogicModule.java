package com.fumbbl.ffb.client.state.logic.mixed;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

public class FuriousOutburstLogicModule extends LogicModule {
	public FuriousOutburstLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}


	@Override
	public ClientStateId getId() {
		return ClientStateId.FURIOUS_OUTBURST;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		if (isEligible(pCoordinate)) {
			client.getCommunication().sendFieldCoordinate(pCoordinate);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		if (isEligible(pCoordinate)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.invalid();
		}
	}

	private boolean isEligible(FieldCoordinate coordinate) {
		return client.getGame().getFieldModel().getMoveSquare(coordinate) != null;
	}

	@Override
	protected void performAvailableAction(Player<?> pPlayer, ClientAction action) {
		if (pPlayer != null) {
			switch (action) {
				case END_MOVE:
					client.getCommunication().sendActingPlayer(null, null, false);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		actionContext.add(ClientAction.END_MOVE);
		if (actingPlayer.hasActed()) {
			actionContext.add(Influences.HAS_ACTED);
		}
		return actionContext;
	}
}
