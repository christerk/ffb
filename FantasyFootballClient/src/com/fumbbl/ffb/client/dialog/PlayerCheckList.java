package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlayerCheckList extends JList<PlayerCheckListItem> {

	// Handles rendering cells in the list using a checkbox

	public PlayerCheckList(FantasyFootballClient client, String[] playerIds, String[] descriptions, int minSelects,
			int maxSelects, boolean preSelected, JButton selectButton) {

		if (!ArrayTool.isProvided(playerIds)) {
			throw new IllegalArgumentException("Argument players must not be empty or null.");
		}

		Game game = client.getGame();
		List<PlayerCheckListItem> checkListItems = new ArrayList<>();
		PlayerIconFactory playerIconFactory = client.getUserInterface().getPlayerIconFactory();
		for (int i = 0; i < playerIds.length; i++) {
			Player<?> player = game.getPlayerById(playerIds[i]);
			if (player != null) {
				boolean homePlayer = game.getTeamHome().hasPlayer(player);
				BufferedImage playerIcon = playerIconFactory.getBasicIcon(client, player, homePlayer, false, false, false);
				StringBuilder text = new StringBuilder();
				text.append(player.getName());
				if (ArrayTool.isProvided(descriptions)) {
					int descriptionIndex = i;
					if (descriptionIndex >= descriptions.length) {
						descriptionIndex = descriptions.length - 1;
					}
					text.append(" ").append(descriptions[descriptionIndex]);
				}
				PlayerCheckListItem checkListItem = new PlayerCheckListItem(player, new ImageIcon(playerIcon), text.toString());
				checkListItem.setSelected((playerIds.length == 1) || preSelected);
				checkListItems.add(checkListItem);
			}
		}
		setListData(checkListItems.toArray(new PlayerCheckListItem[0]));

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new PlayerCheckListRenderer(client.getUserInterface().getDimensionProvider()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection
		addMouseListener(new PlayerCheckListMouseAdapter(minSelects, maxSelects, selectButton));

	}

	private static class PlayerCheckListRenderer extends JPanel implements ListCellRenderer<PlayerCheckListItem> {

		private final JCheckBox fCheckBox;
		private final JLabel fLabel;

		public PlayerCheckListRenderer(DimensionProvider dimensionProvider) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			fCheckBox = new JCheckBox();
			add(fCheckBox);
			fLabel = new JLabel(dimensionProvider);
			add(fLabel);
		}

		public Component getListCellRendererComponent(JList<? extends PlayerCheckListItem> pList,
				PlayerCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
			setEnabled(pList.isEnabled());
			setFont(pList.getFont());
			setBackground(pList.getBackground());
			setForeground(pList.getForeground());
			fCheckBox.setBackground(pList.getBackground());
			fCheckBox.setSelected(pValue.isSelected());
			fLabel.setIcon(pValue.getIcon());
			fLabel.setText(pValue.getText());
			return this;
		}
	}

	private class PlayerCheckListMouseAdapter extends MouseAdapter {

		private final int fMinSelects;
		private final int fMaxSelects;
		private final JButton fSelectButton;

		public PlayerCheckListMouseAdapter(int minSelects, int maxSelects, JButton selectButton) {
			fMinSelects = minSelects;
			fMaxSelects = maxSelects;
			fSelectButton = selectButton;
		}

		public void mouseReleased(MouseEvent event) {
			JList<?> list = (JList<?>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			PlayerCheckListItem selectedItem = (PlayerCheckListItem) list.getModel().getElementAt(index);
			if (!selectedItem.isSelected()) {
				if (fMaxSelects > 1) {
					int nrOfSelectedItems = findNrOfSelectedItems();
					if (nrOfSelectedItems < fMaxSelects) {
						selectedItem.setSelected(true);
					}
				} else {
					for (int i = 0; i < list.getModel().getSize(); i++) {
						PlayerCheckListItem item = (PlayerCheckListItem) list.getModel().getElementAt(i);
						if (item.isSelected()) {
							item.setSelected(false);
							list.repaint(list.getCellBounds(i, i));
						}
					}
					selectedItem.setSelected(true);
				}
			} else {
				selectedItem.setSelected(false);
			}
			int nrOfSelectedItems = findNrOfSelectedItems();
			if (fMinSelects > 0) {
				fSelectButton.setEnabled(nrOfSelectedItems >= fMinSelects);
			} else {
				fSelectButton.setEnabled(nrOfSelectedItems > 0);
			}
			list.repaint(list.getCellBounds(index, index));
		}

	}

	public Player<?> getPlayerAtIndex(int pIndex) {
		PlayerCheckListItem checkListItem = getModel().getElementAt(pIndex);
		if (checkListItem != null) {
			return checkListItem.getPlayer();
		} else {
			return null;
		}
	}

	public Player<?>[] getSelectedPlayers() {
		List<Player<?>> selectedPlayers = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			PlayerCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedPlayers.add(item.getPlayer());
			}
		}
		return selectedPlayers.toArray(new Player[0]);
	}

	public List<Integer> getSelectedIndexes() {
		List<Integer> selectedIndexes = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			PlayerCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedIndexes.add(i);
			}
		}

		return selectedIndexes;
	}

	private int findNrOfSelectedItems() {
		int nrOfSelectedItems = 0;
		for (int i = 0; i < getModel().getSize(); i++) {
			PlayerCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				nrOfSelectedItems++;
			}
		}
		return nrOfSelectedItems;
	}

}
