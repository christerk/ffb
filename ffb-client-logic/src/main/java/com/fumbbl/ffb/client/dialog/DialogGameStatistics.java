package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class DialogGameStatistics extends Dialog {

	private static final String _FONT_BOLD_CLOSE = "</b></font>";
	private final String fontBoldOpen;
	private final String fontLargeBoldOpen;
	private final String fontBlueBoldOpen;
	private final String fontLargeBlueBoldOpen;
	private final String fontRedBoldOpen;
	private final String fontLargeRedBoldOpen;

	private static final String _BACKGROUND_COLOR_SPP = "#e0e0e0";
	private static final String _BACKGROUND_COLOR_TOTAL_SPP = "#c0c0c0";

	public DialogGameStatistics(FantasyFootballClient pClient) {

		super(pClient, "Game Statistics", true);

		DimensionProvider dimensionProvider = pClient.getUserInterface().getUiDimensionProvider();
		fontBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(9) + "px\"><b>";
		fontLargeBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(13) + "px\"><b>";
		fontBlueBoldOpen = "<font color=\"#0000ff\" face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(9) + "px\"><b>";
		fontLargeBlueBoldOpen = "<font color=\"#0000ff\" face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(13) + "px\"><b>";
		fontRedBoldOpen = "<font color=\"#ff0000\" face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(9) + "px\"><b>";
		fontLargeRedBoldOpen = "<font color=\"#ff0000\" face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(13) + "px\"><b>";

		Game game = getClient().getGame();

		JScrollPane teamComparisonPane = new JScrollPane(createTeamComparisonEditorPane());
		JScrollPane teamHomePane = new JScrollPane(createTeamEditorPane(game.getTeamHome()));
		teamHomePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		teamHomePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane teamAwayPane = new JScrollPane(createTeamEditorPane(game.getTeamAway()));
		teamAwayPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		teamHomePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JTabbedPane fTabbedPane = new JTabbedPane();
		fTabbedPane.addTab("Team Comparison", teamComparisonPane);
		fTabbedPane.addTab("<html><font color=\"#ff0000\">Details Home Team</font></html>", teamHomePane);
		fTabbedPane.addTab("<html><font color=\"#0000ff\">Details Away Team</font></html>", teamAwayPane);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(fTabbedPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		setPreferredSize(teamAwayPane);
		setPreferredSize(teamHomePane);
		setPreferredSize(teamComparisonPane);
		setPreferredSize(this);
		pack();
		setLocationToCenter();
	}

	public DialogId getId() {
		return DialogId.GAME_STATISTICS;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JEditorPane createTeamComparisonEditorPane() {

		JEditorPane teamComparisonPane = new JEditorPane();
		teamComparisonPane.setEditable(false);
		teamComparisonPane.setContentType("text/html");

		Game game = getClient().getGame();
		GameResult gameResult = game.getGameResult();
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		StringBuilder statistics = new StringBuilder();
		statistics.append("<html>\n");
		statistics.append("<body>\n");
		statistics.append(fontLargeBoldOpen).append("Team Comparison").append(_FONT_BOLD_CLOSE).append("<br/>\n");
		statistics.append("<br/>\n");
		statistics.append("<table border=\"1\" cellspacing=\"0\">\n");
		statistics.append("<tr>\n");
		statistics.append("  <td></td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen).append(game.getTeamHome().getName())
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen).append(game.getTeamAway().getName())
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Team Value").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(StringTool.formatThousands(gameResult.getTeamResultHome().getTeamValue() / 1000)).append("k")
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(StringTool.formatThousands(gameResult.getTeamResultAway().getTeamValue() / 1000)).append("k")
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Completions").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalCompletions()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalCompletions()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Touchdowns").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().getScore()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().getScore()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Deflections").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
			.append(gameResult.getTeamResultHome().totalDeflections()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
			.append(gameResult.getTeamResultAway().totalDeflections()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Interceptions").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalInterceptions()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalInterceptions()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Casualties").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalCasualties()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalCasualties()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Earned SPPs").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalEarnedSpps()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalEarnedSpps()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Suffered Injuries")
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen);
		statistics.append(gameResult.getTeamResultHome().getBadlyHurtSuffered());
		statistics.append(" / ").append(gameResult.getTeamResultHome().getSeriousInjurySuffered());
		statistics.append(" / ").append(gameResult.getTeamResultHome().getRipSuffered());
		statistics.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen);
		statistics.append(gameResult.getTeamResultAway().getBadlyHurtSuffered());
		statistics.append(" / ").append(gameResult.getTeamResultAway().getSeriousInjurySuffered());
		statistics.append(" / ").append(gameResult.getTeamResultAway().getRipSuffered());
		statistics.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Passing").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalPassing()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalPassing()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Carried Ball").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalRushing()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalRushing()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Blocks").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().totalBlocks()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().totalBlocks()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Fouls").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
			.append(gameResult.getTeamResultHome().totalFouls()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
			.append(gameResult.getTeamResultAway().totalFouls()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append(gameMechanic.fanModificationName()).append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		StringBuilder fanFactorHome = new StringBuilder();
		fanFactorHome.append(gameMechanic.fans(game.getTeamHome()));
		int fanModifierHome = gameMechanic.fanModification(gameResult.getTeamResultHome());
		if (fanModifierHome > 0) {
			fanFactorHome.append(" + ").append(fanModifierHome);
		}
		if (fanModifierHome < 0) {
			fanFactorHome.append(" - ").append(Math.abs(fanModifierHome));
		}
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen).append(fanFactorHome)
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		StringBuilder fanFactorAway = new StringBuilder();
		fanFactorAway.append(gameMechanic.fans(game.getTeamAway()));
		int fanModifierAway = gameMechanic.fanModification(gameResult.getTeamResultAway());
		if (fanModifierAway > 0) {
			fanFactorAway.append(" + ").append(fanModifierAway);
		}
		if (fanModifierAway < 0) {
			fanFactorAway.append(" - ").append(Math.abs(fanModifierAway));
		}
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen).append(fanFactorAway)
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Assistant Coaches")
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
			.append(game.getTeamHome().getAssistantCoaches()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
			.append(game.getTeamAway().getAssistantCoaches()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Cheerleaders").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen).append(game.getTeamHome().getCheerleaders())
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(game.getTeamAway().getCheerleaders()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Spectators").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
				.append(gameResult.getTeamResultHome().getSpectators()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
				.append(gameResult.getTeamResultAway().getSpectators()).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		statistics.append("<tr>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append(gameMechanic.audienceName()).append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
			.append(gameMechanic.audience(gameResult.getTeamResultHome())).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
			.append(gameMechanic.audience(gameResult.getTeamResultAway())).append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("</tr>\n");
		if ((gameResult.getTeamResultHome().getWinnings() > 0) || (gameResult.getTeamResultAway().getWinnings() > 0)) {
			statistics.append("<tr>\n");
			statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Winnings").append(_FONT_BOLD_CLOSE)
					.append("</td>\n");
			statistics.append("  <td align=\"right\">").append(fontRedBoldOpen)
					.append(gameResult.getTeamResultHome().getWinnings()).append(_FONT_BOLD_CLOSE).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(fontBlueBoldOpen)
					.append(gameResult.getTeamResultAway().getWinnings()).append(_FONT_BOLD_CLOSE).append("</td>\n");
			statistics.append("</tr>\n");
		}
		statistics.append("</table>\n");
		statistics.append("</body>\n");
		statistics.append("</html>");

		teamComparisonPane.setText(statistics.toString());
		return teamComparisonPane;

	}

	private JEditorPane createTeamEditorPane(Team pTeam) {

		JEditorPane teamPane = new JEditorPane();
		teamPane.setEditable(false);
		teamPane.setContentType("text/html");

		Game game = getClient().getGame();
		GameResult gameResult = game.getGameResult();
		boolean homeTeam = (game.getTeamHome() == pTeam);

		StringBuilder statistics = new StringBuilder();
		statistics.append("<html>\n");
		statistics.append("<body>\n");
		statistics.append(homeTeam ? fontLargeRedBoldOpen : fontLargeBlueBoldOpen).append(pTeam.getName())
				.append(_FONT_BOLD_CLOSE).append("<br/>\n");
		statistics.append("<br/>\n");
		statistics.append("<table border=\"1\" cellspacing=\"0\">\n");
		// Player Cps TDs Int Cas MVP SPP Pass Rush Blocks Fouls
		statistics.append("<tr>\n");
		statistics.append("  <td colspan=\"2\" align=\"left\">").append(fontBoldOpen).append("Player")
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Turns").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Cps").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Cps+").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("TDs").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Def").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Int").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Cas").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("Cas+").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
			.append(fontBoldOpen).append("MVP").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_TOTAL_SPP).append("\">")
			.append(fontBoldOpen).append("SPP").append(_FONT_BOLD_CLOSE).append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Pass").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Rush").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Blocks").append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		statistics.append("  <td align=\"right\">").append(fontBoldOpen).append("Fouls").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
		statistics.append("</tr>\n");
		for (Player<?> player : pTeam.getPlayers()) {
			PlayerResult playerResult = gameResult.getPlayerResult(player);
			statistics.append("<tr>\n");
			statistics.append("  <td align=\"right\">").append(fontBoldOpen).append(player.getNr())
				.append(_FONT_BOLD_CLOSE).append("</td>\n");
			statistics.append("  <td align=\"left\">").append(homeTeam ? fontRedBoldOpen : fontBlueBoldOpen)
				.append(player.getName()).append(_FONT_BOLD_CLOSE).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(formatPlayerStat(playerResult.getTurnsPlayed()))
				.append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getCompletions())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getCompletionsWithAdditionalSpp())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getTouchdowns())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getDeflections())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getInterceptions())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getCasualties())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getCasualtiesWithAdditionalSpp())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_SPP).append("\">")
				.append(formatPlayerStat(playerResult.getPlayerAwards())).append("</td>\n");
			statistics.append("  <td align=\"right\" bgcolor=\"").append(_BACKGROUND_COLOR_TOTAL_SPP).append("\">")
				.append(formatPlayerStat(playerResult.totalEarnedSpps())).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(formatPlayerStat(playerResult.getPassing())).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(formatPlayerStat(playerResult.getRushing())).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(formatPlayerStat(playerResult.getBlocks())).append("</td>\n");
			statistics.append("  <td align=\"right\">").append(formatPlayerStat(playerResult.getFouls())).append("</td>\n");
			statistics.append("</tr>\n");
		}
		statistics.append("</table>\n");
		statistics.append("</p>\n");
		statistics.append("</body>\n");
		statistics.append("</html>");

		teamPane.setText(statistics.toString());
		// teamPane.setPreferredSize(new Dimension(teamPane.getPreferredSize().width +
		// 20, 250));
		teamPane.setCaretPosition(0);
		return teamPane;

	}

	private String formatPlayerStat(int pPlayerStat) {
		StringBuilder formattedStat = new StringBuilder();
		if (pPlayerStat != 0) {
			formattedStat.append(fontBoldOpen).append(pPlayerStat).append(_FONT_BOLD_CLOSE);
		} else {
			formattedStat.append("&nbsp;");
		}
		return formattedStat.toString();
	}

	private void setPreferredSize(Component component) {
		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();

		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		component.setPreferredSize(new Dimension(this.getPreferredSize().width, frameSize.height - menuBarSize.height - dimensionProvider.scale(150)));
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			menuBarSize.height + 5);

	}

}
