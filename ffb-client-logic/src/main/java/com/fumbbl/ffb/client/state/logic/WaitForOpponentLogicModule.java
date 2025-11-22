package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
public class WaitForOpponentLogicModule extends LogicModule {

	public WaitForOpponentLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WAIT_FOR_OPPONENT;
	}

	public void illegalProcedure() {
		client.getCommunication().sendIllegalProcedure();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in waiting context");
	}

	@Override
	public Optional<Player<?>> getPlayer(FieldCoordinate coordinate) {
		List<Player<?>> players = client.getGame().getFieldModel().getPlayers(coordinate);
		if (players == null || players.isEmpty()) {
			return Optional.empty();
		}

		if (players.size() == 1) {
			return Optional.of(players.get(0));
		}

		return Optional.ofNullable(players.stream().filter(player -> client.getGame().getTeamHome().hasPlayer(player)).findFirst()
			.orElse(players.get(players.size() - 1)));
	}
}
