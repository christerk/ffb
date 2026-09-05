package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandBlock;
import com.fumbbl.ffb.net.commands.ClientCommandBlockChoice;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandFollowupChoice;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandPushback;

public class Commands {
	public static ClientCommandActingPlayer selectPlayer(String playerId, PlayerAction action) {
		return new ClientCommandActingPlayer(playerId, action, false);
	}

	public static ClientCommandBlock block(String attackerId, String defenderId) {
		return new ClientCommandBlock(attackerId, defenderId, false, false, false, false, false);
	}

	public static ClientCommandBlockChoice blockChoice(int index) {
		return new ClientCommandBlockChoice(index);
	}

	public static ClientCommandPushback pushback(Pushback pushback) {
		return new ClientCommandPushback(pushback);
	}

	public static ClientCommandFollowupChoice followup(boolean follow) {
		return new ClientCommandFollowupChoice(follow);
	}

	public static ClientCommandPass pass(String playerId, FieldCoordinate target) {
		return new ClientCommandPass(playerId, target);
	}

	public static ClientCommandHandOver handOver(String playerId, String catcherId) {
		return new ClientCommandHandOver(playerId, catcherId);
	}

	public static ClientCommandMove move(String playerId, FieldCoordinate coordinateFrom,
		FieldCoordinate... coordinatesTo) {
		return new ClientCommandMove(playerId, coordinateFrom, coordinatesTo, null);
	}

	public static ClientCommandEndTurn endTurn(TurnMode turnMode) {
		return new ClientCommandEndTurn(turnMode, null);
	}

	public static ClientCommandPlayerChoice playerChoice(PlayerChoiceMode mode, Player<?>... players) {
		return new ClientCommandPlayerChoice(mode, players);
	}
}
