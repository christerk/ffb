package com.balancedbytes.games.ffb.client.dialog;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

@SuppressWarnings("serial")
public class DialogChatCommands extends Dialog {
  
  private static final String _FONT_BOLD_OPEN = "<font face=\"Sans Serif\" size=\"-1\"><b>";
  private static final String _FONT_BOLD_CLOSE = "</b></font>";
  private static final String _FONT_MEDIUM_BOLD_OPEN = "<font face=\"Sans Serif\"><b>";
  private static final String _FONT_OPEN = "<font face=\"Sans Serif\" size=\"-1\">";
  private static final String _FONT_SMALL_OPEN = "<font face=\"Sans Serif\" size=\"-2\">";
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
    
    Game game = getClient().getGame();
    JEditorPane aboutPane = new JEditorPane();
    aboutPane.setEditable(false);
    aboutPane.setContentType("text/html");

    StringBuilder html = new StringBuilder();
    html.append("<html>\n");
    html.append("<body>\n");
    html.append("<table border=\"0\" cellspacing=\"1\" width=\"100%\">\n");
    html.append("<tr><td>").append(_FONT_OPEN);
    html.append("All commands can be given in the chat input field.<br><i>Spectator sounds are played with a 10 sec. enforced &quot;cooldown&quot; time between sounds.</i>");
    html.append(_FONT_CLOSE).append("</td></tr>\n");
    html.append("</table>\n<br>\n");
    html.append("<table border=\"1\" cellspacing=\"0\" width=\"100%\">\n");
    html.append("<tr>\n");
    html.append("<td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Spectator Commands").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/aah").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("aaahing spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/boo").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("booing spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/cheer").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("cheering spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/clap").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("clapping spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/crickets").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("the sound of crickets in the grass").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/laugh").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("laughing spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/ooh").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("ooohing spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/shock").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("shocked, gasping spectators").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/stomp").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("spectators stomping their feet").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("<td>").append(_FONT_BOLD_OPEN).append("/specs").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("<td>").append(_FONT_OPEN).append("shows all logged in spectators by name").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    if (game.isTesting()) {
      html.append("<br>\n<table border=\"1\" cellspacing=\"0\" width=\"100%\">\n");
      html.append("<tr>\n");
      html.append("<td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Test Commands").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td valign=\"top\">").append(_FONT_BOLD_OPEN).append("/animation &lt;type&gt &lt;x&gt; &lt;y&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("plays animation at the given coordinate.<br>").append(_FONT_CLOSE);
      html.append(_FONT_SMALL_OPEN).append("(bombExplosion, spellFireball, spellLightning, kickoffBlitz, kickoffBlizzard, kickoffBrilliantCoaching,<br>");
      html.append(" kickoffCheeringFans, kickoffGetTheRef, kickoffHighKick, kickoffNice, kickoffPerfectDefense, kickoffPitchInvasion,<br>");
      html.append(" kickoffPouringRain, kickoffQuickSnap, kickoffRiot, kickoffSwelteringHeat, kickoffThrowARock, kickoffVerySunny)").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/box &lt;box&gt &lt;playerlist&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("puts players on your team into a box (rsv, ko, bh, si, rip, ban).").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/card &lt;shortname&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("adds card with given shortname to your inducements.").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/injury &lt;injury&gt; &lt;playerlist&gt; ").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("gives players on your team an injury of that type (ni, -ma, -av, -ag or -st)").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/option &lt;name&gt; &lt;value&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("sets option with given name to given value").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/options").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("lists all available options with their current value").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/prone &lt;playerlist&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("places players on your team prone.").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/roll &lt;roll1&gt; &lt;roll2&gt; &lt;roll3&gt; &lt;...&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("determines the next dicerolls (separated by space)").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/roll clear").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("removes all queued dicerolls from the RNG").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td valign=\"top\">").append(_FONT_BOLD_OPEN).append("/skill &lt;add|remove&gt; &lt;skillname&gt; &lt;playerlist&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("adds or removes a skill to players on your team.<br>skill names use underscores instead of blanks (diving_tackle, pass_block).").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/stat &lt;stat&gt; &lt;value&gt; &lt;playerlist&gt; ").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("sets a stat of players on your team to the given value").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/stun &lt;playerlist&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("stuns players on your team.").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/turn &lt;turnnr&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("jumps to the turn with the given number").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td>").append(_FONT_BOLD_OPEN).append("/weather &lt;shortname&gt;").append(_FONT_BOLD_CLOSE).append("</td>\n");
      html.append("<td>").append(_FONT_OPEN).append("changes the weather to nice, sunny, rain, heat or blizzard").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("<tr>\n");
      html.append("<td colspan=\"2\">").append(_FONT_OPEN).append("<i>Commands accepting a playerlist may either list player numbers separated by space or use the keyword &quot;all&quot; for all players.</i>").append(_FONT_CLOSE).append("</td>\n");
      html.append("</tr>\n");
      html.append("</table>\n");
    }
    html.append("</body>\n");
    html.append("</html>");
    
    aboutPane.setText(html.toString());
    aboutPane.setCaretPosition(0);
    
    return aboutPane;
    
  }
  
  protected void setLocationToCenter() {
    Dimension dialogSize = getSize();
    Dimension frameSize = getClient().getUserInterface().getSize();
    Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
    // Dimension menuBarSize = getClient().getGameMenuBar().getSize();
    setLocation((frameSize.width - dialogSize.width) / 2, ((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
  }
    
}
