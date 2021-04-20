package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.GameList;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ClientCommandJoin;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.db.DbQueryFactory;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.query.DbPasswordForCoachQuery;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestCheckAuthorization;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerJoin extends ServerCommandHandler {

	protected ServerCommandHandlerJoin(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_JOIN;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		ClientCommandJoin joinCommand = (ClientCommandJoin) pReceivedCommand.getCommand();
		ServerCommunication communication = getServer().getCommunication();

		if ((joinCommand.getGameId() > 0) || StringTool.isProvided(joinCommand.getGameName())) {

			if (ServerMode.FUMBBL == getServer().getMode()) {

				getServer().getRequestProcessor()
						.add(new FumbblRequestCheckAuthorization(pReceivedCommand.getSession(), joinCommand.getCoach(),
								joinCommand.getPassword(), joinCommand.getGameId(), joinCommand.getGameName(), joinCommand.getTeamId(),
								joinCommand.getClientMode()));

			} else {

				DbQueryFactory statementFactory = getServer().getDbQueryFactory();
				DbPasswordForCoachQuery passwordQuery = (DbPasswordForCoachQuery) statementFactory
						.getStatement(DbStatementId.PASSWORD_FOR_COACH_QUERY);
				String password = passwordQuery.execute(joinCommand.getCoach());

				if (joinCommand.getPassword().equals(password)) {
					InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(
							joinCommand.getGameId(), joinCommand.getGameName(), joinCommand.getCoach(), joinCommand.getTeamId(),
							joinCommand.getClientMode());
					ReceivedCommand receivedJoinApproved = new ReceivedCommand(joinApprovedCommand,
							pReceivedCommand.getSession());
					communication.handleCommand(receivedJoinApproved);

				} else {
					communication.sendStatus(pReceivedCommand.getSession(), ServerStatus.ERROR_WRONG_PASSWORD, null);
				}

			}

		} else {

			GameList gameList = null;
			GameCache gameCache = getServer().getGameCache();
			if (ClientMode.PLAYER == joinCommand.getClientMode()) {
				gameList = gameCache.findOpenGamesForCoach(joinCommand.getCoach());
			} else {
				gameList = gameCache.findActiveGames();
			}
			communication.sendGameList(pReceivedCommand.getSession(), gameList);

		}

		return true;

	}

}
