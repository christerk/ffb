package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;

public class DialogChatCommands extends Dialog {

	private static final String _FONT_BOLD_OPEN = "<font face=\"Sans Serif\" size=\"-1\"><b>";
	private static final String _FONT_BOLD_CLOSE = "</b></font>";
	private static final String _FONT_MEDIUM_BOLD_OPEN = "<font face=\"Sans Serif\"><b>";
	private static final String _FONT_OPEN = "<font face=\"Sans Serif\" size=\"-1\">";
	private static final String _FONT_CLOSE = "</font>";

	public DialogChatCommands(FantasyFootballClient pClient) {

		super(pClient, "Chat Commands", true);

		JScrollPane aboutPane = new JScrollPane(createEditorPane());

		Game game = getClient().getGame();
		if (game.isTesting()) {
			aboutPane.setPreferredSize(new Dimension(aboutPane.getPreferredSize().width + 100, 500));
		} else {
			aboutPane.setPreferredSize(new Dimension(aboutPane.getPreferredSize().width + 10, 300));
		}

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(aboutPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		pack();

		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.CHAT_COMMANDS;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JEditorPane createEditorPane() {

		JEditorPane aboutPane = new JEditorPane();
		aboutPane.setEditable(false);
		aboutPane.setContentType("text/html");

		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<body>\n");

		html.append("<table border=\"0\" cellspacing=\"1\" width=\"100%\">\n");
		html.append("<tr><td>").append(_FONT_OPEN);
		html.append(
			"All commands can be given in the chat input field.<br><i>Spectator sounds are played with a 10 sec. enforced &quot;cooldown&quot; time between sounds.</i>");
		html.append(_FONT_CLOSE).append("</td></tr>\n");
		html.append("</table>\n<br>\n");
		html.append("<table border=\"1\" cellspacing=\"0\" width=\"100%\">\n");
		html.append("<tr>\n");
		html.append("<td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Spectator Commands")
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		html.append("</tr>\n");
		html.append(commandLine("/aah", "aaahing spectators"));
		html.append(commandLine("/boo", "booing spectators"));
		html.append(commandLine("/cheer", "cheering spectators"));
		html.append(commandLine("/clap", "clapping spectators"));
		html.append(commandLine("/crickets", "the sound of crickets in the grass"));
		html.append(commandLine("/hurt", "ouch"));
		html.append(commandLine("/laugh", "laughing spectators"));
		html.append(commandLine("/ooh", "ooohing spectators"));
		html.append(commandLine("/shock", "shocked, gasping spectators"));
		html.append(commandLine("/stomp", "spectators stomping their feet"));
		html.append(commandLine("/specs", "shows all logged in spectators by name - can also be used by playing coaches"));
		html.append("</table>\n");

		if (getClient().getMode() == ClientMode.SPECTATOR) {
			html.append("<br>\n<table border=\"1\" cellspacing=\"0\" width=\"100%\">\n");
			html.append("<tr>\n");
			html.append("<td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Admin Commands").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
			html.append("</tr>\n");
			html.append(commandLine("/action_used_home &lt;true|false&gt; &lt;actionlist&gt;", "sets the given actions to used/not used for the home team.<br/>Actions can be [blitz|foul|handOver|pass|throwBomb|kickTeamMate]."));
			html.append(commandLine("/action_used_away &lt;true|false&gt; &lt;actionlist&gt;", "sets the given actions to used/not used for the away team.<br/>Actions can be [blitz|foul|handOver|pass|throwBomb|kickTeamMate]."));
			html.append(commandLine("/box_home &lt;box&gt; &lt;playerlist&gt;", "puts players on home team into a box (rsv, ko, bh, si, rip, ban)."));
			html.append(commandLine("/box_away &lt;box&gt; &lt;playerlist&gt;", "puts players on away team into a box (rsv, ko, bh, si, rip, ban)."));
			html.append(commandLine("/injury_home &lt;injury&gt; &lt;playerlist&gt;", "gives players on away team an injury (up to two) of that type (ni, -ma, -av, -ag or -st).<br/>"
				+ "Any other string will remove all injuries from the player."));
			html.append(commandLine("/injury_away &lt;injury&gt; &lt;playerlist&gt;", "gives players on away team an injury (up to two) of that type (ni, -ma, -av, -ag or -st).<br/>"
				+ "Any other string will remove all injuries from the player."));
			html.append(commandLine("/move_ball &lt;direction&gt; &lt;distance&gt;", "moves ball to given direction by given distance. Direction can be [north|northwest|west..."));
			html.append(commandLine("/move_player_home &lt;player nr&gt; &lt;direction&gt; &lt;distance&gt;", "sets player on home team to given direction by given distance.<br/>Direction can be [north|northwest|west..."));
			html.append(commandLine("/move_player_away &lt;player nr&gt; &lt;x coordinate&gt; &lt;y coordinate&gt;", "sets player on away team to given direction by given distance.<br/>Direction can be [north|northwest|west..."));
			html.append(commandLine("/reset_stack", "reset the internal state to await a new player selection.<br/>" +
				"Use with care and not during special sequences like kick-off events or dump off."));

			html.append(commandLine("/playing_home", "sets home team as playing team."));
			html.append(commandLine("/playing_away", "sets away team as playing team."));

			html.append(commandLine("/prone_home &lt;playerlist&gt;", "places players on home team prone."));
			html.append(commandLine("/prone_away &lt;playerlist&gt;", "places players on away team prone."));
			html.append(commandLine("/set_activated_home &lt;true|false&gt;  &lt;playerlist&gt;", "sets players on home team to (not) activated for this turn."));
			html.append(commandLine("/set_activated_away &lt;true|false&gt;  &lt;playerlist&gt;", "sets players on away team to (not) activated for this turn."));
			html.append(commandLine("/set_ball &lt;x coordinate&gt; &lt;y coordinate&gt;", "moves ball to square defined by coordinate."));
			html.append(commandLine("/set_player_home &lt;player nr&gt; &lt;x coordinate&gt; &lt;y coordinate&gt;", "sets player on home team on square defined by coordinate."));
			html.append(commandLine("/set_player_away &lt;player nr&gt; &lt;x coordinate&gt; &lt;y coordinate&gt;", "sets player on away team on square defined by coordinate."));
			html.append(commandLine("/skill_home &lt;add|remove&gt; &lt;skillname&gt; &lt;playerlist&gt;", "adds or removes a skill to players on home team.<br>skill names use underscores instead of blanks (diving_tackle, pass_block)."));
			html.append(commandLine("/skill_away &lt;add|remove&gt; &lt;skillname&gt; &lt;playerlist&gt;", "adds or removes a skill to players on away team.<br>skill names use underscores instead of blanks (diving_tackle, pass_block)."));
			html.append(commandLine("/stat_home &lt;stat&gt; &lt;value&gt; &lt;playerlist&gt;", "sets a stat of players on home team to the given value."));
			html.append(commandLine("/stat_away &lt;stat&gt; &lt;value&gt; &lt;playerlist&gt;", "sets a stat of players on away team to the given value."));
			html.append(commandLine("/stun_home &lt;playerlist&gt;", "stuns players on home team."));
			html.append(commandLine("/stun_away &lt;playerlist&gt;", "stuns players on away team."));

			html.append(commandLine("/turn_home &lt;turnnr&gt;", "jumps to the turn with the given number for home team."));
			html.append(commandLine("/turn_away &lt;turnnr&gt;", "jumps to the turn with the given number for away team."));
			html.append(commandLine("/weather &lt;shortname&gt;", "changes the weather to nice, sunny, rain, heat or blizzard."));

			html.append("</table>\n");
		}

		if (getClient().getGame().isTesting()) {
			html.append("<br>\n<table border=\"1\" cellspacing=\"0\" width=\"100%\">\n");
			html.append("<tr>\n");
			html.append("<td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Test Commands").append(_FONT_BOLD_CLOSE)
				.append("</td>\n");
			html.append("</tr>\n");

			html.append(commandLine("/action_used &lt;true|false&gt; &lt;actionlist&gt;", "sets the given actions to used/not used for your team.<br/>Actions can be [blitz|foul|handOver|pass|throwBomb|kickTeamMate]."));
			html.append(commandLine("/box &lt;box&gt; &lt;playerlist&gt;", "puts players on your team into a box (rsv, ko, bh, si, rip, ban)."));
			html.append(commandLine("/card &lt;add|remove&gt; &lt;shortCardName&gt;", "adds or removes card with given name to/from your inducements."));
			html.append(commandLine("/gameid", "outputs the current game id."));
			html.append(commandLine("/injury &lt;injury&gt; &lt;playerlist&gt;", "gives players on your team an injury of that type (ni, -ma, -av, -ag or -st)"));
			html.append(commandLine("/move_ball &lt;direction&gt; &lt;distance&gt;", "moves ball to given direction by given distance. Direction can be [north|northwest|west..."));
			html.append(commandLine("/move_player &lt;player nr&gt; &lt;direction&gt; &lt;distance&gt;", "sets player on your team to given direction by given distance.<br/>Direction can be [north|northwest|west..."));
			html.append(commandLine("/option &lt;name&gt; &lt;value&gt;", "sets option with given name to given value."));
			html.append(commandLine("/options", "lists all available options with their current value."));
			html.append(commandLine("/prayer &lt;roll&gt; [&lt;playerNr|skillName&gt;]", "adds the prayer for this roll to your team.<br>" +
				"&lt;playerNumber|skillName&gt; is needed for prayers that require player or skill selection<br/>(would show a dialog during the game).<br>" +
				"For a playerNumber the player with the corresponding roster number gains the prayer effect,<br/>if they are eligible for selection.<br>" +
				"For the skillName that skill will be assigned to a random player if it is a primary skill.<br>" +
				"skillName has the same format when adding removing skills<br>" +
				"Should the prayer, player or the skill not be available for any reason there will be no effect."));
			html.append(commandLine("/prone &lt;playerlist&gt;", "places players on your team prone."));
			html.append(commandLine("/redeploy [brachname]", "shuts down the server and redeploys the current HEAD of the given branch. Defaults to master<br>" +
				"Only available for DEV users and if the server is in test mode."));


			html.append(commandLine("/roll &lt;roll1&gt; &lt;roll2&gt; &lt;roll3&gt; &lt;...&gt;", "determines the next dicerolls (separated by space).<br>" +
				"General roll values 1 2 3 4..... etc<br>" +
				"Directional roll values n ne e se s sw w nw<br>" +
				"Block roll values skull bothdown push stumble pow."));
			html.append(commandLine("/roll clear", "removes all queued dicerolls from the RNG."));
			html.append(commandLine("/set_activated &lt;true|false&gt;  &lt;playerlist&gt;", "sets players on your team to (not) activated for this turn."));
			html.append(commandLine("/set_ball &lt;x coordinate&gt; &lt;y coordinate&gt;", "moves ball to square defined by coordinate."));
			html.append(commandLine("/set_player &lt;player nr&gt; &lt;x coordinate&gt; &lt;y coordinate&gt;", "sets player on your team on square defined by coordinate."));
			html.append(commandLine("/skill &lt;add|remove&gt; &lt;skillname&gt; &lt;playerlist&gt;", "adds or removes a skill to players on your team.<br>skill names use underscores instead of blanks (diving_tackle, pass_block)."));
			html.append(commandLine("/stat &lt;stat&gt; &lt;value&gt; &lt;playerlist&gt;", "sets a stat of players on your team to the given value."));
			html.append(commandLine("/sound &lt;name&gt;", "plays the given sound."));
			html.append(commandLine("/sounds", "lists all available sounds."));
			html.append(commandLine("/stun &lt;playerlist&gt;", "stuns players on your team."));

			html.append(commandLine("/turn &lt;turnnr&gt;", "jumps to the turn with the given number."));
			html.append(commandLine("/weather &lt;shortname&gt;", "changes the weather to nice, sunny, rain, heat or blizzard."));
			html.append("<tr>\n");
			html.append("<td colspan=\"2\">").append(_FONT_OPEN).append(
					"<i>Commands accepting a playerlist may either list player numbers separated by space or use the keyword &quot;all&quot; for all players.</i>")
				.append(_FONT_CLOSE).append("</td>\n");
			html.append("</tr>\n");
			html.append("</table>\n");
		}

		html.append("</body>\n");
		html.append("</html>");

		aboutPane.setText(html.toString());
		aboutPane.setCaretPosition(0);

		return aboutPane;

	}

	private String commandLine(String command, String description) {
		return "<tr>\n" +
			"<td>" + _FONT_BOLD_OPEN + command + _FONT_BOLD_CLOSE +
			"</td>\n" +
			"<td>" + _FONT_OPEN + description +
			_FONT_CLOSE + "</td>\n" +
			"</tr>\n";
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		// Dimension menuBarSize = getClient().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}

}
