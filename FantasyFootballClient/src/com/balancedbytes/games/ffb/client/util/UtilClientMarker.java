package com.balancedbytes.games.ffb.client.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class UtilClientMarker {
  
  private static final int _FIELD_SQUARE_SIZE = 30;
  
  public static void showMarkerPopup(final FantasyFootballClient pClient, final Player pPlayer, int pX, int pY) {
    if (pPlayer != null) {
      final JPopupMenu markerPopupMenu = new JPopupMenu();
      PlayerMarker playerMarker = pClient.getGame().getFieldModel().getPlayerMarker(pPlayer.getId());
      String markerText = (playerMarker != null) ? playerMarker.getHomeText() : null;
      final JTextField markerField = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu, "Mark Player", StringTool.print(markerText), pX, pY);
      markerField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent pActionEvent) {
          String text = StringTool.print(markerField.getText());
          pClient.getCommunication().sendSetMarker(pPlayer.getId(), text);
          markerPopupMenu.setVisible(false);
        }
      });
    }
  }

  public static void showMarkerPopup(final FantasyFootballClient pClient, final FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      Game game = pClient.getGame();
      final JPopupMenu markerPopupMenu = new JPopupMenu();
      FieldMarker fieldMarker = game.getFieldModel().getFieldMarker(pCoordinate);
      String markerText = (fieldMarker != null) ? fieldMarker.getHomeText() : null;
      int x = (pCoordinate.getX() + 1) * _FIELD_SQUARE_SIZE;
      int y = (pCoordinate.getY() + 1) * _FIELD_SQUARE_SIZE;
      final JTextField markerField = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu, "Mark Field", StringTool.print(markerText), x, y);
      markerField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent pActionEvent) {
          String text = StringTool.print(markerField.getText());
          pClient.getCommunication().sendSetMarker(pCoordinate, text);
          markerPopupMenu.setVisible(false);
        }
      });
    }
  }

  private static JTextField createMarkerPopup(FieldComponent pFieldComponent, JPopupMenu pPopupMenu, String pTitle, String pMarkerText, int pX, int pY) {
    if (StringTool.isProvided(pTitle)) {
      pPopupMenu.add(new JLabel(pTitle));
    }
    pPopupMenu.setLayout(new BoxLayout(pPopupMenu, BoxLayout.X_AXIS));
    pPopupMenu.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    JTextField markerField = new JTextField(7);
    if (StringTool.isProvided(pMarkerText)) {
      markerField.setText(pMarkerText);
    }
    pPopupMenu.add(Box.createHorizontalStrut(5));
    pPopupMenu.add(markerField);
    pPopupMenu.show(pFieldComponent, pX, pY);
    markerField.selectAll();
    markerField.requestFocus();
    return markerField;
  }

}
