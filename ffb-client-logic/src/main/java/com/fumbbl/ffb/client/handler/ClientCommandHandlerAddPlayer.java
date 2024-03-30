package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandAddPlayer;
import com.fumbbl.ffb.util.UtilBox;

public class ClientCommandHandlerAddPlayer extends ClientCommandHandler {

	protected ClientCommandHandlerAddPlayer(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADD_PLAYER;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		ServerCommandAddPlayer addPlayerCommand = (ServerCommandAddPlayer) pNetCommand;

		Game game = getClient().getGame();

		Team team = game.getTeamHome().getId().equals(addPlayerCommand.getTeamId()) ? game.getTeamHome()
				: game.getTeamAway();
		Player<?> oldPlayer = team.getPlayerById(addPlayerCommand.getPlayer().getId());
		if (oldPlayer == null) {
			team.addPlayer(addPlayerCommand.getPlayer());
			RosterPosition rosterPosition = team.getRoster().getPositionById(addPlayerCommand.getPlayer().getPositionId());
			addPlayerCommand.getPlayer().updatePosition(rosterPosition, game.getRules(), game.getId());
		} else if (oldPlayer instanceof RosterPlayer) {
			oldPlayer.init(addPlayerCommand.getPlayer(), game.getRules());
		} else {
			return false;
		}

		game.getFieldModel().setPlayerState(addPlayerCommand.getPlayer(), addPlayerCommand.getPlayerState());
		UtilBox.putPlayerIntoBox(game, addPlayerCommand.getPlayer());

		PlayerResult playerResult = game.getGameResult().getPlayerResult(addPlayerCommand.getPlayer());
		playerResult.setSendToBoxReason(addPlayerCommand.getSendToBoxReason());
		playerResult.setSendToBoxTurn(addPlayerCommand.getSendToBoxTurn());
		playerResult.setSendToBoxHalf(addPlayerCommand.getSendToBoxHalf());

		// team.setTeamValue(UtilTeamValue.findTeamValue(team, game));

		if (pMode == ClientCommandHandlerMode.PLAYING) {
			refreshGameMenuBar();
			refreshFieldComponent();
			refreshSideBars();
		}

		return true;

	}

}
