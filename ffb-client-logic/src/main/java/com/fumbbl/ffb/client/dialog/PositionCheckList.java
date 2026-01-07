package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.model.RosterPosition;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PositionCheckList extends JList<PositionCheckListItem> {

	// Handles rendering cells in the list using a checkbox

	public PositionCheckList(DimensionProvider dimensionProvider, List<RosterPosition> positions, int minSelects,
												int maxSelects, boolean preSelected, JButton selectButton) {

		if (positions == null || positions.isEmpty()) {
			throw new IllegalArgumentException("Argument positions must not be empty or null.");
		}

		List<PositionCheckListItem> checkListItems = new ArrayList<>();
		for (RosterPosition position : positions) {
			PositionCheckListItem checkListItem = new PositionCheckListItem(position);
			checkListItem.setSelected((positions.size() == 1) || preSelected);
			checkListItems.add(checkListItem);

		}
		setListData(checkListItems.toArray(new PositionCheckListItem[0]));

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new PositionCheckListRenderer(dimensionProvider));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection
		addMouseListener(new PositionCheckListMouseAdapter(minSelects, maxSelects, selectButton));
	}

	public RosterPosition[] getSelectedPositions() {
		List<RosterPosition> selectedPositions = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			PositionCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedPositions.add(item.getPosition());
			}
		}
		return selectedPositions.toArray(new RosterPosition[0]);
	}

	private int findNrOfSelectedItems() {
		int nrOfSelectedItems = 0;
		for (int i = 0; i < getModel().getSize(); i++) {
			PositionCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				nrOfSelectedItems++;
			}
		}
		return nrOfSelectedItems;
	}

	private static class PositionCheckListRenderer extends JPanel implements ListCellRenderer<PositionCheckListItem> {

		private final JCheckBox fCheckBox;
		private final JLabel fLabel;

		public PositionCheckListRenderer(DimensionProvider dimensionProvider) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			fCheckBox = new JCheckBox(dimensionProvider);
			add(fCheckBox);
			fLabel = new JLabel(dimensionProvider);
			add(fLabel);
		}

		public Component getListCellRendererComponent(JList<? extends PositionCheckListItem> pList,
			PositionCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
			setEnabled(pList.isEnabled());
			setFont(pList.getFont());
			setBackground(pList.getBackground());
			setForeground(pList.getForeground());
			fCheckBox.setBackground(pList.getBackground());
			fCheckBox.setSelected(pValue.isSelected());
			fLabel.setText(pValue.getText());
			return this;
		}
	}

	private class PositionCheckListMouseAdapter extends MouseAdapter {

		private final int fMinSelects;
		private final int fMaxSelects;
		private final JButton fSelectButton;

		public PositionCheckListMouseAdapter(int minSelects, int maxSelects, JButton selectButton) {
			fMinSelects = minSelects;
			fMaxSelects = maxSelects;
			fSelectButton = selectButton;
		}

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent event) {
			JList<PositionCheckListItem> list = (JList<PositionCheckListItem>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			PositionCheckListItem selectedItem = list.getModel().getElementAt(index);
			if (!selectedItem.isSelected()) {
				if (fMaxSelects > 1) {
					int nrOfSelectedItems = findNrOfSelectedItems();
					if (nrOfSelectedItems < fMaxSelects) {
						selectedItem.setSelected(true);
					}
				} else {
					for (int i = 0; i < list.getModel().getSize(); i++) {
						PositionCheckListItem item = list.getModel().getElementAt(i);
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

}
