package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.marking.TransientPlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kalimar
 */
public class MarkerService {

	private TransientPlayerMarker.Mode defaultMode = TransientPlayerMarker.Mode.REPLACE;

	public void showMarkerPopup(final FantasyFootballClient pClient, final Player<?> pPlayer, int pX, int pY) {
		if (pPlayer != null) {
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode() && IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(pClient.getProperty(CommonProperty.SETTING_SHOW_AUTO_MARKING_DIALOG));
			final JPopupMenu markerPopupMenu = new JPopupMenu();
			PlayerMarker playerMarker = persistMarker ? pClient.getGame().getFieldModel().getPlayerMarker(pPlayer.getId()) : pClient.getGame().getFieldModel().getTransientPlayerMarker(pPlayer.getId());
			String markerText = (playerMarker != null) ? playerMarker.getHomeText() : null;
			PopupComponents components = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu,
				"Mark Player", StringTool.print(markerText), pX, pY, pClient.getUserInterface().getUiDimensionProvider(), !persistMarker);
			final JTextField markerField = components.textField;
			markerField.addActionListener(pActionEvent -> {
				String text = StringTool.print(markerField.getText());
				if (persistMarker) {
					pClient.getCommunication().sendSetMarker(pPlayer.getId(), text);
				} else {
					if (StringTool.isProvided(text)) {
						TransientPlayerMarker.Mode mode = components.modeBox.getSelectedItem();
						defaultMode = mode;
						TransientPlayerMarker transientMarker = new TransientPlayerMarker(pPlayer.getId(), mode);
						transientMarker.setHomeText(text);
						pClient.getGame().getFieldModel().addTransient(transientMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(transientMarker);
					} else {
						pClient.getGame().getFieldModel().removeTransient((TransientPlayerMarker) playerMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(playerMarker);
					}
					pClient.getUserInterface().getFieldComponent().refresh();
				}
				markerPopupMenu.setVisible(false);
			});
		}
	}

	public void showMarkerPopup(final FantasyFootballClient pClient, final FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Game game = pClient.getGame();
			final JPopupMenu markerPopupMenu = new JPopupMenu();
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode();
			FieldMarker fieldMarker = persistMarker ? game.getFieldModel().getFieldMarker(pCoordinate) : game.getFieldModel().getTransientFieldMarker(pCoordinate);
			String markerText = (fieldMarker != null) ? fieldMarker.getHomeText() : null;
			PitchDimensionProvider dimensionProvider = pClient.getUserInterface().getPitchDimensionProvider();
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate.getX(), pCoordinate.getY(), false);

			final JTextField markerField = createMarkerPopup(pClient.getUserInterface().getFieldComponent(), markerPopupMenu,
				"Mark Field", StringTool.print(markerText), dimension.width, dimension.height, dimensionProvider, false).textField;
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

	private PopupComponents createMarkerPopup(FieldComponent pFieldComponent, JPopupMenu pPopupMenu, String pTitle,
			String pMarkerText, int pX, int pY, DimensionProvider dimensionProvider, boolean includeMode) {
		if (StringTool.isProvided(pTitle)) {
			pPopupMenu.add(new JLabel(dimensionProvider, pTitle));
		}
		pPopupMenu.setLayout(new BoxLayout(pPopupMenu, BoxLayout.X_AXIS));
		pPopupMenu.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JTextField markerField = new JTextField(dimensionProvider, 7);
		if (StringTool.isProvided(pMarkerText)) {
			markerField.setText(pMarkerText);
		}
		pPopupMenu.add(Box.createHorizontalStrut(5));
		pPopupMenu.add(markerField);

		JComboBox<TransientPlayerMarker.Mode> box = null;
		if (includeMode) {
			box = new JComboBox<>(dimensionProvider, TransientPlayerMarker.Mode.values());
			box.setSelectedItem(defaultMode);
			pPopupMenu.add(box);
		}

		pPopupMenu.show(pFieldComponent, pX, pY);
		markerField.selectAll();
		markerField.requestFocus();

		return new PopupComponents(markerField, box);
	}

	private static class PopupComponents {
		private final JTextField textField;
		private final JComboBox<TransientPlayerMarker.Mode> modeBox;

		public PopupComponents(JTextField textField, JComboBox<TransientPlayerMarker.Mode> modeBox) {
			this.textField = textField;
			this.modeBox = modeBox;
		}
	}

}
