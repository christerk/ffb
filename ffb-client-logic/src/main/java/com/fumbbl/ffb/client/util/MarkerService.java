package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.marking.TransientPlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;


public class MarkerService {

	private TransientPlayerMarker.Mode defaultMode = TransientPlayerMarker.Mode.APPEND;

	public void showMarkerPopup(final FantasyFootballClient pClient, Component source, final Player<?> pPlayer, int pX, int pY) {
		if (pPlayer != null) {
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode() && !IClientPropertyValue.AUTO_MARKING.contains(pClient.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE));
			PlayerMarker playerMarker = persistMarker ? pClient.getGame().getFieldModel().getPlayerMarker(pPlayer.getId()) : pClient.getGame().getFieldModel().getTransientPlayerMarker(pPlayer.getId());
			String markerText = (playerMarker != null) ? playerMarker.getHomeText() : null;
			final JTextField markerField = createMarkerPopup(pClient.getUserInterface(), source, "Mark Player", StringTool.print(markerText), pX, pY, !persistMarker);
			markerField.addActionListener(pActionEvent -> {
				String text = StringTool.print(markerField.getText());
				if (persistMarker) {
					pClient.getCommunication().sendSetMarker(pPlayer.getId(), text);
				} else {
					if (StringTool.isProvided(text)) {
						TransientPlayerMarker transientMarker = new TransientPlayerMarker(pPlayer.getId(), defaultMode);
						transientMarker.setHomeText(text);
						pClient.getGame().getFieldModel().addTransient(transientMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(transientMarker);
					} else {
						pClient.getGame().getFieldModel().removeTransient((TransientPlayerMarker) playerMarker);
						pClient.getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(playerMarker);
					}
					pClient.getUserInterface().refresh();
				}
			});
		}
	}

	public void showMarkerPopup(final FantasyFootballClient pClient, Component source, final FieldCoordinate pCoordinate, int pX, int pY) {
		if (pCoordinate != null) {
			Game game = pClient.getGame();
			boolean persistMarker = ClientMode.PLAYER == pClient.getMode();
			FieldMarker fieldMarker = persistMarker ? game.getFieldModel().getFieldMarker(pCoordinate) : game.getFieldModel().getTransientFieldMarker(pCoordinate);
			String markerText = (fieldMarker != null) ? fieldMarker.getHomeText() : null;

			final JTextField markerField = createMarkerPopup(pClient.getUserInterface(), source, "Mark Field", StringTool.print(markerText), pX, pY, false);
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
			});
		}
	}

	private JTextField createMarkerPopup(UserInterface ui, Component source, String pTitle, String pMarkerText, int pX, int pY, boolean includeMode) {

		DimensionProvider dimensionProvider = ui.getPitchDimensionProvider();

		JDialog pPopupMenu = new JDialog(ui);

		JPanel spacerPanel = new JPanel();
		spacerPanel.setLayout(new BoxLayout(spacerPanel, BoxLayout.Y_AXIS));
		spacerPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		if (StringTool.isProvided(pTitle)) {
			JLabel comp = new JLabel(dimensionProvider, pTitle);
			comp.setAlignmentX(JPanel.LEFT_ALIGNMENT);
			JPanel panel = new JPanel();
			panel.add(comp);
			spacerPanel.add(panel);
		}
		JTextField markerField = new JTextField(dimensionProvider, 7);
		if (StringTool.isProvided(pMarkerText)) {
			markerField.setText(pMarkerText);
		}
		markerField.addActionListener(pActionEvent -> {
			if (pPopupMenu.isVisible()) {
				pPopupMenu.setVisible(false);
			}
		});

		spacerPanel.add(markerField);

		if (includeMode) {

			JComboBox<TransientPlayerMarker.Mode> modeComboBox = new JComboBox<>(dimensionProvider, TransientPlayerMarker.Mode.values());
			modeComboBox.setRenderer(new MarkerCellRenderer(dimensionProvider));
			modeComboBox.setSelectedItem(defaultMode);
			modeComboBox.addActionListener(pActionEvent -> {
				defaultMode = modeComboBox.getSelectedItem();
				markerField.requestFocus();
			});

			spacerPanel.add(modeComboBox);
		}
		pPopupMenu.add(spacerPanel);

		pPopupMenu.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				pPopupMenu.setVisible(false);
			}
		});
		pPopupMenu.setUndecorated(true);
		pPopupMenu.pack();
		Dimension offset = offset(ui, source, dimensionProvider);
		int componentOffsetX = offset.width + ui.getX();
		int componentOffsetY = offset.height + ui.getY();

		pPopupMenu.setLocation(pX + componentOffsetX, pY + componentOffsetY);
		pPopupMenu.setVisible(true);
		markerField.selectAll();
		markerField.requestFocus();

		return markerField;
	}

	private Dimension offset(UserInterface ui, Component source, DimensionProvider dimensionProvider) {
		Dimension dimension = new Dimension(0, ui.getGameMenuBar().getHeight() + LayoutSettings.TITLE_BAR_HEIGHT);

		if (source == ui.getFieldComponent()) {
			dimension.width = (int) dimensionProvider.dimension(com.fumbbl.ffb.client.Component.SIDEBAR).getWidth();
		} else if (source == ui.getSideBarAway()) {
			dimension.width = (int) (dimensionProvider.dimension(com.fumbbl.ffb.client.Component.SIDEBAR).getWidth() + dimensionProvider.dimension(com.fumbbl.ffb.client.Component.FIELD).getWidth());
		}
		return dimension;
	}

	private static class MarkerCellRenderer extends JPanel implements ListCellRenderer<TransientPlayerMarker.Mode> {

		private final JLabel label;

		public MarkerCellRenderer(DimensionProvider dimensionProvider) {
			label = new JLabel(dimensionProvider);
			add(label);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends TransientPlayerMarker.Mode> list, TransientPlayerMarker.Mode value, int index, boolean isSelected, boolean cellHasFocus) {
			label.setText(value.getDisplayText());
			return this;
		}
	}
}
