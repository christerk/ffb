package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * @author Kalimar
 */
public class UtilClientMarker {

	public static void showMarkerPopup(final FantasyFootballClient pClient, final Player<?> pPlayer, int pX, int pY) {
		if (pPlayer != null) {
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode();
			final JPopupMenu markerPopupMenu = new JPopupMenu();
			PlayerMarker playerMarker = persistMarker ? pClient.getGame().getFieldModel().getPlayerMarker(pPlayer.getId()) : pClient.getGame().getFieldModel().getTransientPlayerMarker(pPlayer.getId());
			String markerText = (playerMarker != null) ? playerMarker.getHomeText() : null;
			final JTextField markerField = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu,
				"Mark Player", StringTool.print(markerText), pX, pY);
			markerField.addActionListener(pActionEvent -> {
				String text = StringTool.print(markerField.getText());
				if (persistMarker) {
					pClient.getCommunication().sendSetMarker(pPlayer.getId(), text);
				} else {
					if (StringTool.isProvided(text)) {
						PlayerMarker transientMarker = new PlayerMarker(pPlayer.getId());
						transientMarker.setHomeText(text);
						pClient.getGame().getFieldModel().addTransient(transientMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(transientMarker);
					} else {
						pClient.getGame().getFieldModel().removeTransient(playerMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(playerMarker);
					}
					pClient.getUserInterface().getFieldComponent().refresh();
				}
				markerPopupMenu.setVisible(false);
			});
		}
	}

	public static void showMarkerPopup(final FantasyFootballClient pClient, final FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Game game = pClient.getGame();
			final JPopupMenu markerPopupMenu = new JPopupMenu();
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode();
			FieldMarker fieldMarker = persistMarker ? game.getFieldModel().getFieldMarker(pCoordinate) : game.getFieldModel().getTransientFieldMarker(pCoordinate);
			String markerText = (fieldMarker != null) ? fieldMarker.getHomeText() : null;
			DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate.getX(), pCoordinate.getY(), false);

			final JTextField markerField = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu,
				"Mark Field", StringTool.print(markerText), dimension.width, dimension.height);
			markerField.addActionListener(pActionEvent -> {
				String text = StringTool.print(markerField.getText());
				if (persistMarker) {
					pClient.getCommunication().sendSetMarker(pCoordinate, text);
				} else {
					if (StringTool.isProvided(text)) {
						pClient.getUserInterface().getFieldComponent().getLayerMarker().removeFieldMarker(fieldMarker, true);
						FieldMarker transientMarker = new FieldMarker(pCoordinate, text, null);
						pClient.getGame().getFieldModel().addTransient(transientMarker);
						pClient.getUserInterface().getFieldComponent().getLayerMarker().drawFieldMarker(transientMarker, true);
					} else {
						pClient.getGame().getFieldModel().removeTransient(fieldMarker);
						pClient.getUserInterface().getFieldComponent().getLayerMarker().removeFieldMarker(fieldMarker, true);
					}
					pClient.getUserInterface().getFieldComponent().refresh();
				}
				markerPopupMenu.setVisible(false);
			});
		}
	}

	private static JTextField createMarkerPopup(FieldComponent pFieldComponent, JPopupMenu pPopupMenu, String pTitle,
			String pMarkerText, int pX, int pY) {
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
