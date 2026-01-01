package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.model.Keyword;

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

public class KeywordCheckList extends JList<KeywordCheckListItem> {

	// Handles rendering cells in the list using a checkbox

	public KeywordCheckList(DimensionProvider dimensionProvider, List<Keyword> keywords, int minSelects,
												int maxSelects, boolean preSelected, JButton selectButton) {

		if (keywords == null || keywords.isEmpty()) {
			throw new IllegalArgumentException("Argument keywords must not be empty or null.");
		}

		List<KeywordCheckListItem> checkListItems = new ArrayList<>();
		for (Keyword keyword : keywords) {
			KeywordCheckListItem checkListItem = new KeywordCheckListItem(keyword);
			checkListItem.setSelected((keywords.size() == 1) || preSelected);
			checkListItems.add(checkListItem);

		}
		setListData(checkListItems.toArray(new KeywordCheckListItem[0]));

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new KeywordCheckListRenderer(dimensionProvider));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection
		addMouseListener(new KeywordCheckListMouseAdapter(minSelects, maxSelects, selectButton));
	}

	public Keyword[] getSelectedKeywords() {
		List<Keyword> selectedKeywords = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			KeywordCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedKeywords.add(item.getKeyword());
			}
		}
		return selectedKeywords.toArray(new Keyword[0]);
	}
	private int findNrOfSelectedItems() {
		int nrOfSelectedItems = 0;
		for (int i = 0; i < getModel().getSize(); i++) {
			KeywordCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				nrOfSelectedItems++;
			}
		}
		return nrOfSelectedItems;
	}

	private static class KeywordCheckListRenderer extends JPanel implements ListCellRenderer<KeywordCheckListItem> {

		private final JCheckBox fCheckBox;
		private final JLabel fLabel;

		public KeywordCheckListRenderer(DimensionProvider dimensionProvider) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			fCheckBox = new JCheckBox(dimensionProvider);
			add(fCheckBox);
			fLabel = new JLabel(dimensionProvider);
			add(fLabel);
		}

		public Component getListCellRendererComponent(JList<? extends KeywordCheckListItem> pList,
			KeywordCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
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

	private class KeywordCheckListMouseAdapter extends MouseAdapter {

		private final int fMinSelects;
		private final int fMaxSelects;
		private final JButton fSelectButton;

		public KeywordCheckListMouseAdapter(int minSelects, int maxSelects, JButton selectButton) {
			fMinSelects = minSelects;
			fMaxSelects = maxSelects;
			fSelectButton = selectButton;
		}

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent event) {
			JList<KeywordCheckListItem> list = (JList<KeywordCheckListItem>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			KeywordCheckListItem selectedItem = list.getModel().getElementAt(index);
			if (!selectedItem.isSelected()) {
				if (fMaxSelects > 1) {
					int nrOfSelectedItems = findNrOfSelectedItems();
					if (nrOfSelectedItems < fMaxSelects) {
						selectedItem.setSelected(true);
					}
				} else {
					for (int i = 0; i < list.getModel().getSize(); i++) {
						KeywordCheckListItem item = list.getModel().getElementAt(i);
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
