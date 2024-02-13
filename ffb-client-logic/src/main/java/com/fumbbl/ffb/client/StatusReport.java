package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandLeave;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.StringTool;

import java.net.InetAddress;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatusReport {

	private final FantasyFootballClient fClient;
	private int fIndent;
	public boolean fPettyCashReportReceived;
	public boolean fCardsBoughtReportReceived;
	public boolean inducementsBoughtReportReceived;
	private boolean initialized;

	private final Map<ReportId, ReportMessageBase<? extends IReport>> messageRenderers;
	
	public StatusReport(FantasyFootballClient pClient) {
		fClient = pClient;
		messageRenderers = new HashMap<>();
	}

	@SuppressWarnings("rawtypes")
	public void init(GameOptions gameOptions) {
		if (initialized) {
			return;
		}
		initialized = true;
		Collection<ReportMessageBase> renderers = new Scanner<>(ReportMessageBase.class).getSubclasses(gameOptions);

		for (ReportMessageBase<?> renderer : renderers) {
			try {
				renderer.setStatusReport(this);
				ReportId reportId = renderer.getClass().getAnnotation(ReportMessageType.class).value();
				messageRenderers.put(reportId, renderer);
			} catch (SecurityException | IllegalArgumentException e) {
				System.err.println(e.getMessage());
			} catch (NullPointerException npe) {
				System.err.println("Error processing " + renderer.getClass().getName());
			}
		}
	}
	
	public FantasyFootballClient getClient() {
		return fClient;
	}

	public int getIndent() {
		return fIndent;
	}

	public void setIndent(int pIndent) {
		fIndent = pIndent;
	}

	public void reportVersion() {
		println(0, "FantasyFootballClient Version " + FantasyFootballConstants.VERSION);
	}

	public void reportConnecting(InetAddress pInetAddress, int pPort) {
		println(0, "Connecting to " + pInetAddress + ":" + pPort + " ...");
	}

	public void reportIconLoadFailure(URL pIconUrl) {
		println(0, "Unable to load icon from URL " + pIconUrl + ".");
	}

	public void reportTimeout() {
		println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.BOLD, "The timelimit has been reached for this turn.");
	}

	public void reportGameName(String pGameName) {
		if (StringTool.isProvided(pGameName)) {
			println(0, "You have started a new game named \"" + pGameName + "\".");
		}
	}

	public void reportSocketClosed() {
		if (getClient().getMode() != ClientMode.REPLAY) {
			println(ParagraphStyle.SPACE_ABOVE, TextStyle.NONE, "The connection to the server has been closed.");
			println(ParagraphStyle.SPACE_BELOW, TextStyle.NONE, "To re-connect you need to restart the client.");
		}
	}

	public void reportConnectionEstablished(boolean pSuccesful) {
		if (pSuccesful) {
			println(0, "Connection established.");
		} else {
			println(0, "Cannot connect to the server.");
		}
	}

	public void reportJoin(ServerCommandJoin pJoinCommand) {
		Game game = getClient().getGame();
		if (ClientMode.PLAYER == pJoinCommand.getClientMode()) {
			printCoachName(game, pJoinCommand.getCoach());
			println(0, TextStyle.BOLD, " joins the game.");
		} else if (ClientMode.SPECTATOR == pJoinCommand.getClientMode()) {
			print(0, "Spectator ");
			print(0, pJoinCommand.getCoach());
			println(0, " joins the game.");
		}
	}

	public void reportLeave(ServerCommandLeave pLeaveCommand) {
		Game game = getClient().getGame();
		if (ClientMode.PLAYER == pLeaveCommand.getClientMode()) {
			printCoachName(game, pLeaveCommand.getCoach());
			println(0, TextStyle.BOLD, " leaves the game.");
		} else if (ClientMode.SPECTATOR == pLeaveCommand.getClientMode()) {
			print(0, "Spectator ");
			print(0, pLeaveCommand.getCoach());
			println(0, " leaves the game.");
		}
	}

	private void printCoachName(Game game, String coach) {
		print(0, TextStyle.BOLD, "Player ");
		if (game.getTeamHome() != null && StringTool.isProvided(game.getTeamHome().getCoach())
			&& game.getTeamHome().getCoach().equals(coach)) {
			print(0, TextStyle.HOME_BOLD, coach);
		} else {
			print(0, TextStyle.AWAY_BOLD, coach);
		}
	}

	public String formatRollModifiers(RollModifier<?>[] pRollModifiers) {
		StringBuilder modifiers = new StringBuilder();
		if (ArrayTool.isProvided(pRollModifiers)) {
			for (RollModifier<?> rollModifier : pRollModifiers) {
				if (rollModifier.getModifier() > 0) {
					modifiers.append(" - ");
				} else {
					modifiers.append(" + ");
				}
				if (!rollModifier.isModifierIncluded()) {
					modifiers.append(Math.abs(rollModifier.getModifier())).append(" ");
				}
				modifiers.append(rollModifier.getName());
			}
		}
		return modifiers.toString();
	}

	public void reportStatus(ServerStatus status) {
		println();
		println(0, TextStyle.BOLD, status.getMessage());
		println();
	}

	public void report(IReport report) {
		ReportMessageBase<? extends IReport> renderer = messageRenderers.get(report.getId());
		if (renderer != null) {
			renderer.renderMessage(fClient.getGame(), report);
		} else {
			throw new IllegalStateException("Unhandled report id " + report.getId().getName() + ".");
		}
	}
	
	public void report(ReportList pReportList) {
		for (IReport report : pReportList.getReports()) {
			report(report);
		}
	}

	private ParagraphStyle findParagraphStyle(int pIndent) {
		ParagraphStyle paragraphStyle = null;
		switch (pIndent) {
			case 0:
				paragraphStyle = ParagraphStyle.INDENT_0;
				break;
			case 1:
				paragraphStyle = ParagraphStyle.INDENT_1;
				break;
			case 2:
				paragraphStyle = ParagraphStyle.INDENT_2;
				break;
			case 3:
				paragraphStyle = ParagraphStyle.INDENT_3;
				break;
			case 4:
				paragraphStyle = ParagraphStyle.INDENT_4;
				break;
			case 5:
				paragraphStyle = ParagraphStyle.INDENT_5;
				break;
			case 6:
				paragraphStyle = ParagraphStyle.INDENT_6;
				break;
		}
		return paragraphStyle;
	}

	public void print(int pIndent, TextStyle pTextStyle, String pText) {
		print(findParagraphStyle(pIndent), pTextStyle, pText);
	}

	public void print(int pIndent, String pText) {
		print(findParagraphStyle(pIndent), null, pText);
	}

	public void print(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
		getClient().getUserInterface().getLog().append(pParagraphStyle, pTextStyle, pText);
	}

	public void println(int pIndent, TextStyle pTextStyle, String pText) {
		println(findParagraphStyle(pIndent), pTextStyle, pText);
	}

	public void println(int pIndent, String pText) {
		println(findParagraphStyle(pIndent), null, pText);
	}

	public void println() {
		println(findParagraphStyle(0), null, null);
	}

	public void println(ParagraphStyle pParagraphStyle, TextStyle pTextStyle, String pText) {
		print(pParagraphStyle, pTextStyle, pText);
		getClient().getUserInterface().getLog().append(null, null, null);
	}

	public void print(int pIndent, boolean pBold, Player<?> pPlayer) {
		if (pPlayer != null) {
			ParagraphStyle paragraphStyle = findParagraphStyle(pIndent);
			if (getClient().getGame().getTeamHome().hasPlayer(pPlayer)) {
				if (pBold) {
					print(paragraphStyle, TextStyle.HOME_BOLD, pPlayer.getName());
				} else {
					print(paragraphStyle, TextStyle.HOME, pPlayer.getName());
				}
			} else {
				if (pBold) {
					print(paragraphStyle, TextStyle.AWAY_BOLD, pPlayer.getName());
				} else {
					print(paragraphStyle, TextStyle.AWAY, pPlayer.getName());
				}
			}
		}
	}

	public void printTeamName(Game pGame, boolean pBold, String pTeamId) {
		if (pGame.getTeamHome().getId().equals(pTeamId)) {
			if (pBold) {
				print(getIndent() + 1, TextStyle.HOME_BOLD, pGame.getTeamHome().getName());
			} else {
				print(getIndent() + 1, TextStyle.HOME, pGame.getTeamHome().getName());
			}
		} else {
			if (pBold) {
				print(getIndent() + 1, TextStyle.AWAY_BOLD, pGame.getTeamAway().getName());
			} else {
				print(getIndent() + 1, TextStyle.AWAY, pGame.getTeamAway().getName());
			}
		}
	}

}
