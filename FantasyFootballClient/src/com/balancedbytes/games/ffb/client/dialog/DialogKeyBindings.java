package com.balancedbytes.games.ffb.client.dialog;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

@SuppressWarnings("serial")
public class DialogKeyBindings extends Dialog {
  
  private static final String _FONT_BOLD_OPEN = "<font face=\"Sans Serif\" size=\"-1\"><b>";
  private static final String _FONT_BOLD_CLOSE = "</b></font>";
  private static final String _FONT_MEDIUM_BOLD_OPEN = "<font face=\"Sans Serif\"><b>";
  private static final String _FONT_OPEN = "<font face=\"Sans Serif\" size=\"-1\">";
  private static final String _FONT_CLOSE = "</font>";
  
  public DialogKeyBindings(FantasyFootballClient pClient) {
    
    super(pClient, "Key Bindings", true);
    
    JScrollPane keyBindingsPane = new JScrollPane(createKeyBindingsEditorPane());
    keyBindingsPane.setPreferredSize(new Dimension(keyBindingsPane.getPreferredSize().width + 20, 500));
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
    infoPanel.add(keyBindingsPane);    
    
    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    getContentPane().add(infoPanel);

    pack();
    
    setLocationToCenter();

  }

  public DialogId getId() {
    return DialogId.KEY_BINDINGS;
  }
  
  public void internalFrameClosing(InternalFrameEvent pE) {
    if (getCloseListener() != null) {
      getCloseListener().dialogClosed(this);
    }
  }
  
  private JEditorPane createKeyBindingsEditorPane() {
    
    JEditorPane keyBindingsPane = new JEditorPane();
    keyBindingsPane.setEditable(false);
    keyBindingsPane.setContentType("text/html");

    StringBuilder html = new StringBuilder();
    html.append("<html>\n");
    html.append("<body>\n");
    html.append("<table border=\"1\" cellspacing=\"0\">\n");
    html.append("<tr>\n");
    html.append("  <td colspan=\"3\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Player Moves").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move North").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTH)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 8").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move Northeast").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTHEAST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 9").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move East").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_EAST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 6").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move Southeast").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTHEAST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 3").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move South").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTH)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 2").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move Southwest").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTHWEST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 1").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move West").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_WEST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 4").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Move Northwest").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTHWEST)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append("NUMPAD 7").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    html.append("<br>\n");
    html.append("<table border=\"1\" cellspacing=\"0\">\n");
    html.append("<tr>\n");
    html.append("  <td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Player Selection").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Select Current Player").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_SELECT)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Selection Cycle Left").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_CYCLE_LEFT)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Selection Cycle Right").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_CYCLE_RIGHT)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    html.append("<br>\n");
    html.append("<table border=\"1\" cellspacing=\"0\">\n");
    html.append("<tr>\n");
    html.append("  <td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Player Actions").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Block").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_BLOCK)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Blitz").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_BLITZ)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Move").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_MOVE)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Foul").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_FOUL)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Stand Up").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_STAND_UP)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Hand Over").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_HAND_OVER)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Pass").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_PASS)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Stab").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_STAB)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action Gaze").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_GAZE)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Action End Move").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_END_MOVE)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    html.append("<br>\n");
    html.append("<table border=\"1\" cellspacing=\"0\">\n");
    html.append("<tr>\n");
    html.append("  <td colspan=\"2\">").append(_FONT_MEDIUM_BOLD_OPEN).append("Toolbar &amp; Menu Shortcuts").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("End Turn").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_TOOLBAR_TURN_END)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Load Team Setup").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_MENU_SETUP_LOAD)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("Save Team Setup").append(_FONT_CLOSE).append("</td>\n");
    html.append("  <td>").append(_FONT_BOLD_OPEN).append(getClient().getProperty(IClientProperty.KEY_MENU_SETUP_SAVE)).append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    html.append("<br>\n");
    html.append("<table border=\"1\" cellspacing=\"0\">\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_MEDIUM_BOLD_OPEN).append("Dialogs").append(_FONT_BOLD_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("<tr>\n");
    html.append("  <td>").append(_FONT_OPEN).append("In all dialogs buttons can be activated by the first<br>letter of their label. So &lt;Y&gt; for Yes or &lt;N&gt; for No.<br>Block dices are numbered 1, 2, 3 from left to right<br>and can be activated this way.").append(_FONT_CLOSE).append("</td>\n");
    html.append("</tr>\n");
    html.append("</table>\n");
    html.append("</body>\n");
    html.append("</html>");
    
    keyBindingsPane.setText(html.toString());
    keyBindingsPane.setCaretPosition(0);
    
    return keyBindingsPane;
    
  }
  
  protected void setLocationToCenter() {
    Dimension dialogSize = getSize();
    Dimension frameSize = getClient().getUserInterface().getSize();
    Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
    // Dimension menuBarSize = getClient().getGameMenuBar().getSize();
    setLocation((frameSize.width - dialogSize.width) / 2, ((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
  }
  
}
