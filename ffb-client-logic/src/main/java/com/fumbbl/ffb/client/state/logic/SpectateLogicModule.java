package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class SpectateLogicModule extends LogicModule {

	public SpectateLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SPECTATE;
	}

	@Override
	public void setUp() {
		super.setUp();
		if (canSwitchToSpectate()) {
			client.setMode(ClientMode.SPECTATOR);
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	public boolean canSwitchToSpectate() {
		return (client.getGame().getFinished() != null) && (ClientMode.PLAYER == client.getMode());
	}

	public void startReplay() {
		client.getReplayer().start();
		client.getReplayer().setControl(true);
		client.getReplayer().getReplayControl().setActive(true);
		client.updateClientState();
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in spectate context");
	}

}
